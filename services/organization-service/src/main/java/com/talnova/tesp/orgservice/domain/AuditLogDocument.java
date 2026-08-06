package com.talnova.tesp.orgservice.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "audit_logs")
public class AuditLogDocument {

    @Id
    private String id;

    private String projectId;
    private String action;
    private String resourceId;
    private String performedBy;
    private String correlationId;
    private Map<String, Object> metadata;

    @CreatedDate
    private Instant timestamp;

    public AuditLogDocument() {
    }

    public AuditLogDocument(String id, String projectId, String action, String resourceId,
                            String performedBy, String correlationId, Map<String, Object> metadata,
                            Instant timestamp) {
        this.id = id;
        this.projectId = projectId;
        this.action = action;
        this.resourceId = resourceId;
        this.performedBy = performedBy;
        this.correlationId = correlationId;
        this.metadata = metadata;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String id;
        private String projectId;
        private String action;
        private String resourceId;
        private String performedBy;
        private String correlationId;
        private Map<String, Object> metadata;
        private Instant timestamp;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder performedBy(String performedBy) { this.performedBy = performedBy; return this; }
        public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
        public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public AuditLogDocument build() {
            return new AuditLogDocument(id, projectId, action, resourceId, performedBy, correlationId, metadata, timestamp);
        }
    }
}
