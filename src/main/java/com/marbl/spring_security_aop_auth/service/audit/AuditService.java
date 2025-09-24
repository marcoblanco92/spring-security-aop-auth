package com.marbl.spring_security_aop_auth.service.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.marbl.spring_security_aop_auth.dto.audit.AuditDto;

public interface AuditService {
    void saveAudit(AuditDto before, AuditDto after, String action) throws JsonProcessingException;
}