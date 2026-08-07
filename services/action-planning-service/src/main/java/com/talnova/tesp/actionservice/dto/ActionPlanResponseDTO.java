package com.talnova.tesp.actionservice.dto;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;

import java.time.Instant;

public class ActionPlanResponseDTO {

    private String actionPlanId;
    private String projectId;
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
    private Instant targetCompletionDate;
    private int milestoneCount;
    private String externalSyncSystem;
    private Instant createdAt;
    private Instant updatedAt;

    public ActionPlanResponseDTO() {}

    public static ActionPlanResponseDTO fromDocument(ActionPlanDocument doc) {
        if (doc == null) return null;
        ActionPlanResponseDTO dto = new ActionPlanResponseDTO();
        dto.setActionPlanId(doc.getActionPlanId());
        dto.setProjectId(doc.getProjectId());
        dto.setCampaignId(doc.getCampaignId());
        dto.setNodeId(doc.getNodeId());
        dto.setGroupId(doc.getGroupId());
        dto.setTitle(doc.getTitle());
        dto.setDescription(doc.getDescription());
        dto.setBaselineScore(doc.getBaselineScore());
        dto.setTargetScore(doc.getTargetScore());
        dto.setPostActionScore(doc.getPostActionScore());
        dto.setStatus(doc.getStatus());
        dto.setAssigneeId(doc.getAssigneeId());
        dto.setTargetCompletionDate(doc.getTargetCompletionDate());
        dto.setMilestoneCount(doc.getMilestones() != null ? doc.getMilestones().size() : 0);
        dto.setExternalSyncSystem(doc.getExternalSync() != null ? doc.getExternalSync().getSystem() : null);
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setUpdatedAt(doc.getUpdatedAt());
        return dto;
    }

    public String getActionPlanId() { return actionPlanId; }
    public void setActionPlanId(String actionPlanId) { this.actionPlanId = actionPlanId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

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

    public Instant getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(Instant targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public int getMilestoneCount() { return milestoneCount; }
    public void setMilestoneCount(int milestoneCount) { this.milestoneCount = milestoneCount; }

    public String getExternalSyncSystem() { return externalSyncSystem; }
    public void setExternalSyncSystem(String externalSyncSystem) { this.externalSyncSystem = externalSyncSystem; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
