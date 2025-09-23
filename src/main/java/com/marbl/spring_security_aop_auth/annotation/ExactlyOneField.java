package com.marbl.spring_security_aop_auth.annotation;

import com.marbl.spring_security_aop_auth.validator.ExactlyOneFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExactlyOneFieldValidator.class)
@Documented
public @interface ExactlyOneField {
    String message() default "Provide exactly one between username and email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}