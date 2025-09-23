package com.marbl.spring_security_aop_auth.component.kafka;

import com.marbl.spring_security_aop_auth.entity.user.Users;
import com.marbl.spring_security_aop_auth.model.kafka.EmailEvent;
import com.marbl.spring_security_aop_auth.model.token.TokenPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEmailProducer {

    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;
    private static final String TOPIC = "email-topic";

    public void sendEmail(EmailEvent emailEvent) {
        try {
            kafkaTemplate.send(TOPIC, emailEvent);
            log.info("EmailEvent sent to Kafka: {}", emailEvent);
        } catch (Exception e) {
            log.error("Failed to send EmailEvent: {}", emailEvent, e);
        }
    }

    public static EmailEvent createEmailEvent(Users users, TokenPair tokenPair) {
        return EmailEvent.builder()
                .to(users.getEmail())
                .subject("Reset your password")
                .body(buildResetPasswordBody(tokenPair.rawToken()))
                .build();
    }

    private static String buildResetPasswordBody(String token) {
        String resetUrl = "https://frontend-app/reset-password?token=" + token;

        return """
                Hello,
                
                We received a request to reset your password.
                Click the link below to reset your password (valid for 15 minutes):
                
                %s
                
                If you did not request a password reset, please ignore this email.
                
                Best regards,
                MarBl Team
                """.formatted(resetUrl);
    }
}