package com.talnova.tesp.distservice.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "survey_campaigns")
@CompoundIndexes({
        @CompoundIndex(name = "idx_project_campaign_unique", def = "{'projectId': 1, 'campaignId': 1}", unique = true),
        @CompoundIndex(name = "idx_project_status", def = "{'projectId': 1, 'status': 1}")
})
public class SurveyCampaignDocument {

    @Id
    private String id;

    private String projectId;
    private String campaignId;
    private String surveyId;
    private int surveyVersion;
    private String title;

    private AnonymityLevel anonymityLevel;

    private List<DistributionChannel> channels = new ArrayList<>();
    private TargetAudience targetAudience = new TargetAudience();
    private CampaignSchedule schedule = new CampaignSchedule();
    private CampaignMetrics metrics = new CampaignMetrics();

    private CampaignStatus status;

    private Instant startDate;
    private Instant expirationDate;

    private boolean isDeleted = false;

    @Version
    private Long documentVersion;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public SurveyCampaignDocument() {
    }

    public SurveyCampaignDocument(String id, String projectId, String campaignId, String surveyId, int surveyVersion, String title, AnonymityLevel anonymityLevel, List<DistributionChannel> channels, TargetAudience targetAudience, CampaignSchedule schedule, CampaignMetrics metrics, CampaignStatus status, Instant startDate, Instant expirationDate, boolean isDeleted, Long documentVersion, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.surveyVersion = surveyVersion;
        this.title = title;
        this.anonymityLevel = anonymityLevel;
        this.channels = channels != null ? channels : new ArrayList<>();
        this.targetAudience = targetAudience != null ? targetAudience : new TargetAudience();
        this.schedule = schedule != null ? schedule : new CampaignSchedule();
        this.metrics = metrics != null ? metrics : new CampaignMetrics();
        this.status = status;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.isDeleted = isDeleted;
        this.documentVersion = documentVersion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public int getSurveyVersion() { return surveyVersion; }
    public void setSurveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public List<DistributionChannel> getChannels() { return channels; }
    public void setChannels(List<DistributionChannel> channels) { this.channels = channels; }

    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }

    public CampaignSchedule getSchedule() { return schedule; }
    public void setSchedule(CampaignSchedule schedule) { this.schedule = schedule; }

    public CampaignMetrics getMetrics() { return metrics; }
    public void setMetrics(CampaignMetrics metrics) { this.metrics = metrics; }

    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Instant expirationDate) { this.expirationDate = expirationDate; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Long getDocumentVersion() { return documentVersion; }
    public void setDocumentVersion(Long documentVersion) { this.documentVersion = documentVersion; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String campaignId;
        private String surveyId;
        private int surveyVersion;
        private String title;
        private AnonymityLevel anonymityLevel;
        private List<DistributionChannel> channels = new ArrayList<>();
        private TargetAudience targetAudience = new TargetAudience();
        private CampaignSchedule schedule = new CampaignSchedule();
        private CampaignMetrics metrics = new CampaignMetrics();
        private CampaignStatus status;
        private Instant startDate;
        private Instant expirationDate;
        private boolean isDeleted = false;
        private Long documentVersion;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder surveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder channels(List<DistributionChannel> channels) { this.channels = channels; return this; }
        public Builder targetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; return this; }
        public Builder schedule(CampaignSchedule schedule) { this.schedule = schedule; return this; }
        public Builder metrics(CampaignMetrics metrics) { this.metrics = metrics; return this; }
        public Builder status(CampaignStatus status) { this.status = status; return this; }
        public Builder startDate(Instant startDate) { this.startDate = startDate; return this; }
        public Builder expirationDate(Instant expirationDate) { this.expirationDate = expirationDate; return this; }
        public Builder isDeleted(boolean isDeleted) { this.isDeleted = isDeleted; return this; }
        public Builder documentVersion(Long documentVersion) { this.documentVersion = documentVersion; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public SurveyCampaignDocument build() {
            return new SurveyCampaignDocument(id, projectId, campaignId, surveyId, surveyVersion, title, anonymityLevel, channels, targetAudience, schedule, metrics, status, startDate, expirationDate, isDeleted, documentVersion, createdAt, updatedAt);
        }
    }
}
