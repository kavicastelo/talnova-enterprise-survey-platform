package com.talnova.tesp.actionservice.dto;

import jakarta.validation.constraints.NotBlank;

public class ApprovalRequestDTO {

    @NotBlank(message = "actorId is required")
    private String actorId;

    @NotBlank(message = "userRole is required")
    private String userRole;

    private String rationale;

    public ApprovalRequestDTO() {}

    public ApprovalRequestDTO(String actorId, String userRole, String rationale) {
        this.actorId = actorId;
        this.userRole = userRole;
        this.rationale = rationale;
    }

    public static Builder builder() { return new Builder(); }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public static class Builder {
        private String actorId;
        private String userRole;
        private String rationale;

        public Builder actorId(String actorId) { this.actorId = actorId; return this; }
        public Builder userRole(String userRole) { this.userRole = userRole; return this; }
        public Builder rationale(String rationale) { this.rationale = rationale; return this; }

        public ApprovalRequestDTO build() {
            return new ApprovalRequestDTO(actorId, userRole, rationale);
        }
    }
}
