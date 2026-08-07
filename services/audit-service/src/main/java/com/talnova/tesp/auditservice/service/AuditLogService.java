package com.talnova.tesp.auditservice.service;

import com.talnova.tesp.auditservice.domain.model.AuditLogDocument;
import com.talnova.tesp.auditservice.dto.AuditLogRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuditLogService {

    AuditLogDocument recordAuditLog(AuditLogRequestDTO request);

    Optional<AuditLogDocument> getAuditLogById(String auditId);

    Page<AuditLogDocument> getAuditLogsByProject(String projectId, String actorId, String action, Pageable pageable);
}
