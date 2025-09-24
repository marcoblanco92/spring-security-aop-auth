package com.marbl.spring_security_aop_auth.dto.audit;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class AuditDto {

    private String correlationId;
    private String usernameOrEmail;
    private List<String> rolesBefore;
    private List<String> rolesAfter;
    private String passwordHashBefore;
    private String passwordHashAfter;
    private String token;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String action;
}
