package com.marbl.spring_security_aop_auth.validator;

import com.marbl.spring_security_aop_auth.annotation.ExactlyOneField;
import com.marbl.spring_security_aop_auth.dto.auth.BaseAuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExactlyOneFieldValidator implements ConstraintValidator<ExactlyOneField, BaseAuthRequestDto> {

    @Override
    public boolean isValid(BaseAuthRequestDto dto, ConstraintValidatorContext context) {
        boolean hasUsername = dto.getUsername() != null && !dto.getUsername().isBlank();
        boolean hasEmail = dto.getEmail() != null && !dto.getEmail().isBlank();

        // Validation passes only if exactly one field is provided (XOR logic)
        return hasUsername ^ hasEmail;
    }
}