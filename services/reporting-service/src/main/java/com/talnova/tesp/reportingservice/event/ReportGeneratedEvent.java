package com.talnova.tesp.reportingservice.event;

import com.talnova.tesp.reportingservice.domain.model.ReportType;

import java.time.Instant;

public class ReportGeneratedEvent {

    private String eventId;
    private String projectId;
    private String campaignId;
    private String jobId;
    private ReportType reportType;
    private String downloadUrl;
    private Instant expiresAt;
    private String recipientEmail;
    private Instant timestamp;

    public ReportGeneratedEvent() {}

    public ReportGeneratedEvent(String eventId, String projectId, String campaignId, String jobId, ReportType reportType, String downloadUrl, Instant expiresAt, String recipientEmail, Instant timestamp) {
        this.eventId = eventId;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.jobId = jobId;
        this.reportType = reportType;
        this.downloadUrl = downloadUrl;
        this.expiresAt = expiresAt;
        this.recipientEmail = recipientEmail;
        this.timestamp = timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String eventId;
        private String projectId;
        private String campaignId;
        private String jobId;
        private ReportType reportType;
        private String downloadUrl;
        private Instant expiresAt;
        private String recipientEmail;
        private Instant timestamp;

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder jobId(String jobId) { this.jobId = jobId; return this; }
        public Builder reportType(ReportType reportType) { this.reportType = reportType; return this; }
        public Builder downloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder recipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public ReportGeneratedEvent build() {
            return new ReportGeneratedEvent(eventId, projectId, campaignId, jobId, reportType, downloadUrl, expiresAt, recipientEmail, timestamp);
        }
    }
}
