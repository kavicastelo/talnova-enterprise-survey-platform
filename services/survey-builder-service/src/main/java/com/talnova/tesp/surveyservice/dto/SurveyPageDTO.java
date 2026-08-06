package com.talnova.tesp.surveyservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Data Transfer Object for survey page node")
public class SurveyPageDTO {

    @NotBlank(message = "pageId is mandatory")
    @Schema(description = "Unique page identifier", example = "PAGE-1")
    private String pageId;

    @Min(value = 1, message = "pageOrder must be at least 1")
    @Schema(description = "1-based order index of page", example = "1")
    private int pageOrder;

    @Schema(description = "Localized page title map", example = "{\"en-US\": \"Page 1: Core Leadership\"}")
    private Map<String, String> title = new HashMap<>();

    @Schema(description = "List of sections inside this page")
    private List<SurveySectionDTO> sections = new ArrayList<>();

    public SurveyPageDTO() {
    }

    public SurveyPageDTO(String pageId, int pageOrder, Map<String, String> title, List<SurveySectionDTO> sections) {
        this.pageId = pageId;
        this.pageOrder = pageOrder;
        this.title = title != null ? title : new HashMap<>();
        this.sections = sections != null ? sections : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    public int getPageOrder() { return pageOrder; }
    public void setPageOrder(int pageOrder) { this.pageOrder = pageOrder; }

    public Map<String, String> getTitle() { return title; }
    public void setTitle(Map<String, String> title) { this.title = title; }

    public List<SurveySectionDTO> getSections() { return sections; }
    public void setSections(List<SurveySectionDTO> sections) { this.sections = sections; }

    public static class Builder {
        private String pageId;
        private int pageOrder;
        private Map<String, String> title = new HashMap<>();
        private List<SurveySectionDTO> sections = new ArrayList<>();

        public Builder pageId(String pageId) { this.pageId = pageId; return this; }
        public Builder pageOrder(int pageOrder) { this.pageOrder = pageOrder; return this; }
        public Builder title(Map<String, String> title) { this.title = title; return this; }
        public Builder sections(List<SurveySectionDTO> sections) { this.sections = sections; return this; }

        public SurveyPageDTO build() {
            return new SurveyPageDTO(pageId, pageOrder, title, sections);
        }
    }
}
