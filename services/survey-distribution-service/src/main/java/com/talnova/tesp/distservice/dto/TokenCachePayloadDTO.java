package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "In-memory Redis cached token metadata payload for sub-millisecond intake validation")
public class TokenCachePayloadDTO {

    private String projectId;
    private String campaignId;
    private String surveyId;
    private int surveyVersion;
    private AnonymityLevel anonymityLevel;
    private String kioskPin;
    private Instant createdAt;

    public TokenCachePayloadDTO() {
    }

    public TokenCachePayloadDTO(String projectId, String campaignId, String surveyId, int surveyVersion, AnonymityLevel anonymityLevel, String kioskPin, Instant createdAt) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.surveyVersion = surveyVersion;
        this.anonymityLevel = anonymityLevel;
        this.kioskPin = kioskPin;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public int getSurveyVersion() { return surveyVersion; }
    public void setSurveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private String surveyId;
        private int surveyVersion;
        private AnonymityLevel anonymityLevel;
        private String kioskPin;
        private Instant createdAt = Instant.now();

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder surveyVersion(int surveyVersion) { this.surveyVersion = surveyVersion; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public TokenCachePayloadDTO build() {
            return new TokenCachePayloadDTO(projectId, campaignId, surveyId, surveyVersion, anonymityLevel, kioskPin, createdAt);
        }
    }
}
