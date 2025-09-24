package com.marbl.spring_security_aop_auth.component.filter;

import com.marbl.spring_security_aop_auth.mapper.auth.JwtAuthenticationToken;
import com.marbl.spring_security_aop_auth.utils.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {

    @Value("${jwt.refresh-ms}")
    private long refreshExpirationMs;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtAuthenticationToken.getToken();
        Date expiresAt = jwtTokenProvider.getExpiresAt(token);

        if (!jwtTokenProvider.isExpiringSoon(expiresAt)) {
            filterChain.doFilter(request, response);
            return;
        }

        String newToken = jwtTokenProvider.generateToken(
                jwtTokenProvider.getSubject(token),
                jwtTokenProvider.getRoles(token),
                refreshExpirationMs
        );

        response.setHeader("Authorization", "Bearer " + newToken);

        JwtAuthenticationToken newAuth = new JwtAuthenticationToken(
                jwtAuthenticationToken.getPrincipal(),
                newToken,
                jwtAuthenticationToken.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        filterChain.doFilter(request, response);
    }
}