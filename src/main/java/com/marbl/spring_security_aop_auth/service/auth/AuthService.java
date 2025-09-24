package com.marbl.spring_security_aop_auth.service.auth;

import com.marbl.spring_security_aop_auth.dto.auth.ChangePasswordRequestDto;
import com.marbl.spring_security_aop_auth.dto.auth.LoginRequestDto;
import com.marbl.spring_security_aop_auth.entity.user.Users;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import com.marbl.spring_security_aop_auth.service.blacklist.TokenBlacklistService;
import com.marbl.spring_security_aop_auth.utils.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.marbl.spring_security_aop_auth.utils.PrivacyUtils.maskSensitive;
import static com.marbl.spring_security_aop_auth.utils.PrivacyUtils.maskUsername;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;


    @Transactional
    public UserDetails authenticate(LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );
            log.info("Authentication successful for user/email: {}", maskSensitive(loginRequestDto));
            resetFailedAttempts(loginRequestDto);
            return (UserDetails) authentication.getPrincipal();
        } catch (BadCredentialsException ex) {
            log.warn("Login failed for user/email: {}", maskSensitive(loginRequestDto));
            increaseFailedAttempts(loginRequestDto);
            throw ex; //error managed from SecurityExceptionHandler
        }
    }

    private void increaseFailedAttempts(LoginRequestDto loginRequestDto) {
        Users user = usersRepository.findByUsernameOrEmail(loginRequestDto.getUsername(), loginRequestDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        int newAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newAttempts);

        usersRepository.save(user);
    }

    private void resetFailedAttempts(LoginRequestDto loginRequestDto) {
        Users user = usersRepository.findByUsernameOrEmail(loginRequestDto.getUsername(), loginRequestDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (user.getFailedAttempts() > 0) {
            user.setFailedAttempts(0);
            usersRepository.save(user);
        }
    }

    public String generateJwtToken(UserDetails user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtTokenProvider.generateToken(user.getUsername(), roles);
        log.info("JWT token generated for user: {} with roles: {}", maskUsername(user.getUsername()), roles);
        return token;
    }

    public void handleLogout(String token) {
        long ttl = jwtTokenProvider.getExpiresAt(token).getTime() - System.currentTimeMillis();
        tokenBlacklistService.blacklist(token, ttl);
        log.info("Token has been blacklisted, TTL: {} ms", ttl);
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDto changePasswordRequest) throws BadRequestException {
        log.info("Change password attempt for user/email: {}", maskSensitive(changePasswordRequest));

        Users user = usersRepository.findByUsernameOrEmail(changePasswordRequest.getUsername(), changePasswordRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Change password failed: user not found for username/email: {}", maskSensitive(changePasswordRequest));
                    return new UsernameNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            log.warn("Change password failed: incorrect old password for user: {}", maskUsername(user.getUsername()));
            throw new BadRequestException("Old password is incorrect");
        }

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            log.warn("Change password failed: new password matches old password for user: {}", maskUsername(user.getUsername()));
            throw new BadRequestException("New password must differ from old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.setFailedAttempts(0);
        usersRepository.save(user);

        log.info("Password successfully changed for user: {}", maskUsername(user.getUsername()));
    }
}
