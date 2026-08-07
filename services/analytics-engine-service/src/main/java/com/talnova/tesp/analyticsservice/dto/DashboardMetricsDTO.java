package com.talnova.tesp.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.talnova.tesp.analyticsservice.domain.model.GroupScore;
import java.util.List;

public class DashboardMetricsDTO {

    private String campaignId;
    private String nodeId;
    private int totalResponses;
    private double participationRate;

    @JsonProperty("eNPS")
    private Double enps;

    private Double engagementIndex;
    private List<GroupScore> groupScores;

    public DashboardMetricsDTO() {}

    public DashboardMetricsDTO(String campaignId, String nodeId, int totalResponses, double participationRate, Double enps, Double engagementIndex, List<GroupScore> groupScores) {
        this.campaignId = campaignId;
        this.nodeId = nodeId;
        this.totalResponses = totalResponses;
        this.participationRate = participationRate;
        this.enps = enps;
        this.engagementIndex = engagementIndex;
        this.groupScores = groupScores;
    }

    public static Builder builder() { return new Builder(); }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public int getTotalResponses() { return totalResponses; }
    public void setTotalResponses(int totalResponses) { this.totalResponses = totalResponses; }

    public double getParticipationRate() { return participationRate; }
    public void setParticipationRate(double participationRate) { this.participationRate = participationRate; }

    public Double getEnps() { return enps; }
    public void setEnps(Double enps) { this.enps = enps; }

    public Double getEngagementIndex() { return engagementIndex; }
    public void setEngagementIndex(Double engagementIndex) { this.engagementIndex = engagementIndex; }

    public List<GroupScore> getGroupScores() { return groupScores; }
    public void setGroupScores(List<GroupScore> groupScores) { this.groupScores = groupScores; }

    public static class Builder {
        private String campaignId;
        private String nodeId;
        private int totalResponses;
        private double participationRate;
        private Double enps;
        private Double engagementIndex;
        private List<GroupScore> groupScores;

        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder totalResponses(int totalResponses) { this.totalResponses = totalResponses; return this; }
        public Builder participationRate(double participationRate) { this.participationRate = participationRate; return this; }
        public Builder eNPS(Double enps) { this.enps = enps; return this; }
        public Builder enps(Double enps) { this.enps = enps; return this; }
        public Builder engagementIndex(Double engagementIndex) { this.engagementIndex = engagementIndex; return this; }
        public Builder groupScores(List<GroupScore> groupScores) { this.groupScores = groupScores; return this; }

        public DashboardMetricsDTO build() {
            return new DashboardMetricsDTO(campaignId, nodeId, totalResponses, participationRate, enps, engagementIndex, groupScores);
        }
    }
}
