package com.talnova.tesp.configservice.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_logs")
public class AuditLogDocument {

    @Id
    private String id;
    private String auditId;
    private String projectId;
    private String userId;
    private String action;
    private String ipAddress;
    private String correlationId;
    private String details;

    @CreatedDate
    private Instant timestamp;

    public AuditLogDocument() {
    }

    public AuditLogDocument(String id, String auditId, String projectId, String userId, String action,
                            String ipAddress, String correlationId, String details, Instant timestamp) {
        this.id = id;
        this.auditId = auditId;
        this.projectId = projectId;
        this.userId = userId;
        this.action = action;
        this.ipAddress = ipAddress;
        this.correlationId = correlationId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String id;
        private String auditId;
        private String projectId;
        private String userId;
        private String action;
        private String ipAddress;
        private String correlationId;
        private String details;
        private Instant timestamp;

        public Builder id(String id) { this.id = id; return this; }
        public Builder auditId(String auditId) { this.auditId = auditId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public AuditLogDocument build() {
            return new AuditLogDocument(id, auditId, projectId, userId, action, ipAddress, correlationId, details, timestamp);
        }
    }
}
