package com.talnova.tesp.configservice.service;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.configservice.domain.AuditLogDocument;
import com.talnova.tesp.configservice.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditLoggerService {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggerService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLoggerService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAuditEvent(String projectId, String action, String details, String clientIp) {
        try {
            String userId = ProjectContextHolder.getUserId() != null ? ProjectContextHolder.getUserId() : "SYSTEM";
            String correlationId = ProjectContextHolder.getCorrelationId();

            AuditLogDocument auditLog = AuditLogDocument.builder()
                    .auditId("AUD-" + UUID.randomUUID().toString().substring(0, 8))
                    .projectId(projectId)
                    .userId(userId)
                    .action(action)
                    .ipAddress(clientIp != null ? clientIp : "127.0.0.1")
                    .correlationId(correlationId)
                    .details(details)
                    .timestamp(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit log recorded [{}] action={} actor={} projectId={}", auditLog.getAuditId(), action, userId, projectId);
        } catch (Exception ex) {
            log.error("Failed to persist audit log for projectId {}: {}", projectId, ex.getMessage());
        }
    }
}
