package com.marbl.spring_security_aop_auth.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String generateToken(String subject, List<String> roles) {
        return JWT.create()
                .withSubject(subject) // sub
                .withClaim("roles", roles) // custom claim
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(algorithm());
    }

    public DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm()).build();
        return verifier.verify(token);
    }

    public String getSubject(String token) {
        return validateToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return validateToken(token).getClaim("roles").asList(String.class);
    }

    public Date getExpiresAt(String token) {
        return validateToken(token).getExpiresAt();
    }

    public boolean isExpired(String token) {
        return validateToken(token).getExpiresAt().before(new Date());
    }


}
