package com.marbl.spring_security_aop_auth.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the new user", example = "marcoblanco")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long, contain uppercase, lowercase, number, and special character"
    )
    @Schema(description = "Password of the new user", example = "P@ssw0rd123")
    private String password;

    @NotBlank
    @Email(message = "Invalid email format")
    @Schema(description = "Email of the new user", example = "marco@example.com")
    private String email;
}
