package com.talnova.tesp.distservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "Payload DTO for AWS SES & Twilio delivery status webhook ingestion")
public class DeliveryWebhookPayloadDTO {

    public enum Provider {
        AWS_SES,
        TWILIO
    }

    public enum EventType {
        DELIVERED,
        OPENED,
        BOUNCED,
        FAILED
    }

    @NotBlank(message = "projectId is mandatory")
    private String projectId;

    @NotBlank(message = "campaignId is mandatory")
    private String campaignId;

    @NotNull(message = "provider is mandatory")
    private Provider provider;

    @NotNull(message = "eventType is mandatory")
    private EventType eventType;

    private String externalMessageId;
    private String recipientContact;
    private Instant timestamp;

    public DeliveryWebhookPayloadDTO() {
        this.timestamp = Instant.now();
    }

    public DeliveryWebhookPayloadDTO(String projectId, String campaignId, Provider provider, EventType eventType, String externalMessageId, String recipientContact, Instant timestamp) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.provider = provider;
        this.eventType = eventType;
        this.externalMessageId = externalMessageId;
        this.recipientContact = recipientContact;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getExternalMessageId() { return externalMessageId; }
    public void setExternalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; }

    public String getRecipientContact() { return recipientContact; }
    public void setRecipientContact(String recipientContact) { this.recipientContact = recipientContact; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private Provider provider;
        private EventType eventType;
        private String externalMessageId;
        private String recipientContact;
        private Instant timestamp = Instant.now();

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder provider(Provider provider) { this.provider = provider; return this; }
        public Builder eventType(EventType eventType) { this.eventType = eventType; return this; }
        public Builder externalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; return this; }
        public Builder recipientContact(String recipientContact) { this.recipientContact = recipientContact; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public DeliveryWebhookPayloadDTO build() {
            return new DeliveryWebhookPayloadDTO(projectId, campaignId, provider, eventType, externalMessageId, recipientContact, timestamp);
        }
    }
}
