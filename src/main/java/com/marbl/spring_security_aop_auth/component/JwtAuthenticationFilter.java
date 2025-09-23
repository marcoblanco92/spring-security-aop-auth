package com.marbl.spring_security_aop_auth.component;

import com.marbl.spring_security_aop_auth.mapper.auth.JwtAuthenticationToken;
import com.marbl.spring_security_aop_auth.service.blacklist.TokenBlacklistService;
import com.marbl.spring_security_aop_auth.utils.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            try {
                // Check on blacklist
                if (tokenBlacklistService.isBlacklisted(token)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                // Validate token
                jwtTokenProvider.validateToken(token);

                List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                Authentication auth = new JwtAuthenticationToken(
                        jwtTokenProvider.getSubject(token),
                        token,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
