package com.talnova.tesp.analyticsservice.dto;

public class LongitudinalDeltaDTO {

    private String campaignId;
    private String baselineCampaignId;
    private Double currentEnps;
    private Double baselineEnps;
    private Double enpsDelta;
    private Double currentEngagementIndex;
    private Double baselineEngagementIndex;
    private Double engagementIndexDelta;
    private String trendDirection; // UP, DOWN, STABLE

    public LongitudinalDeltaDTO() {}

    public LongitudinalDeltaDTO(String campaignId, String baselineCampaignId, Double currentEnps, Double baselineEnps, Double enpsDelta, Double currentEngagementIndex, Double baselineEngagementIndex, Double engagementIndexDelta, String trendDirection) {
        this.campaignId = campaignId;
        this.baselineCampaignId = baselineCampaignId;
        this.currentEnps = currentEnps;
        this.baselineEnps = baselineEnps;
        this.enpsDelta = enpsDelta;
        this.currentEngagementIndex = currentEngagementIndex;
        this.baselineEngagementIndex = baselineEngagementIndex;
        this.engagementIndexDelta = engagementIndexDelta;
        this.trendDirection = trendDirection;
    }

    public static Builder builder() { return new Builder(); }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getBaselineCampaignId() { return baselineCampaignId; }
    public void setBaselineCampaignId(String baselineCampaignId) { this.baselineCampaignId = baselineCampaignId; }

    public Double getCurrentEnps() { return currentEnps; }
    public void setCurrentEnps(Double currentEnps) { this.currentEnps = currentEnps; }

    public Double getBaselineEnps() { return baselineEnps; }
    public void setBaselineEnps(Double baselineEnps) { this.baselineEnps = baselineEnps; }

    public Double getEnpsDelta() { return enpsDelta; }
    public void setEnpsDelta(Double enpsDelta) { this.enpsDelta = enpsDelta; }

    public Double getCurrentEngagementIndex() { return currentEngagementIndex; }
    public void setCurrentEngagementIndex(Double currentEngagementIndex) { this.currentEngagementIndex = currentEngagementIndex; }

    public Double getBaselineEngagementIndex() { return baselineEngagementIndex; }
    public void setBaselineEngagementIndex(Double baselineEngagementIndex) { this.baselineEngagementIndex = baselineEngagementIndex; }

    public Double getEngagementIndexDelta() { return engagementIndexDelta; }
    public void setEngagementIndexDelta(Double engagementIndexDelta) { this.engagementIndexDelta = engagementIndexDelta; }

    public String getTrendDirection() { return trendDirection; }
    public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }

    public static class Builder {
        private String campaignId;
        private String baselineCampaignId;
        private Double currentEnps;
        private Double baselineEnps;
        private Double enpsDelta;
        private Double currentEngagementIndex;
        private Double baselineEngagementIndex;
        private Double engagementIndexDelta;
        private String trendDirection;

        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder baselineCampaignId(String baselineCampaignId) { this.baselineCampaignId = baselineCampaignId; return this; }
        public Builder currentEnps(Double currentEnps) { this.currentEnps = currentEnps; return this; }
        public Builder baselineEnps(Double baselineEnps) { this.baselineEnps = baselineEnps; return this; }
        public Builder enpsDelta(Double enpsDelta) { this.enpsDelta = enpsDelta; return this; }
        public Builder currentEngagementIndex(Double currentEngagementIndex) { this.currentEngagementIndex = currentEngagementIndex; return this; }
        public Builder baselineEngagementIndex(Double baselineEngagementIndex) { this.baselineEngagementIndex = baselineEngagementIndex; return this; }
        public Builder engagementIndexDelta(Double engagementIndexDelta) { this.engagementIndexDelta = engagementIndexDelta; return this; }
        public Builder trendDirection(String trendDirection) { this.trendDirection = trendDirection; return this; }

        public LongitudinalDeltaDTO build() {
            return new LongitudinalDeltaDTO(campaignId, baselineCampaignId, currentEnps, baselineEnps, enpsDelta, currentEngagementIndex, baselineEngagementIndex, engagementIndexDelta, trendDirection);
        }
    }
}
