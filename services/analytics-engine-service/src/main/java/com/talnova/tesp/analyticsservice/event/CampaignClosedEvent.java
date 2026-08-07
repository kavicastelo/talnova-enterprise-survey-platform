package com.talnova.tesp.analyticsservice.event;

import java.time.Instant;

public class CampaignClosedEvent {

    private String campaignId;
    private String projectId;
    private Instant closedAt;
    private int totalResponses;

    public CampaignClosedEvent() {}

    public CampaignClosedEvent(String campaignId, String projectId, Instant closedAt, int totalResponses) {
        this.campaignId = campaignId;
        this.projectId = projectId;
        this.closedAt = closedAt;
        this.totalResponses = totalResponses;
    }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }

    public int getTotalResponses() { return totalResponses; }
    public void setTotalResponses(int totalResponses) { this.totalResponses = totalResponses; }
}
