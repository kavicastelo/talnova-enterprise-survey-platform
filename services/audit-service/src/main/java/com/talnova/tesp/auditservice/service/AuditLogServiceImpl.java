package com.talnova.tesp.auditservice.service;

import com.talnova.tesp.auditservice.domain.model.AuditLogDocument;
import com.talnova.tesp.auditservice.dto.AuditLogRequestDTO;
import com.talnova.tesp.auditservice.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLogDocument recordAuditLog(AuditLogRequestDTO request) {
        String auditId = "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Recording append-only audit log entry '{}' for actor '{}', action '{}'",
                auditId, request.getActorId(), request.getAction());

        AuditLogDocument doc = AuditLogDocument.builder()
                .auditId(auditId)
                .projectId(request.getProjectId())
                .actorId(request.getActorId())
                .userRole(request.getUserRole())
                .action(request.getAction())
                .resourceId(request.getResourceId())
                .timestamp(Instant.now())
                .details(request.getDetails())
                .build();

        AuditLogDocument saved = auditLogRepository.save(doc);
        log.info("Audit log entry '{}' successfully persisted to tesp_audit_db.audit_logs per BR-SEC-012", auditId);
        return saved;
    }

    @Override
    public Optional<AuditLogDocument> getAuditLogById(String auditId) {
        return auditLogRepository.findByAuditId(auditId);
    }

    @Override
    public Page<AuditLogDocument> getAuditLogsByProject(String projectId, String actorId, String action, Pageable pageable) {
        if (actorId != null && !actorId.isBlank()) {
            return auditLogRepository.findByProjectIdAndActorId(projectId, actorId, pageable);
        }
        if (action != null && !action.isBlank()) {
            return auditLogRepository.findByProjectIdAndAction(projectId, action, pageable);
        }
        return auditLogRepository.findByProjectId(projectId, pageable);
    }
}
