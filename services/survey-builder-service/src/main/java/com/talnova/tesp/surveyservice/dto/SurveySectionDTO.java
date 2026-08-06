package com.talnova.tesp.surveyservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Data Transfer Object for survey section node")
public class SurveySectionDTO {

    @NotBlank(message = "sectionId is mandatory")
    @Schema(description = "Unique section identifier", example = "SEC-LEADERSHIP")
    private String sectionId;

    @Schema(description = "Localized section title map", example = "{\"en-US\": \"Leadership Effectiveness\"}")
    private Map<String, String> title = new HashMap<>();

    @Schema(description = "List of questions inside this section")
    private List<SurveyQuestionDTO> questions = new ArrayList<>();

    public SurveySectionDTO() {
    }

    public SurveySectionDTO(String sectionId, Map<String, String> title, List<SurveyQuestionDTO> questions) {
        this.sectionId = sectionId;
        this.title = title != null ? title : new HashMap<>();
        this.questions = questions != null ? questions : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public Map<String, String> getTitle() { return title; }
    public void setTitle(Map<String, String> title) { this.title = title; }

    public List<SurveyQuestionDTO> getQuestions() { return questions; }
    public void setQuestions(List<SurveyQuestionDTO> questions) { this.questions = questions; }

    public static class Builder {
        private String sectionId;
        private Map<String, String> title = new HashMap<>();
        private List<SurveyQuestionDTO> questions = new ArrayList<>();

        public Builder sectionId(String sectionId) { this.sectionId = sectionId; return this; }
        public Builder title(Map<String, String> title) { this.title = title; return this; }
        public Builder questions(List<SurveyQuestionDTO> questions) { this.questions = questions; return this; }

        public SurveySectionDTO build() {
            return new SurveySectionDTO(sectionId, title, questions);
        }
    }
}
