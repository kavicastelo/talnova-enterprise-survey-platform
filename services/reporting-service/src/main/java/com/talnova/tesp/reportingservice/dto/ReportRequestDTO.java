package com.talnova.tesp.reportingservice.dto;

import com.talnova.tesp.reportingservice.domain.model.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportRequestDTO {

    @NotBlank(message = "projectId is required")
    private String projectId;

    @NotBlank(message = "campaignId is required")
    private String campaignId;

    @NotNull(message = "reportType is required")
    private ReportType reportType;

    private String nodeId;

    @NotBlank(message = "requestedBy is required")
    private String requestedBy;

    @Size(min = 6, max = 30, message = "passwordProtection must be between 6 and 30 characters if enabled per VR-RPT-004")
    private String passwordProtection;

    public ReportRequestDTO() {}

    public ReportRequestDTO(String projectId, String campaignId, ReportType reportType, String nodeId, String requestedBy, String passwordProtection) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.reportType = reportType;
        this.nodeId = nodeId;
        this.requestedBy = requestedBy;
        this.passwordProtection = passwordProtection;
    }

    public static Builder builder() { return new Builder(); }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getPasswordProtection() { return passwordProtection; }
    public void setPasswordProtection(String passwordProtection) { this.passwordProtection = passwordProtection; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private ReportType reportType;
        private String nodeId;
        private String requestedBy;
        private String passwordProtection;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder reportType(ReportType reportType) { this.reportType = reportType; return this; }
        public Builder nodeId(String nodeId) { this.nodeId = nodeId; return this; }
        public Builder requestedBy(String requestedBy) { this.requestedBy = requestedBy; return this; }
        public Builder passwordProtection(String passwordProtection) { this.passwordProtection = passwordProtection; return this; }

        public ReportRequestDTO build() {
            return new ReportRequestDTO(projectId, campaignId, reportType, nodeId, requestedBy, passwordProtection);
        }
    }
}
