package com.marbl.spring_security_aop_auth.repository.audit;

import com.marbl.spring_security_aop_auth.entity.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
