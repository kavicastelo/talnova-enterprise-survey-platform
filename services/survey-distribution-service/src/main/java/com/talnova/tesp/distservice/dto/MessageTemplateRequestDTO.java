package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "DTO for message template hydration requests")
public class MessageTemplateRequestDTO {

    private String projectId;
    private String campaignId;
    private String campaignTitle;
    private String employeeId;
    private String recipientContact;
    private DistributionChannel channel;
    private String locale;
    private String token;
    private String kioskPin;
    private Map<String, String> customVariables = new HashMap<>();

    public MessageTemplateRequestDTO() {
    }

    public MessageTemplateRequestDTO(String projectId, String campaignId, String campaignTitle, String employeeId, String recipientContact, DistributionChannel channel, String locale, String token, String kioskPin, Map<String, String> customVariables) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.campaignTitle = campaignTitle;
        this.employeeId = employeeId;
        this.recipientContact = recipientContact;
        this.channel = channel;
        this.locale = locale != null ? locale : "en-US";
        this.token = token;
        this.kioskPin = kioskPin;
        this.customVariables = customVariables != null ? customVariables : new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getCampaignTitle() { return campaignTitle; }
    public void setCampaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getRecipientContact() { return recipientContact; }
    public void setRecipientContact(String recipientContact) { this.recipientContact = recipientContact; }

    public DistributionChannel getChannel() { return channel; }
    public void setChannel(DistributionChannel channel) { this.channel = channel; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public Map<String, String> getCustomVariables() { return customVariables; }
    public void setCustomVariables(Map<String, String> customVariables) { this.customVariables = customVariables; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private String campaignTitle;
        private String employeeId;
        private String recipientContact;
        private DistributionChannel channel;
        private String locale = "en-US";
        private String token;
        private String kioskPin;
        private Map<String, String> customVariables = new HashMap<>();

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder campaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder recipientContact(String recipientContact) { this.recipientContact = recipientContact; return this; }
        public Builder channel(DistributionChannel channel) { this.channel = channel; return this; }
        public Builder locale(String locale) { this.locale = locale; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }
        public Builder customVariables(Map<String, String> customVariables) { this.customVariables = customVariables; return this; }

        public MessageTemplateRequestDTO build() {
            return new MessageTemplateRequestDTO(projectId, campaignId, campaignTitle, employeeId, recipientContact, channel, locale, token, kioskPin, customVariables);
        }
    }
}
