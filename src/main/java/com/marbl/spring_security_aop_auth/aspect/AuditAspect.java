package com.marbl.spring_security_aop_auth.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.marbl.spring_security_aop_auth.dto.audit.AuditDto;
import com.marbl.spring_security_aop_auth.dto.auth.ChangePasswordRequestDto;
import com.marbl.spring_security_aop_auth.dto.auth.ResetPasswordConfirmRequestDto;
import com.marbl.spring_security_aop_auth.dto.user.RegisterDto;
import com.marbl.spring_security_aop_auth.entity.token.PasswordResetToken;
import com.marbl.spring_security_aop_auth.entity.user.User;
import com.marbl.spring_security_aop_auth.repository.token.PasswordResetTokenRepository;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import com.marbl.spring_security_aop_auth.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final AuditService auditService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final ThreadLocal<AuditDto> beforeState = new ThreadLocal<>();

    @Pointcut("execution(* com.marbl.spring_security_aop_auth.service.user.UsersService.register(..)) || " +
            "execution(* com.marbl.spring_security_aop_auth.service.auth.AuthService.changePassword(..)) || " +
            "execution(* com.marbl.spring_security_aop_auth.service.token.TokenService.confirmResetPassword(..))")
    public void sensitiveOperations() {
    }


    @Before("sensitiveOperations()")
    public void captureBefore(JoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        AuditDto before = null;
        var methodName = joinPoint.getSignature().getName();

        String correlationId = MDC.get("correlationId");

        switch (methodName) {
            case "register" -> {
                var user = (RegisterDto) args[0];
                before = AuditDto.builder()
                        .correlationId(correlationId)
                        .usernameOrEmail(user.getUsername())
                        .rolesBefore(Collections.emptyList())
                        .passwordHashBefore(passwordEncoder.encode(user.getPassword()))
                        .action(methodName)
                        .build();
            }
            case "changePassword" -> {
                var dto = (ChangePasswordRequestDto) args[0];
                before = AuditDto.builder()
                        .correlationId(correlationId)
                        .usernameOrEmail(dto.getUsername())
                        .passwordHashBefore(passwordEncoder.encode(dto.getNewPassword()))
                        .action(methodName)
                        .build();
            }
            case "confirmResetPassword" -> {
                var dto2 = (ResetPasswordConfirmRequestDto) args[0];
                before = AuditDto.builder()
                        .correlationId(correlationId)
                        .token(dto2.getToken())
                        .passwordHashBefore(passwordEncoder.encode(dto2.getPassword()))
                        .action(methodName)
                        .build();
            }
        }
        beforeState.set(before);
    }

    @After("sensitiveOperations()")
    public void captureAfter(JoinPoint joinPoint) throws JsonProcessingException {
        var before = beforeState.get();
        if (before == null) return;

        var methodName = joinPoint.getSignature().getName();
        AuditDto after = null;
        String correlationId = MDC.get("correlationId");


        switch (methodName) {
            case "register" -> {
                var dto = (RegisterDto) joinPoint.getArgs()[0];
                User user = usersRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()).orElse(null);
                if (user == null) {
                    break;
                }
                after = AuditDto.builder()
                        .correlationId(correlationId)
                        .usernameOrEmail(user.getUsername())
                        .rolesAfter(user.getRoles().stream().map(role -> role.getRoleName().name()).toList())
                        .action(methodName)
                        .build();
            }
            case "changePassword" -> {
                var dto = (ChangePasswordRequestDto) joinPoint.getArgs()[0];
                User user = usersRepository.findByUsername(dto.getUsername()).orElse(null);
                if (user == null) {
                    break;
                }
                after = AuditDto.builder()
                        .correlationId(correlationId)
                        .usernameOrEmail(user.getUsername())
                        .passwordHashAfter(user.getPassword())
                        .action(methodName)
                        .build();
            }
            case "confirmResetPassword" -> {
                var dto2 = (ResetPasswordConfirmRequestDto) joinPoint.getArgs()[0];
                User user = passwordResetTokenRepository
                        .findByTokenHashAndUsedTrue(DigestUtils.sha256Hex(dto2.getToken()))
                        .map(PasswordResetToken::getUser)
                        .orElse(null);
                if (user == null) {
                    break;
                }
                after = AuditDto.builder()
                        .correlationId(correlationId)
                        .usernameOrEmail(user.getUsername())
                        .passwordHashAfter(user.getPassword())
                        .action(methodName)
                        .build();
            }
        }

        auditService.saveAudit(before, after, methodName);

        beforeState.remove();
    }
}
