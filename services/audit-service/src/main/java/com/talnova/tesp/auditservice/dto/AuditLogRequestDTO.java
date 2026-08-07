package com.talnova.tesp.auditservice.dto;

import jakarta.validation.constraints.NotBlank;

public class AuditLogRequestDTO {

    private String projectId;

    @NotBlank(message = "Actor ID is required")
    private String actorId;

    private String userRole;

    @NotBlank(message = "Action is required")
    private String action;

    private String resourceId;
    private String details;

    public AuditLogRequestDTO() {}

    public AuditLogRequestDTO(String projectId, String actorId, String userRole, String action, String resourceId, String details) {
        this.projectId = projectId;
        this.actorId = actorId;
        this.userRole = userRole;
        this.action = action;
        this.resourceId = resourceId;
        this.details = details;
    }

    public static Builder builder() { return new Builder(); }

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

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public static class Builder {
        private String projectId;
        private String actorId;
        private String userRole;
        private String action;
        private String resourceId;
        private String details;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder actorId(String actorId) { this.actorId = actorId; return this; }
        public Builder userRole(String userRole) { this.userRole = userRole; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder details(String details) { this.details = details; return this; }

        public AuditLogRequestDTO build() {
            return new AuditLogRequestDTO(projectId, actorId, userRole, action, resourceId, details);
        }
    }
}
