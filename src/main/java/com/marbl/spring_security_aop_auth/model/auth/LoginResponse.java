package com.marbl.spring_security_aop_auth.model.auth;

import java.util.Date;


public record LoginResponse(String token, Date expiresAt) {
}
