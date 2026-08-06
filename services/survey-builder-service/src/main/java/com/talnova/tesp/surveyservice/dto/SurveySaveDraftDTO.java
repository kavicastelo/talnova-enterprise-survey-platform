package com.talnova.tesp.surveyservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Request payload for creating or saving a survey AST draft")
public class SurveySaveDraftDTO {

    @NotBlank(message = "projectId is mandatory")
    @Pattern(regexp = "^PRJ-[A-Z0-9]{4,10}$", message = "projectId must match pattern ^PRJ-[A-Z0-9]{4,10}$")
    @Schema(description = "Tenant project ID", example = "PRJ-99201")
    private String projectId;

    @NotBlank(message = "surveyId is mandatory")
    @Pattern(regexp = "^SRV-[A-Za-z0-9_-]{3,20}$", message = "surveyId must match pattern ^SRV-[A-Za-z0-9_-]{3,20}$")
    @Schema(description = "Survey identifier", example = "SRV-5001")
    private String surveyId;

    @Schema(description = "Localized title dictionary map", example = "{\"en-US\": \"2026 Annual Employee Engagement Survey\"}")
    private Map<String, String> title = new HashMap<>();

    @Schema(description = "Localized description dictionary map", example = "{\"en-US\": \"Company-wide annual pulse questionnaire.\"}")
    private Map<String, String> description = new HashMap<>();

    @Schema(description = "List of survey pages")
    private List<SurveyPageDTO> pages = new ArrayList<>();

    public SurveySaveDraftDTO() {
    }

    public SurveySaveDraftDTO(String projectId, String surveyId, Map<String, String> title, Map<String, String> description, List<SurveyPageDTO> pages) {
        this.projectId = projectId;
        this.surveyId = surveyId;
        this.title = title != null ? title : new HashMap<>();
        this.description = description != null ? description : new HashMap<>();
        this.pages = pages != null ? pages : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getSurveyId() { return surveyId; }
    public void setSurveyId(String surveyId) { this.surveyId = surveyId; }

    public Map<String, String> getTitle() { return title; }
    public void setTitle(Map<String, String> title) { this.title = title; }

    public Map<String, String> getDescription() { return description; }
    public void setDescription(Map<String, String> description) { this.description = description; }

    public List<SurveyPageDTO> getPages() { return pages; }
    public void setPages(List<SurveyPageDTO> pages) { this.pages = pages; }

    public static class Builder {
        private String projectId;
        private String surveyId;
        private Map<String, String> title = new HashMap<>();
        private Map<String, String> description = new HashMap<>();
        private List<SurveyPageDTO> pages = new ArrayList<>();

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder surveyId(String surveyId) { this.surveyId = surveyId; return this; }
        public Builder title(Map<String, String> title) { this.title = title; return this; }
        public Builder description(Map<String, String> description) { this.description = description; return this; }
        public Builder pages(List<SurveyPageDTO> pages) { this.pages = pages; return this; }

        public SurveySaveDraftDTO build() {
            return new SurveySaveDraftDTO(projectId, surveyId, title, description, pages);
        }
    }
}
