package com.marbl.spring_security_aop_auth.validator;

import com.marbl.spring_security_aop_auth.annotation.AtLeastOneField;
import com.marbl.spring_security_aop_auth.dto.user.LoginRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, LoginRequestDto> {

    @Override
    public boolean isValid(LoginRequestDto request, ConstraintValidatorContext context) {
        return (request.getUsername() != null && !request.getUsername().isBlank())
            || (request.getEmail() != null && !request.getEmail().isBlank());
    }
}