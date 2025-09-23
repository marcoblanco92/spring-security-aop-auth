package com.marbl.spring_security_aop_auth.validator;

import com.marbl.spring_security_aop_auth.annotation.ExactlyOneField;
import com.marbl.spring_security_aop_auth.dto.auth.LoginRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExactlyOneFieldValidator implements ConstraintValidator<ExactlyOneField, LoginRequestDto> {

    @Override
    public boolean isValid(LoginRequestDto dto, ConstraintValidatorContext context) {
        boolean hasUsername = dto.getUsername() != null && !dto.getUsername().isBlank();
        boolean hasEmail = dto.getEmail() != null && !dto.getEmail().isBlank();

        // Validation passes only if exactly one field is provided (XOR logic)
        return hasUsername ^ hasEmail;
    }
}