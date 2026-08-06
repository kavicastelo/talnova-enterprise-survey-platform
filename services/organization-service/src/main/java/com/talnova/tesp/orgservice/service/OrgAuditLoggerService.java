package com.talnova.tesp.orgservice.service;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.orgservice.domain.AuditLogDocument;
import com.talnova.tesp.orgservice.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class OrgAuditLoggerService {

    private static final Logger log = LoggerFactory.getLogger(OrgAuditLoggerService.class);

    private final AuditLogRepository auditLogRepository;

    public OrgAuditLoggerService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logOrgEvent(String projectId, String action, String resourceId, Map<String, Object> metadata) {
        try {
            String performedBy = ProjectContextHolder.getUserId() != null ? ProjectContextHolder.getUserId() : "SYSTEM";
            String correlationId = ProjectContextHolder.getCorrelationId() != null ? ProjectContextHolder.getCorrelationId() : "CORR-UNTRACKED";

            AuditLogDocument auditLog = AuditLogDocument.builder()
                    .projectId(projectId)
                    .action(action)
                    .resourceId(resourceId)
                    .performedBy(performedBy)
                    .correlationId(correlationId)
                    .metadata(metadata)
                    .timestamp(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Recorded audit log for action '{}' on resource '{}' by user '{}'", action, resourceId, performedBy);
        } catch (Exception ex) {
            log.error("Failed to write audit log entry: {}", ex.getMessage(), ex);
        }
    }
}
