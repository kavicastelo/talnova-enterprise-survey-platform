package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Response object representing survey distribution campaign details")
public class CampaignResponseDTO {

    private String id;
    private String projectId;
    private String campaignId;
    private String surveyId;
    private int surveyVersion;
    private String title;
    private AnonymityLevel anonymityLevel;
    private List<DistributionChannel> channels = new ArrayList<>();
    private TargetAudienceDTO targetAudience;
    private CampaignScheduleDTO schedule;
    private CampaignMetricsDTO metrics;
    private CampaignStatus status;
    private Instant startDate;
    private Instant expirationDate;
    private Instant createdAt;
    private Instant updatedAt;

    public CampaignResponseDTO() {
    }

    public CampaignResponseDTO(String id, String projectId, String campaignId, String surveyId, int surveyVersion, String title, AnonymityLevel anonymityLevel, List<DistributionChannel> channels, TargetAudienceDTO targetAudience, CampaignScheduleDTO schedule, CampaignMetricsDTO metrics, CampaignStatus status, Instant startDate, Instant expirationDate, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.surveyVersion = surveyVersion;
        this.title = title;
        this.anonymityLevel = anonymityLevel;
        this.channels = channels != null ? channels : new ArrayList<>();
        this.targetAudience = targetAudience;
        this.schedule = schedule;
        this.metrics = metrics;
        this.status = status;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
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

    public TargetAudienceDTO getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudienceDTO targetAudience) { this.targetAudience = targetAudience; }

    public CampaignScheduleDTO getSchedule() { return schedule; }
    public void setSchedule(CampaignScheduleDTO schedule) { this.schedule = schedule; }

    public CampaignMetricsDTO getMetrics() { return metrics; }
    public void setMetrics(CampaignMetricsDTO metrics) { this.metrics = metrics; }

    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Instant expirationDate) { this.expirationDate = expirationDate; }

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
        private TargetAudienceDTO targetAudience;
        private CampaignScheduleDTO schedule;
        private CampaignMetricsDTO metrics;
        private CampaignStatus status;
        private Instant startDate;
        private Instant expirationDate;
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
        public Builder targetAudience(TargetAudienceDTO targetAudience) { this.targetAudience = targetAudience; return this; }
        public Builder schedule(CampaignScheduleDTO schedule) { this.schedule = schedule; return this; }
        public Builder metrics(CampaignMetricsDTO metrics) { this.metrics = metrics; return this; }
        public Builder status(CampaignStatus status) { this.status = status; return this; }
        public Builder startDate(Instant startDate) { this.startDate = startDate; return this; }
        public Builder expirationDate(Instant expirationDate) { this.expirationDate = expirationDate; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public CampaignResponseDTO build() {
            return new CampaignResponseDTO(id, projectId, campaignId, surveyId, surveyVersion, title, anonymityLevel, channels, targetAudience, schedule, metrics, status, startDate, expirationDate, createdAt, updatedAt);
        }
    }
}
