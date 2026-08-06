package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request payload for bulk token generation")
public class TokenBatchGenerationRequestDTO {

    @NotBlank(message = "projectId is mandatory")
    @Schema(description = "Project ID", example = "PRJ-99201")
    private String projectId;

    @NotBlank(message = "campaignId is mandatory")
    @Schema(description = "Campaign ID", example = "CMP-1001")
    private String campaignId;

    @NotNull(message = "anonymityLevel is mandatory")
    @Schema(description = "Anonymity protection tier", example = "SEMI_ANONYMOUS")
    private AnonymityLevel anonymityLevel;

    @NotEmpty(message = "employeeIds list must not be empty")
    @Schema(description = "Target employee ID list", example = "[\"EMP-10020\", \"EMP-10021\"]")
    private List<String> employeeIds = new ArrayList<>();

    @Schema(description = "Optional campaign secret salt for HMAC-SHA256 generation")
    private String campaignSalt;

    public TokenBatchGenerationRequestDTO() {
    }

    public TokenBatchGenerationRequestDTO(String projectId, String campaignId, AnonymityLevel anonymityLevel, List<String> employeeIds, String campaignSalt) {
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.anonymityLevel = anonymityLevel;
        this.employeeIds = employeeIds != null ? employeeIds : new ArrayList<>();
        this.campaignSalt = campaignSalt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public List<String> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<String> employeeIds) { this.employeeIds = employeeIds; }

    public String getCampaignSalt() { return campaignSalt; }
    public void setCampaignSalt(String campaignSalt) { this.campaignSalt = campaignSalt; }

    public static class Builder {
        private String projectId;
        private String campaignId;
        private AnonymityLevel anonymityLevel;
        private List<String> employeeIds = new ArrayList<>();
        private String campaignSalt;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder employeeIds(List<String> employeeIds) { this.employeeIds = employeeIds; return this; }
        public Builder campaignSalt(String campaignSalt) { this.campaignSalt = campaignSalt; return this; }

        public TokenBatchGenerationRequestDTO build() {
            return new TokenBatchGenerationRequestDTO(projectId, campaignId, anonymityLevel, employeeIds, campaignSalt);
        }
    }
}
