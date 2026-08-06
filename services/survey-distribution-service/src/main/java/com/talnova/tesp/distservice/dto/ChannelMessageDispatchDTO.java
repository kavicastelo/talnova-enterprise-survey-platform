package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a hydrated message payload ready for multi-channel dispatch")
public class ChannelMessageDispatchDTO {

    private String projectId;
    private String campaignId;
    private String employeeId;
    private String recipientContact;
    private DistributionChannel channel;
    private String subject;
    private String bodyHtml;
    private String bodyText;
    private String surveyUrl;
    private String kioskPin;

    public ChannelMessageDispatchDTO() {
    }

    public ChannelMessageDispatchDTO(String projectId, String campaignId, String employeeId, String recipientContact, DistributionChannel channel, String subject, String bodyHtml, String bodyText, String surveyUrl, String kioskPin) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.employeeId = employeeId;
        this.recipientContact = recipientContact;
        this.channel = channel;
        this.subject = subject;
        this.bodyHtml = bodyHtml;
        this.bodyText = bodyText;
        this.surveyUrl = surveyUrl;
        this.kioskPin = kioskPin;
    }

    public static Builder builder() {
        return new Builder();
    }

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

    public String getBodyHtml() { return bodyHtml; }
    public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }

    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }

    public String getSurveyUrl() { return surveyUrl; }
    public void setSurveyUrl(String surveyUrl) { this.surveyUrl = surveyUrl; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private String employeeId;
        private String recipientContact;
        private DistributionChannel channel;
        private String subject;
        private String bodyHtml;
        private String bodyText;
        private String surveyUrl;
        private String kioskPin;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder recipientContact(String recipientContact) { this.recipientContact = recipientContact; return this; }
        public Builder channel(DistributionChannel channel) { this.channel = channel; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder bodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; return this; }
        public Builder bodyText(String bodyText) { this.bodyText = bodyText; return this; }
        public Builder surveyUrl(String surveyUrl) { this.surveyUrl = surveyUrl; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }

        public ChannelMessageDispatchDTO build() {
            return new ChannelMessageDispatchDTO(projectId, campaignId, employeeId, recipientContact, channel, subject, bodyHtml, bodyText, surveyUrl, kioskPin);
        }
    }
}
