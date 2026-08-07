package com.talnova.tesp.reportingservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "report_jobs")
@CompoundIndexes({
        @CompoundIndex(name = "idx_proj_job", def = "{'projectId': 1, 'jobId': 1}", unique = true),
        @CompoundIndex(name = "idx_status", def = "{'status': 1}"),
        @CompoundIndex(name = "idx_proj_campaign", def = "{'projectId': 1, 'campaignId': 1}")
})
public class ReportJobDocument {

    @Id
    private String id;
    private String projectId;
    private String jobId;
    private ReportType reportType;
    private String campaignId;
    private String nodeId;
    private String requestedBy;
    private ReportStatus status;
    private String downloadUrl;
    private Instant expiresAt;
    private String errorMessage;
    private Instant createdAt;
    private Instant completedAt;

    public ReportJobDocument() {}

    public ReportJobDocument(String id, String projectId, String jobId, ReportType reportType, String campaignId, String nodeId, String requestedBy, ReportStatus status, String downloadUrl, Instant expiresAt, String errorMessage, Instant createdAt, Instant completedAt) {
        this.id = id;
        this.projectId = projectId;
        this.jobId = jobId;
        this.reportType = reportType;
        this.campaignId = campaignId;
        this.nodeId = nodeId;
        this.requestedBy = requestedBy;
        this.status = status;
        this.downloadUrl = downloadUrl;
        this.expiresAt = expiresAt;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String jobId;
        private ReportType reportType;
        private String campaignId;
        private String nodeId;
        private String requestedBy;
        private ReportStatus status;
        private String downloadUrl;
        private Instant expiresAt;
        private String errorMessage;
        private Instant createdAt;
        private Instant completedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder jobId(String jobId) { this.jobId = jobId; return this; }
        public Builder reportType(ReportType reportType) { this.reportType = reportType; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder requestedBy(String requestedBy) { this.requestedBy = requestedBy; return this; }
        public Builder status(ReportStatus status) { this.status = status; return this; }
        public Builder downloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }

        public ReportJobDocument build() {
            return new ReportJobDocument(id, projectId, jobId, reportType, campaignId, nodeId, requestedBy, status, downloadUrl, expiresAt, errorMessage, createdAt, completedAt);
        }
    }
}
