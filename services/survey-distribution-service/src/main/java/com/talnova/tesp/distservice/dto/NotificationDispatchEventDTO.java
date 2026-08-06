package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Kafka event payload published to tesp.distribution.dispatch-events.v1 topic")
public class NotificationDispatchEventDTO {

    private String eventId;
    private String projectId;
    private String campaignId;
    private String employeeId;
    private String recipientContact;
    private DistributionChannel channel;
    private String subject;
    private String bodyText;
    private String surveyUrl;
    private String kioskPin;
    private Instant timestamp;

    public NotificationDispatchEventDTO() {
        this.eventId = "EVT-DISP-" + UUID.randomUUID().toString().substring(0, 8);
        this.timestamp = Instant.now();
    }

    public NotificationDispatchEventDTO(String eventId, String projectId, String campaignId, String employeeId, String recipientContact, DistributionChannel channel, String subject, String bodyText, String surveyUrl, String kioskPin, Instant timestamp) {
        this.eventId = eventId != null ? eventId : "EVT-DISP-" + UUID.randomUUID().toString().substring(0, 8);
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.employeeId = employeeId;
        this.recipientContact = recipientContact;
        this.channel = channel;
        this.subject = subject;
        this.bodyText = bodyText;
        this.surveyUrl = surveyUrl;
        this.kioskPin = kioskPin;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getRecipientContact() { return recipientContact; }
    public void setRecipientContact(String recipientContact) { this.recipientContact = recipientContact; }

    public DistributionChannel getChannel() { return channel; }
    public void setChannel(DistributionChannel channel) { this.channel = channel; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }

    public String getSurveyUrl() { return surveyUrl; }
    public void setSurveyUrl(String surveyUrl) { this.surveyUrl = surveyUrl; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public static class Builder {
        private String eventId = "EVT-DISP-" + UUID.randomUUID().toString().substring(0, 8);
        private String projectId;
        private String campaignId;
        private String employeeId;
        private String recipientContact;
        private DistributionChannel channel;
        private String subject;
        private String bodyText;
        private String surveyUrl;
        private String kioskPin;
        private Instant timestamp = Instant.now();

        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder recipientContact(String recipientContact) { this.recipientContact = recipientContact; return this; }
        public Builder channel(DistributionChannel channel) { this.channel = channel; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder bodyText(String bodyText) { this.bodyText = bodyText; return this; }
        public Builder surveyUrl(String surveyUrl) { this.surveyUrl = surveyUrl; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public NotificationDispatchEventDTO build() {
            return new NotificationDispatchEventDTO(eventId, projectId, campaignId, employeeId, recipientContact, channel, subject, bodyText, surveyUrl, kioskPin, timestamp);
        }
    }
}
