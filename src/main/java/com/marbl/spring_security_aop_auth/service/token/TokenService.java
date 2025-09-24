package com.marbl.spring_security_aop_auth.service.token;

import com.marbl.spring_security_aop_auth.component.kafka.KafkaEmailProducer;
import com.marbl.spring_security_aop_auth.dto.auth.ResetPasswordConfirmRequestDto;
import com.marbl.spring_security_aop_auth.dto.auth.ResetPasswordRequestDto;
import com.marbl.spring_security_aop_auth.entity.token.PasswordResetToken;
import com.marbl.spring_security_aop_auth.entity.user.Users;
import com.marbl.spring_security_aop_auth.model.token.TokenPair;
import com.marbl.spring_security_aop_auth.repository.token.PasswordResetTokenRepository;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.marbl.spring_security_aop_auth.utils.PrivacyUtils.maskEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final KafkaEmailProducer kafkaEmailProducer;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    public static TokenPair generateToken() {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = DigestUtils.sha256Hex(rawToken);
        return new TokenPair(rawToken, tokenHash);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        log.info("Hard reset password for email user: {}", maskEmail(resetPasswordRequestDto.getEmail()));
        Users users = usersRepository.findByUsernameOrEmail(null, resetPasswordRequestDto.getEmail()).orElse(null);

        if (users == null) {
            log.warn("Reset password failed: user not found for email: {}", maskEmail(resetPasswordRequestDto.getEmail()));
            return;
        }

        List<PasswordResetToken> tokensToDisable = passwordResetTokenRepository
                .findAllByUserAndUsedFalseAndExpiresAtAfter(users, LocalDateTime.now());

        tokensToDisable.forEach(t -> t.setUsed(true));
        passwordResetTokenRepository.saveAll(tokensToDisable);

        TokenPair tokenPair = TokenService.generateToken();

        PasswordResetToken newToken = PasswordResetToken.builder()
                .user(users)
                .tokenHash(tokenPair.tokenHash())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        passwordResetTokenRepository.save(newToken);

        kafkaEmailProducer.sendEmail(KafkaEmailProducer.createEmailEvent(users, tokenPair));
    }

    @Transactional
    public void confirmResetPassword(ResetPasswordConfirmRequestDto resetPasswordConfirmRequestDto) throws BadRequestException {
        log.info("Starting reset password confirmation");

        String tokenHash = DigestUtils.sha256Hex(resetPasswordConfirmRequestDto.getToken());
        log.debug("Token hash generated for verification");

        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenHashAndUsedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now())
                .orElseThrow(() -> {
                    log.warn("Reset password failed: invalid or expired token");
                    return new BadRequestException("Token invalid or expired");
                });

        Users user = token.getUser();
        log.info("Reset password confirmed for user: {}", maskEmail(user.getEmail()));

        if (passwordEncoder.matches(resetPasswordConfirmRequestDto.getPassword(), user.getPassword())) {
            log.warn("New password matches old password for user: {}", maskEmail(user.getEmail()));
            throw new BadRequestException("New password must differ from old password");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordConfirmRequestDto.getPassword()));
        user.setFailedAttempts(0);
        usersRepository.save(user);
        log.info("User password updated and failed attempts reset for user: {}", maskEmail(user.getEmail()));

        token.setUsed(true);
        passwordResetTokenRepository.save(token);
        log.debug("Token marked as used for user: {}", maskEmail(user.getEmail()));

        log.info("Reset password process completed successfully for user: {}", maskEmail(user.getEmail()));
    }
}
