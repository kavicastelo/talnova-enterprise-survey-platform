package com.talnova.tesp.reportingservice.dto;

import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;

import java.time.Instant;

public class ReportJobResponseDTO {

    private String jobId;
    private String projectId;
    private String campaignId;
    private ReportType reportType;
    private String nodeId;
    private ReportStatus status;
    private String downloadUrl;
    private Instant createdAt;
    private Instant expiresAt;
    private String message;

    public ReportJobResponseDTO() {}

    public ReportJobResponseDTO(String jobId, String projectId, String campaignId, ReportType reportType, String nodeId, ReportStatus status, String downloadUrl, Instant createdAt, Instant expiresAt, String message) {
        this.jobId = jobId;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.reportType = reportType;
        this.nodeId = nodeId;
        this.status = status;
        this.downloadUrl = downloadUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.message = message;
    }

    public static Builder builder() { return new Builder(); }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class Builder {
        private String jobId;
        private String projectId;
        private String campaignId;
        private ReportType reportType;
        private String nodeId;
        private ReportStatus status;
        private String downloadUrl;
        private Instant createdAt;
        private Instant expiresAt;
        private String message;

        public Builder jobId(String jobId) { this.jobId = jobId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder reportType(ReportType reportType) { this.reportType = reportType; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder status(ReportStatus status) { this.status = status; return this; }
        public Builder downloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder message(String message) { this.message = message; return this; }

        public ReportJobResponseDTO build() {
            return new ReportJobResponseDTO(jobId, projectId, campaignId, reportType, nodeId, status, downloadUrl, createdAt, expiresAt, message);
        }
    }
}
