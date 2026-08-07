package com.talnova.tesp.auditservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.auditservice.dto.AuditLogRequestDTO;
import com.talnova.tesp.auditservice.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventKafkaListener.class);
    public static final String AUDIT_TOPIC = "tesp.audit.events.v1";

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public AuditEventKafkaListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @KafkaListener(topics = AUDIT_TOPIC, groupId = "tesp-audit-group")
    public void consumeAuditEvent(String message) {
        log.info("Received raw audit event payload from Kafka topic '{}'", AUDIT_TOPIC);
        try {
            JsonNode root = objectMapper.readTree(message);
            String projectId = root.has("projectId") ? root.get("projectId").asText() : "PRJ-DEFAULT";
            String actorId = root.has("actorId") ? root.get("actorId").asText() :
                            (root.has("userId") ? root.get("userId").asText() : "SYSTEM");
            String userRole = root.has("userRole") ? root.get("userRole").asText() : "SYSTEM_ROLE";
            String action = root.has("action") ? root.get("action").asText() :
                           (root.has("eventType") ? root.get("eventType").asText() : "DOMAIN_EVENT");
            String resourceId = root.has("resourceId") ? root.get("resourceId").asText() :
                               (root.has("actionPlanId") ? root.get("actionPlanId").asText() :
                               (root.has("nodeId") ? root.get("nodeId").asText() : null));
            String details = root.has("details") ? root.get("details").asText() : message;

            AuditLogRequestDTO request = AuditLogRequestDTO.builder()
                    .projectId(projectId)
                    .actorId(actorId)
                    .userRole(userRole)
                    .action(action)
                    .resourceId(resourceId)
                    .details(details)
                    .build();

            auditLogService.recordAuditLog(request);
        } catch (Exception e) {
            log.error("Failed to parse or process audit event payload: {}", e.getMessage(), e);
        }
    }
}
