package com.marbl.spring_security_aop_auth.service.blacklist;

public interface TokenBlacklistService {
    void blacklist(String token, long ttlMillis);
    boolean isBlacklisted(String token);
}