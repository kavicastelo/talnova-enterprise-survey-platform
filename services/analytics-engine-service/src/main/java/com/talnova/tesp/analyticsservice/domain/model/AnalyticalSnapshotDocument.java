package com.talnova.tesp.analyticsservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "analytical_snapshots")
@CompoundIndexes({
        @CompoundIndex(name = "idx_proj_campaign", def = "{'projectId': 1, 'campaignId': 1}"),
        @CompoundIndex(name = "idx_proj_snapshot_date", def = "{'projectId': 1, 'snapshotDate': -1}")
})
public class AnalyticalSnapshotDocument {

    @Id
    private String id;
    private String projectId;
    private String campaignId;
    private String surveyId;
    private Instant snapshotDate;
    private Integer totalResponses;
    private Double overallEnps;
    private Double overallEngagementIndex;
    private Double participationRate;
    private Boolean isFrozen;
    private List<GroupScore> groupScores;
    private List<NodeAggregate> nodeAggregates;
    private Instant createdAt;

    public AnalyticalSnapshotDocument() {}

    public AnalyticalSnapshotDocument(String id, String projectId, String campaignId, String surveyId, Instant snapshotDate, Integer totalResponses, Double overallEnps, Double overallEngagementIndex, Double participationRate, Boolean isFrozen, List<GroupScore> groupScores, List<NodeAggregate> nodeAggregates, Instant createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.snapshotDate = snapshotDate;
        this.totalResponses = totalResponses;
        this.overallEnps = overallEnps;
        this.overallEngagementIndex = overallEngagementIndex;
        this.participationRate = participationRate;
        this.isFrozen = isFrozen;
        this.groupScores = groupScores;
        this.nodeAggregates = nodeAggregates;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public Instant getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(Instant snapshotDate) { this.snapshotDate = snapshotDate; }

    public Integer getTotalResponses() { return totalResponses; }
    public void setTotalResponses(Integer totalResponses) { this.totalResponses = totalResponses; }

    public Double getOverallEnps() { return overallEnps; }
    public void setOverallEnps(Double overallEnps) { this.overallEnps = overallEnps; }

    public Double getOverallEngagementIndex() { return overallEngagementIndex; }
    public void setOverallEngagementIndex(Double overallEngagementIndex) { this.overallEngagementIndex = overallEngagementIndex; }

    public Double getParticipationRate() { return participationRate; }
    public void setParticipationRate(Double participationRate) { this.participationRate = participationRate; }

    public Boolean getIsFrozen() { return isFrozen; }
    public void setIsFrozen(Boolean isFrozen) { this.isFrozen = isFrozen; }

    public List<GroupScore> getGroupScores() { return groupScores; }
    public void setGroupScores(List<GroupScore> groupScores) { this.groupScores = groupScores; }

    public List<NodeAggregate> getNodeAggregates() { return nodeAggregates; }
    public void setNodeAggregates(List<NodeAggregate> nodeAggregates) { this.nodeAggregates = nodeAggregates; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String campaignId;
        private String surveyId;
        private Instant snapshotDate;
        private Integer totalResponses;
        private Double overallEnps;
        private Double overallEngagementIndex;
        private Double participationRate;
        private Boolean isFrozen;
        private List<GroupScore> groupScores;
        private List<NodeAggregate> nodeAggregates;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder snapshotDate(Instant snapshotDate) { this.snapshotDate = snapshotDate; return this; }
        public Builder snapshotTimestamp(Instant snapshotDate) { this.snapshotDate = snapshotDate; return this; }
        public Builder totalResponses(Integer totalResponses) { this.totalResponses = totalResponses; return this; }
        public Builder overallEnps(Double overallEnps) { this.overallEnps = overallEnps; return this; }
        public Builder overallEngagementIndex(Double overallEngagementIndex) { this.overallEngagementIndex = overallEngagementIndex; return this; }
        public Builder participationRate(Double participationRate) { this.participationRate = participationRate; return this; }
        public Builder isFrozen(Boolean isFrozen) { this.isFrozen = isFrozen; return this; }
        public Builder groupScores(List<GroupScore> groupScores) { this.groupScores = groupScores; return this; }
        public Builder nodeAggregates(List<NodeAggregate> nodeAggregates) { this.nodeAggregates = nodeAggregates; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AnalyticalSnapshotDocument build() {
            return new AnalyticalSnapshotDocument(id, projectId, campaignId, surveyId, snapshotDate, totalResponses, overallEnps, overallEngagementIndex, participationRate, isFrozen, groupScores, nodeAggregates, createdAt);
        }
    }
}
