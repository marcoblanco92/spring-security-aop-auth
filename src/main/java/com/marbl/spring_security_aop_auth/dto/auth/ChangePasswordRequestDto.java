package com.marbl.spring_security_aop_auth.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ChangePasswordRequestDto extends BaseAuthRequestDto {

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long, contain uppercase, lowercase, number, and special character"
    )
    @Schema(description = "Old Password of the user", example = "P@ssw0rd123")
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long, contain uppercase, lowercase, number, and special character"
    )
    @Schema(description = "New Password of the user", example = "P@ssw0rd789")
    private String newPassword;
}
