package com.talnova.tesp.actionservice.event;

import com.talnova.tesp.actionservice.domain.model.ActionStatus;

import java.time.Instant;

public class ActionPlanStatusChangedEvent {

    private String eventId;
    private String actionPlanId;
    private String projectId;
    private String nodeId;
    private ActionStatus oldStatus;
    private ActionStatus newStatus;
    private Double postActionScore;
    private Double scoreDelta;
    private Instant timestamp;

    public ActionPlanStatusChangedEvent() {}

    public ActionPlanStatusChangedEvent(String eventId, String actionPlanId, String projectId, String nodeId, ActionStatus oldStatus, ActionStatus newStatus, Double postActionScore, Double scoreDelta, Instant timestamp) {
        this.eventId = eventId;
        this.actionPlanId = actionPlanId;
        this.projectId = projectId;
        this.nodeId = nodeId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.postActionScore = postActionScore;
        this.scoreDelta = scoreDelta;
        this.timestamp = timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getActionPlanId() { return actionPlanId; }
    public void setActionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public ActionStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(ActionStatus oldStatus) { this.oldStatus = oldStatus; }

    public ActionStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ActionStatus newStatus) { this.newStatus = newStatus; }

    public Double getPostActionScore() { return postActionScore; }
    public void setPostActionScore(Double postActionScore) { this.postActionScore = postActionScore; }

    public Double getScoreDelta() { return scoreDelta; }
    public void setScoreDelta(Double scoreDelta) { this.scoreDelta = scoreDelta; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String eventId;
        private String actionPlanId;
        private String projectId;
        private String nodeId;
        private ActionStatus oldStatus;
        private ActionStatus newStatus;
        private Double postActionScore;
        private Double scoreDelta;
        private Instant timestamp;

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder actionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder oldStatus(ActionStatus oldStatus) { this.oldStatus = oldStatus; return this; }
        public Builder newStatus(ActionStatus newStatus) { this.newStatus = newStatus; return this; }
        public Builder postActionScore(Double postActionScore) { this.postActionScore = postActionScore; return this; }
        public Builder scoreDelta(Double scoreDelta) { this.scoreDelta = scoreDelta; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public ActionPlanStatusChangedEvent build() {
            return new ActionPlanStatusChangedEvent(eventId, actionPlanId, projectId, nodeId, oldStatus, newStatus, postActionScore, scoreDelta, timestamp);
        }
    }
}
