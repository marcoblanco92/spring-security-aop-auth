package com.marbl.spring_security_aop_auth.dto.auth;

import com.marbl.spring_security_aop_auth.annotation.ExactlyOneField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ExactlyOneField
@NoArgsConstructor
@AllArgsConstructor
public class BaseAuthRequestDto {

    @Schema(description = "Username of the user", example = "marcoblanco")
    private String username;

    @Email(message = "Invalid email format")
    @Schema(description = "Email of the user", example = "marco@example.com")
    private String email;
}
