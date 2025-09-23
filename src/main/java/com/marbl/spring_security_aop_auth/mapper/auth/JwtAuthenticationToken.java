package com.marbl.spring_security_aop_auth.mapper.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String token; //original token

    public JwtAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}