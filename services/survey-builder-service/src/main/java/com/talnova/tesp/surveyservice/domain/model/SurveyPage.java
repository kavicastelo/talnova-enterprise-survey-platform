package com.talnova.tesp.surveyservice.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value object representing a survey page node containing sections.
 */
public class SurveyPage {

    private String pageId;
    private int pageOrder;
    private Map<String, String> title = new HashMap<>();
    private List<SurveySection> sections = new ArrayList<>();

    public SurveyPage() {
    }

    public SurveyPage(String pageId, int pageOrder, Map<String, String> title, List<SurveySection> sections) {
        this.pageId = pageId;
        this.pageOrder = pageOrder;
        this.title = title != null ? title : new HashMap<>();
        this.sections = sections != null ? sections : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public int getPageOrder() {
        return pageOrder;
    }

    public void setPageOrder(int pageOrder) {
        this.pageOrder = pageOrder;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public List<SurveySection> getSections() {
        return sections;
    }

    public void setSections(List<SurveySection> sections) {
        this.sections = sections;
    }

    public static class Builder {
        private String pageId;
        private int pageOrder;
        private Map<String, String> title = new HashMap<>();
        private List<SurveySection> sections = new ArrayList<>();

        public Builder pageId(String pageId) { this.pageId = pageId; return this; }
        public Builder pageOrder(int pageOrder) { this.pageOrder = pageOrder; return this; }
        public Builder title(Map<String, String> title) { this.title = title; return this; }
        public Builder sections(List<SurveySection> sections) { this.sections = sections; return this; }

        public SurveyPage build() {
            return new SurveyPage(pageId, pageOrder, title, sections);
        }
    }
}
