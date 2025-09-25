package com.marbl.spring_security_aop_auth.repository.token;

import com.marbl.spring_security_aop_auth.entity.token.PasswordResetToken;
import com.marbl.spring_security_aop_auth.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findAllByUserAndUsedFalseAndExpiresAtAfter(User user, LocalDateTime expiresAt);

    Optional<PasswordResetToken> findByTokenHashAndUsedFalseAndExpiresAtAfter(String tokenHash, LocalDateTime now);

    Optional<PasswordResetToken> findByTokenHashAndUsedTrue(String tokenHash);
}
