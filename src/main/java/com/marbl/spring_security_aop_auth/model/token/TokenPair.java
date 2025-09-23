package com.marbl.spring_security_aop_auth.model.token;

public record TokenPair(String rawToken, String tokenHash) {
    }