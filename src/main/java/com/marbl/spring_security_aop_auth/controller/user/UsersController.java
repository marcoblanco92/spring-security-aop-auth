package com.marbl.spring_security_aop_auth.controller.user;

import com.marbl.spring_security_aop_auth.controller.BaseController;
import com.marbl.spring_security_aop_auth.dto.user.RegisterDto;
import com.marbl.spring_security_aop_auth.entity.role.RolesEnum;
import com.marbl.spring_security_aop_auth.model.error.ErrorResponse;
import com.marbl.spring_security_aop_auth.service.user.UsersService;
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

import static com.marbl.spring_security_aop_auth.utils.PrivacyUtils.maskEmail;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController extends BaseController {

    private final UsersService usersService;

    @PostMapping
    @Operation(tags = "Users", description = "Register a new user", summary = "We are able to register a new user")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterDto registerUserDto) {

        log.info("Registering new user: {}, email: {} for role: {}", registerUserDto.getUsername(), maskEmail(registerUserDto.getEmail()), RolesEnum.ROLE_USER);
        usersService.register(registerUserDto, RolesEnum.ROLE_USER);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created, no body
    }
}
