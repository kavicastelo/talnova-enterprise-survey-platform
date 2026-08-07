package com.talnova.tesp.auditservice.controller;

import java.util.List;
import com.talnova.tesp.auditservice.domain.model.AuditLogDocument;
import com.talnova.tesp.auditservice.dto.AuditLogRequestDTO;
import com.talnova.tesp.auditservice.dto.AuditLogResponseDTO;
import com.talnova.tesp.auditservice.service.AuditLogService;
import com.talnova.tesp.common.context.ProjectContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditLogController {

    private static final Logger log = LoggerFactory.getLogger(AuditLogController.class);
    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PostMapping("/logs")
    @Operation(summary = "Record append-only audit log entry", description = "Persists an immutable audit log record to tesp_audit_db.audit_logs")
    public ResponseEntity<AuditLogResponseDTO> recordAuditLog(
            @Valid @RequestBody AuditLogRequestDTO request,
            HttpServletRequest servletRequest) {
        String resolvedProjectId = resolveProjectId(request.getProjectId(), servletRequest);
        request.setProjectId(resolvedProjectId);

        log.info("REST Ingress: Recording audit log entry for actor '{}', action '{}'",
                request.getActorId(), request.getAction());

        AuditLogDocument doc = auditLogService.recordAuditLog(request);

        AuditLogResponseDTO response = AuditLogResponseDTO.builder()
                .auditId(doc.getAuditId())
                .projectId(doc.getProjectId())
                .actorId(doc.getActorId())
                .userRole(doc.getUserRole())
                .action(doc.getAction())
                .resourceId(doc.getResourceId())
                .timestamp(doc.getTimestamp())
                .details(doc.getDetails())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/logs")
    @Operation(summary = "Query project audit log history", description = "Fetches paginated audit log entries filtered by actorId or action")
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogs(
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest servletRequest) {
        String resolvedProjectId = resolveProjectId(projectIdParam, servletRequest);

        log.info("REST Ingress: Querying audit logs for projectId '{}', actorId '{}', action '{}'",
                resolvedProjectId, actorId, action);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLogDocument> docPage = auditLogService.getAuditLogsByProject(resolvedProjectId, actorId, action, pageable);

        List<AuditLogResponseDTO> responseList = docPage.getContent().stream()
                .map(doc -> AuditLogResponseDTO.builder()
                        .auditId(doc.getAuditId())
                        .projectId(doc.getProjectId())
                        .actorId(doc.getActorId())
                        .userRole(doc.getUserRole())
                        .action(doc.getAction())
                        .resourceId(doc.getResourceId())
                        .timestamp(doc.getTimestamp())
                        .details(doc.getDetails())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/logs/{auditId}")
    @Operation(summary = "Fetch single audit log record by auditId", description = "Returns detailed audit record per BR-SEC-012")
    public ResponseEntity<AuditLogResponseDTO> getAuditLogById(@PathVariable String auditId) {
        log.info("REST Ingress: Fetching audit log entry for auditId '{}'", auditId);

        AuditLogDocument doc = auditLogService.getAuditLogById(auditId)
                .orElseThrow(() -> new IllegalArgumentException("Audit record not found with ID: " + auditId));

        AuditLogResponseDTO response = AuditLogResponseDTO.builder()
                .auditId(doc.getAuditId())
                .projectId(doc.getProjectId())
                .actorId(doc.getActorId())
                .userRole(doc.getUserRole())
                .action(doc.getAction())
                .resourceId(doc.getResourceId())
                .timestamp(doc.getTimestamp())
                .details(doc.getDetails())
                .build();

        return ResponseEntity.ok(response);
    }

    private String resolveProjectId(String paramProjectId, HttpServletRequest request) {
        String contextProjectId = ProjectContextHolder.getProjectId();
        if (contextProjectId != null && !contextProjectId.isBlank()) {
            return contextProjectId;
        }
        if (request != null) {
            String headerProjectId = request.getHeader("X-Project-ID");
            if (headerProjectId != null && !headerProjectId.isBlank()) {
                return headerProjectId;
            }
        }
        if (paramProjectId != null && !paramProjectId.isBlank()) {
            return paramProjectId;
        }
        return "PRJ-DEFAULT";
    }
}
