package com.talnova.tesp.aiservice.event;

import com.talnova.tesp.aiservice.domain.model.RiskSeverity;

import java.time.Instant;

public class WorkplaceRiskAlertEvent {

    private String alertId;
    private String projectId;
    private String campaignId;
    private String responseId;
    private String questionId;
    private String category;
    private RiskSeverity severity;
    private String detectedKeyword;
    private String sanitizedSnippet;
    private Instant detectedAt;

    public WorkplaceRiskAlertEvent() {}

    public WorkplaceRiskAlertEvent(String alertId, String projectId, String campaignId, String responseId, String questionId, String category, RiskSeverity severity, String detectedKeyword, String sanitizedSnippet, Instant detectedAt) {
        this.alertId = alertId;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.responseId = responseId;
        this.questionId = questionId;
        this.category = category;
        this.severity = severity;
        this.detectedKeyword = detectedKeyword;
        this.sanitizedSnippet = sanitizedSnippet;
        this.detectedAt = detectedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public RiskSeverity getSeverity() { return severity; }
    public void setSeverity(RiskSeverity severity) { this.severity = severity; }

    public String getDetectedKeyword() { return detectedKeyword; }
    public void setDetectedKeyword(String detectedKeyword) { this.detectedKeyword = detectedKeyword; }

    public String getSanitizedSnippet() { return sanitizedSnippet; }
    public void setSanitizedSnippet(String sanitizedSnippet) { this.sanitizedSnippet = sanitizedSnippet; }

    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }

    public static class Builder {
        private String alertId;
        private String projectId;
        private String campaignId;
        private String responseId;
        private String questionId;
        private String category;
        private RiskSeverity severity;
        private String detectedKeyword;
        private String sanitizedSnippet;
        private Instant detectedAt;

        public Builder alertId(String alertId) { this.alertId = alertId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder responseId(String responseId) { this.responseId = responseId; return this; }
        public Builder questionId(String questionId) { this.questionId = questionId; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder severity(RiskSeverity severity) { this.severity = severity; return this; }
        public Builder detectedKeyword(String detectedKeyword) { this.detectedKeyword = detectedKeyword; return this; }
        public Builder sanitizedSnippet(String sanitizedSnippet) { this.sanitizedSnippet = sanitizedSnippet; return this; }
        public Builder detectedAt(Instant detectedAt) { this.detectedAt = detectedAt; return this; }

        public WorkplaceRiskAlertEvent build() {
            return new WorkplaceRiskAlertEvent(alertId, projectId, campaignId, responseId, questionId, category, severity, detectedKeyword, sanitizedSnippet, detectedAt);
        }
    }
}
