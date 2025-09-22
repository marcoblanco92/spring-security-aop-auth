package com.marbl.spring_security_aop_auth.controller.user;

import com.marbl.spring_security_aop_auth.dto.user.RegisterUserDto;
import com.marbl.spring_security_aop_auth.service.user.UsersService;
import com.marbl.spring_security_aop_auth.utils.PrivacyUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UsersService usersService;

    @PostMapping
    @Operation(tags = "Users", description = "Register a new user", summary = "We are able to register a new user")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {@Content(mediaType = "application/json", schema = @Schema(example = "Error message"))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(example = "Error message")))})
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserDto registerUserDto) {

        // Log request safely
        String maskedEmail = PrivacyUtils.maskEmail(registerUserDto.getEmail());
        log.info("Registering new user: {}, email: {}", registerUserDto.getUsername(), maskedEmail);

        usersService.registerUser(registerUserDto);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created, no body
    }
}
