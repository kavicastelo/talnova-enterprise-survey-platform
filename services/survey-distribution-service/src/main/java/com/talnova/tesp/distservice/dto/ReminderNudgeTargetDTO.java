package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO representing an uncompleted survey recipient target for scheduled reminder nudges")
public class ReminderNudgeTargetDTO {

    private String projectId;
    private String campaignId;
    private String employeeId;
    private String token;
    private String kioskPin;
    private AnonymityLevel anonymityLevel;
    private List<DistributionChannel> channels = new ArrayList<>();

    public ReminderNudgeTargetDTO() {
    }

    public ReminderNudgeTargetDTO(String projectId, String campaignId, String employeeId, String token, String kioskPin, AnonymityLevel anonymityLevel, List<DistributionChannel> channels) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.employeeId = employeeId;
        this.token = token;
        this.kioskPin = kioskPin;
        this.anonymityLevel = anonymityLevel;
        this.channels = channels != null ? channels : new ArrayList<>();
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

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public List<DistributionChannel> getChannels() { return channels; }
    public void setChannels(List<DistributionChannel> channels) { this.channels = channels; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private String employeeId;
        private String token;
        private String kioskPin;
        private AnonymityLevel anonymityLevel;
        private List<DistributionChannel> channels = new ArrayList<>();

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder channels(List<DistributionChannel> channels) { this.channels = channels; return this; }

        public ReminderNudgeTargetDTO build() {
            return new ReminderNudgeTargetDTO(projectId, campaignId, employeeId, token, kioskPin, anonymityLevel, channels);
        }
    }
}
