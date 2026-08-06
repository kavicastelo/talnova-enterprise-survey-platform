package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "DTO representing execution result of a single notification channel dispatch")
public class DispatchResultDTO {

    public enum DispatchStatus {
        SENT,
        FAILED
    }

    private String dispatchId;
    private String campaignId;
    private String employeeId;
    private DistributionChannel channel;
    private DispatchStatus status;
    private String externalMessageId;
    private String errorMessage;
    private Instant dispatchedAt;

    public DispatchResultDTO() {
    }

    public DispatchResultDTO(String dispatchId, String campaignId, String employeeId, DistributionChannel channel, DispatchStatus status, String externalMessageId, String errorMessage, Instant dispatchedAt) {
        this.dispatchId = dispatchId;
        this.campaignId = campaignId;
        this.employeeId = employeeId;
        this.channel = channel;
        this.status = status;
        this.externalMessageId = externalMessageId;
        this.errorMessage = errorMessage;
        this.dispatchedAt = dispatchedAt != null ? dispatchedAt : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDispatchId() { return dispatchId; }
    public void setDispatchId(String dispatchId) { this.dispatchId = dispatchId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public DistributionChannel getChannel() { return channel; }
    public void setChannel(DistributionChannel channel) { this.channel = channel; }

    public DispatchStatus getStatus() { return status; }
    public void setStatus(DispatchStatus status) { this.status = status; }

    public String getExternalMessageId() { return externalMessageId; }
    public void setExternalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public static class Builder {
        private String dispatchId;
        private String campaignId;
        private String employeeId;
        private DistributionChannel channel;
        private DispatchStatus status;
        private String externalMessageId;
        private String errorMessage;
        private Instant dispatchedAt = Instant.now();

        public Builder dispatchId(String dispatchId) { this.dispatchId = dispatchId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder channel(DistributionChannel channel) { this.channel = channel; return this; }
        public Builder status(DispatchStatus status) { this.status = status; return this; }
        public Builder externalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public Builder dispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; return this; }

        public DispatchResultDTO build() {
            return new DispatchResultDTO(dispatchId, campaignId, employeeId, channel, status, externalMessageId, errorMessage, dispatchedAt);
        }
    }
}
