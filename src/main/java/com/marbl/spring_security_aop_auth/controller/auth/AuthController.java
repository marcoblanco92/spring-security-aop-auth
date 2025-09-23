package com.marbl.spring_security_aop_auth.controller.auth;

import com.marbl.spring_security_aop_auth.controller.BaseController;
import com.marbl.spring_security_aop_auth.dto.auth.LoginRequestDto;
import com.marbl.spring_security_aop_auth.model.auth.LoginResponse;
import com.marbl.spring_security_aop_auth.service.auth.AuthService;
import com.marbl.spring_security_aop_auth.utils.PrivacyUtils;
import com.marbl.spring_security_aop_auth.utils.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @Operation(tags = "Authentication", description = "Login", summary = "User are able to login")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(example = "Error message"))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(example = "Error message")))})
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        String maskedEmail = PrivacyUtils.maskEmail(loginRequestDto.getEmail());
        log.info("Attempting login for user: {} email:{}", loginRequestDto.getUsername(), maskedEmail);

        UserDetails userDetails = authService.authenticate(loginRequestDto);
        String token = authService.generateJwtToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(token, "Bearer", jwtTokenProvider.getExpiresAt(token)));
    }

    @PostMapping("/logout")
    @Operation(tags = "Authentication", description = "Logout", summary = "User are able to logout")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(example = "Error message"))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(example = "Error message")))})
    public ResponseEntity<Void> logout() {
        authService.handleLogout(getJwtToken());
        return ResponseEntity.ok().build();
    }
}
