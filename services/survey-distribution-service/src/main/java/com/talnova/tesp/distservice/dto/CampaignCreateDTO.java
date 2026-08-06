package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request payload for creating and launching a survey distribution campaign")
public class CampaignCreateDTO {

    @NotBlank(message = "projectId is mandatory")
    @Schema(description = "Project ID scope", example = "PRJ-99201")
    private String projectId;

    @NotBlank(message = "campaignId is mandatory")
    @Pattern(regexp = "^CMP-[A-Za-z0-9_-]{3,20}$", message = "VR-DST-001: Campaign ID must match regex ^CMP-[A-Za-z0-9_-]{3,20}$")
    @Schema(description = "Unique campaign identifier", example = "CMP-1001")
    private String campaignId;

    @NotBlank(message = "surveyId is mandatory")
    @Schema(description = "Target survey questionnaire ID", example = "SRV-5001")
    private String surveyId;

    @NotNull(message = "surveyVersion is mandatory")
    @Schema(description = "Published survey version", example = "1")
    private Integer surveyVersion;

    @NotBlank(message = "title is mandatory")
    @Schema(description = "Human readable campaign title", example = "Q3 Employee Pulse Survey")
    private String title;

    @NotNull(message = "anonymityLevel is mandatory")
    @Schema(description = "Anonymity protection level", example = "SEMI_ANONYMOUS")
    private AnonymityLevel anonymityLevel;

    @NotEmpty(message = "VR-DST-004: At least one distribution channel is required")
    @Schema(description = "List of distribution channels", example = "[\"EMAIL\", \"TEAMS\"]")
    private List<DistributionChannel> channels = new ArrayList<>();

    @Schema(description = "Target audience configuration")
    private TargetAudienceDTO targetAudience;

    @Schema(description = "Schedule and reminder configuration")
    private CampaignScheduleDTO schedule;

    @NotNull(message = "startDate is mandatory")
    @Schema(description = "Campaign launch start date")
    private Instant startDate;

    @NotNull(message = "expirationDate is mandatory")
    @Schema(description = "Campaign expiration date")
    private Instant expirationDate;

    public CampaignCreateDTO() {
    }

    public CampaignCreateDTO(String projectId, String campaignId, String surveyId, Integer surveyVersion, String title, AnonymityLevel anonymityLevel, List<DistributionChannel> channels, TargetAudienceDTO targetAudience, CampaignScheduleDTO schedule, Instant startDate, Instant expirationDate) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.surveyId = surveyId;
        this.surveyVersion = surveyVersion;
        this.title = title;
        this.anonymityLevel = anonymityLevel;
        this.channels = channels != null ? channels : new ArrayList<>();
        this.targetAudience = targetAudience;
        this.schedule = schedule;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public Integer getSurveyVersion() { return surveyVersion; }
    public void setSurveyVersion(Integer surveyVersion) { this.surveyVersion = surveyVersion; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public List<DistributionChannel> getChannels() { return channels; }
    public void setChannels(List<DistributionChannel> channels) { this.channels = channels; }

    public TargetAudienceDTO getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudienceDTO targetAudience) { this.targetAudience = targetAudience; }

    public CampaignScheduleDTO getSchedule() { return schedule; }
    public void setSchedule(CampaignScheduleDTO schedule) { this.schedule = schedule; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Instant expirationDate) { this.expirationDate = expirationDate; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private String surveyId;
        private Integer surveyVersion;
        private String title;
        private AnonymityLevel anonymityLevel;
        private List<DistributionChannel> channels = new ArrayList<>();
        private TargetAudienceDTO targetAudience;
        private CampaignScheduleDTO schedule;
        private Instant startDate;
        private Instant expirationDate;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder surveyVersion(Integer surveyVersion) { this.surveyVersion = surveyVersion; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder channels(List<DistributionChannel> channels) { this.channels = channels; return this; }
        public Builder targetAudience(TargetAudienceDTO targetAudience) { this.targetAudience = targetAudience; return this; }
        public Builder schedule(CampaignScheduleDTO schedule) { this.schedule = schedule; return this; }
        public Builder startDate(Instant startDate) { this.startDate = startDate; return this; }
        public Builder expirationDate(Instant expirationDate) { this.expirationDate = expirationDate; return this; }

        public CampaignCreateDTO build() {
            return new CampaignCreateDTO(projectId, campaignId, surveyId, surveyVersion, title, anonymityLevel, channels, targetAudience, schedule, startDate, expirationDate);
        }
    }
}
