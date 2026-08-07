package com.talnova.tesp.actionservice.event;

import java.time.Instant;

public class AnalyticalSnapshotCreatedEvent {

    private String eventId;
    private String projectId;
    private String campaignId;
    private String nodeId;
    private String groupId;
    private String categoryName;
    private Double score;
    private Instant timestamp;

    public AnalyticalSnapshotCreatedEvent() {}

    public AnalyticalSnapshotCreatedEvent(String eventId, String projectId, String campaignId, String nodeId, String groupId, String categoryName, Double score, Instant timestamp) {
        this.eventId = eventId;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.nodeId = nodeId;
        this.groupId = groupId;
        this.categoryName = categoryName;
        this.score = score;
        this.timestamp = timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String eventId;
        private String projectId;
        private String campaignId;
        private String nodeId;
        private String groupId;
        private String categoryName;
        private Double score;
        private Instant timestamp;

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public AnalyticalSnapshotCreatedEvent build() {
            return new AnalyticalSnapshotCreatedEvent(eventId, projectId, campaignId, nodeId, groupId, categoryName, score, timestamp);
        }
    }
}
