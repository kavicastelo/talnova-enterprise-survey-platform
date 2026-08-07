package com.talnova.tesp.actionservice.audit;

import com.talnova.tesp.actionservice.domain.model.ActionStatus;

import java.time.Instant;

public class ActionAuditLog {

    private String auditId;
    private String actionPlanId;
    private String actorId;
    private String actionType;
    private ActionStatus oldStatus;
    private ActionStatus newStatus;
    private String details;
    private Instant timestamp;

    public ActionAuditLog() {}

    public ActionAuditLog(String auditId, String actionPlanId, String actorId, String actionType, ActionStatus oldStatus, ActionStatus newStatus, String details, Instant timestamp) {
        this.auditId = auditId;
        this.actionPlanId = actionPlanId;
        this.actorId = actorId;
        this.actionType = actionType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }

    public String getActionPlanId() { return actionPlanId; }
    public void setActionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public ActionStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(ActionStatus oldStatus) { this.oldStatus = oldStatus; }

    public ActionStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ActionStatus newStatus) { this.newStatus = newStatus; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String auditId;
        private String actionPlanId;
        private String actorId;
        private String actionType;
        private ActionStatus oldStatus;
        private ActionStatus newStatus;
        private String details;
        private Instant timestamp;

        public Builder auditId(String auditId) { this.auditId = auditId; return this; }
        public Builder actionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; return this; }
        public Builder actorId(String actorId) { this.actorId = actorId; return this; }
        public Builder actionType(String actionType) { this.actionType = actionType; return this; }
        public Builder oldStatus(ActionStatus oldStatus) { this.oldStatus = oldStatus; return this; }
        public Builder newStatus(ActionStatus newStatus) { this.newStatus = newStatus; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public ActionAuditLog build() {
            return new ActionAuditLog(auditId, actionPlanId, actorId, actionType, oldStatus, newStatus, details, timestamp);
        }
    }
}
