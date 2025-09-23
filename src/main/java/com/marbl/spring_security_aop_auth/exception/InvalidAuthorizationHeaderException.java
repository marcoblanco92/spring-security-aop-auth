package com.marbl.spring_security_aop_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidAuthorizationHeaderException extends ResponseStatusException {

    public InvalidAuthorizationHeaderException() {
        super(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
    }
}