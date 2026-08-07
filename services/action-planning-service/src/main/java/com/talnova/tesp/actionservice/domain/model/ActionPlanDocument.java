package com.talnova.tesp.actionservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "action_plans")
@CompoundIndexes({
        @CompoundIndex(name = "idx_action_plan_id", def = "{'actionPlanId': 1}", unique = true),
        @CompoundIndex(name = "idx_proj_node_status", def = "{'projectId': 1, 'nodeId': 1, 'status': 1}"),
        @CompoundIndex(name = "idx_proj_campaign", def = "{'projectId': 1, 'campaignId': 1}")
})
public class ActionPlanDocument {

    @Id
    private String id;
    private String projectId;
    private String actionPlanId;
    private String campaignId;
    private String nodeId;
    private String groupId;
    private String title;
    private String description;
    private Double baselineScore;
    private Double targetScore;
    private Double postActionScore;
    private ActionStatus status;
    private String assigneeId;
    private String createdBy;
    private Instant targetCompletionDate;
    private List<ActionMilestone> milestones = new ArrayList<>();
    private ExternalSyncInfo externalSync;
    private Instant createdAt;
    private Instant updatedAt;

    public ActionPlanDocument() {}

    public ActionPlanDocument(String id, String projectId, String actionPlanId, String campaignId, String nodeId, String groupId, String title, String description, Double baselineScore, Double targetScore, Double postActionScore, ActionStatus status, String assigneeId, String createdBy, Instant targetCompletionDate, List<ActionMilestone> milestones, ExternalSyncInfo externalSync, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.actionPlanId = actionPlanId;
        this.campaignId = campaignId;
        this.nodeId = nodeId;
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.baselineScore = baselineScore;
        this.targetScore = targetScore;
        this.postActionScore = postActionScore;
        this.status = status;
        this.assigneeId = assigneeId;
        this.createdBy = createdBy;
        this.targetCompletionDate = targetCompletionDate;
        this.milestones = milestones != null ? milestones : new ArrayList<>();
        this.externalSync = externalSync;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getActionPlanId() { return actionPlanId; }
    public void setActionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getBaselineScore() { return baselineScore; }
    public void setBaselineScore(Double baselineScore) { this.baselineScore = baselineScore; }

    public Double getTargetScore() { return targetScore; }
    public void setTargetScore(Double targetScore) { this.targetScore = targetScore; }

    public Double getPostActionScore() { return postActionScore; }
    public void setPostActionScore(Double postActionScore) { this.postActionScore = postActionScore; }

    public ActionStatus getStatus() { return status; }
    public void setStatus(ActionStatus status) { this.status = status; }

    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(Instant targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public List<ActionMilestone> getMilestones() { return milestones; }
    public void setMilestones(List<ActionMilestone> milestones) { this.milestones = milestones; }

    public ExternalSyncInfo getExternalSync() { return externalSync; }
    public void setExternalSync(ExternalSyncInfo externalSync) { this.externalSync = externalSync; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String actionPlanId;
        private String campaignId;
        private String nodeId;
        private String groupId;
        private String title;
        private String description;
        private Double baselineScore;
        private Double targetScore;
        private Double postActionScore;
        private ActionStatus status;
        private String assigneeId;
        private String createdBy;
        private Instant targetCompletionDate;
        private List<ActionMilestone> milestones = new ArrayList<>();
        private ExternalSyncInfo externalSync;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder actionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder baselineScore(Double baselineScore) { this.baselineScore = baselineScore; return this; }
        public Builder targetScore(Double targetScore) { this.targetScore = targetScore; return this; }
        public Builder postActionScore(Double postActionScore) { this.postActionScore = postActionScore; return this; }
        public Builder status(ActionStatus status) { this.status = status; return this; }
        public Builder assigneeId(String assigneeId) { this.assigneeId = assigneeId; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder targetCompletionDate(Instant targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; return this; }
        public Builder milestones(List<ActionMilestone> milestones) { this.milestones = milestones; return this; }
        public Builder externalSync(ExternalSyncInfo externalSync) { this.externalSync = externalSync; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public ActionPlanDocument build() {
            return new ActionPlanDocument(id, projectId, actionPlanId, campaignId, nodeId, groupId, title, description, baselineScore, targetScore, postActionScore, status, assigneeId, createdBy, targetCompletionDate, milestones, externalSync, createdAt, updatedAt);
        }
    }
}
