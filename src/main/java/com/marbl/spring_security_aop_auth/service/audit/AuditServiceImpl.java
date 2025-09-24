package com.marbl.spring_security_aop_auth.service.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marbl.spring_security_aop_auth.dto.audit.AuditDto;
import com.marbl.spring_security_aop_auth.entity.audit.AuditLog;
import com.marbl.spring_security_aop_auth.repository.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final ObjectMapper objectMapper;
    private final AuditLogRepository auditLogRepository;

    @Override
    public void saveAudit(AuditDto before, AuditDto after, String action) throws JsonProcessingException {

        var log = AuditLog.builder()
                .correlationId(before.getCorrelationId())
                .username(before.getUsernameOrEmail())
                .method(action)
                .beforeState(objectMapper.valueToTree(before))
                .afterState(objectMapper.valueToTree(after))
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}