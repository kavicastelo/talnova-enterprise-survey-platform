package com.talnova.tesp.auditservice.dto;

import java.time.Instant;

public class AuditLogResponseDTO {

    private String auditId;
    private String projectId;
    private String actorId;
    private String userRole;
    private String action;
    private String resourceId;
    private Instant timestamp;
    private String details;

    public AuditLogResponseDTO() {}

    public AuditLogResponseDTO(String auditId, String projectId, String actorId, String userRole, String action, String resourceId, Instant timestamp, String details) {
        this.auditId = auditId;
        this.projectId = projectId;
        this.actorId = actorId;
        this.userRole = userRole;
        this.action = action;
        this.resourceId = resourceId;
        this.timestamp = timestamp;
        this.details = details;
    }

    public static Builder builder() { return new Builder(); }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public static class Builder {
        private String auditId;
        private String projectId;
        private String actorId;
        private String userRole;
        private String action;
        private String resourceId;
        private Instant timestamp;
        private String details;

        public Builder auditId(String auditId) { this.auditId = auditId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder actorId(String actorId) { this.actorId = actorId; return this; }
        public Builder userRole(String userRole) { this.userRole = userRole; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder details(String details) { this.details = details; return this; }

        public AuditLogResponseDTO build() {
            return new AuditLogResponseDTO(auditId, projectId, actorId, userRole, action, resourceId, timestamp, details);
        }
    }
}
