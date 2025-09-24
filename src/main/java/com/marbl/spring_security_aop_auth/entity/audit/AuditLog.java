package com.marbl.spring_security_aop_auth.entity.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.marbl.spring_security_aop_auth.mapper.entity.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log", schema = "audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Column(name = "username")
    private String username;

    @Column(name = "method", nullable = false)
    private String method;

    @Builder.Default
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "before_state", columnDefinition = "TEXT")
    private JsonNode beforeState;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "after_state", columnDefinition = "TEXT")
    private JsonNode afterState;

    @Column(name = "success", nullable = false)
    @Builder.Default
    private boolean success = true;
}