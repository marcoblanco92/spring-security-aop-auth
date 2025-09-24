package com.marbl.spring_security_aop_auth.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.threshold-ms}")
    private long thresholdMillis;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * Generates a JWT with default expiration (used at login)
     */
    public String generateToken(String subject, List<String> roles) {
        return generateToken(subject, roles, expirationMs);
    }

    /**
     * Generates a JWT with custom expiration (used for refresh)
     */
    public String generateToken(String subject, List<String> roles, long customExpirationMs) {
        Date now = new Date();
        return JWT.create()
                .withSubject(subject)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + customExpirationMs))
                .sign(algorithm());
    }

    /**
     * Validates the JWT and returns the decoded token
     */
    public DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm()).build();
        return verifier.verify(token);
    }

    /**
     * Extracts the subject (username) from the token
     */
    public String getSubject(String token) {
        return getDecodedJWT(token).getSubject();
    }

    /**
     * Extracts the roles from the token
     */
    public List<String> getRoles(String token) {
        return getDecodedJWT(token).getClaim("roles").asList(String.class);
    }

    /**
     * Extracts the expiration date from the token
     */
    public Date getExpiresAt(String token) {
        return getDecodedJWT(token).getExpiresAt();
    }

    /**
     * Checks if the token is expired
     */
    public boolean isExpired(String token) {
        return getExpiresAt(token).before(new Date());
    }

    /**
     * Returns an Authentication object for Spring Security
     */
    public Authentication getAuthentication(String token) {
        String username = getSubject(token);
        List<SimpleGrantedAuthority> authorities = getRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * Checks if the token is about to expire within the configured threshold
     */
    public boolean isExpiringSoon(Date expiresAt) {
        if (expiresAt == null) return false;

        Date now = new Date();
        return expiresAt.after(now) && (expiresAt.getTime() - now.getTime() <= thresholdMillis);
    }

    // -------------------------
    // Internal helper
    // -------------------------
    private DecodedJWT getDecodedJWT(String token) {
        // Decodes once per call, could be optimized further by caching per request if needed
        return validateToken(token);
    }
}