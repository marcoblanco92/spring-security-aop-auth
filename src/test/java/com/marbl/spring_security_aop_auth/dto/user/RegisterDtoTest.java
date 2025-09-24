package com.marbl.spring_security_aop_auth.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidDto_thenNoViolations() {
        RegisterDto dto = new RegisterDto(
                "marco",
                "Password1!",
                "marco@example.com"
        );

        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenPasswordTooWeak_thenViolation() {
        RegisterDto dto = new RegisterDto(
                "marco",
                "password", // no uppercase, no digit, no special char
                "marco@example.com"
        );

        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whenEmailInvalid_thenViolation() {
        RegisterDto dto = new RegisterDto(
                "marco",
                "Password1!",
                "not-an-email"
        );

        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenUsernameBlank_thenViolation() {
        RegisterDto dto = new RegisterDto(
                "",
                "Password1!",
                "marco@example.com"
        );

        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }
}