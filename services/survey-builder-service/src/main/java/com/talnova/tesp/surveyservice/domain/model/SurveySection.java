package com.talnova.tesp.surveyservice.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value object representing a survey section node containing questions.
 */
public class SurveySection {

    private String sectionId;
    private Map<String, String> title = new HashMap<>();
    private List<SurveyQuestion> questions = new ArrayList<>();

    public SurveySection() {
    }

    public SurveySection(String sectionId, Map<String, String> title, List<SurveyQuestion> questions) {
        this.sectionId = sectionId;
        this.title = title != null ? title : new HashMap<>();
        this.questions = questions != null ? questions : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

    public static class Builder {
        private String sectionId;
        private Map<String, String> title = new HashMap<>();
        private List<SurveyQuestion> questions = new ArrayList<>();

        public Builder sectionId(String sectionId) { this.sectionId = sectionId; return this; }
        public Builder title(Map<String, String> title) { this.title = title; return this; }
        public Builder questions(List<SurveyQuestion> questions) { this.questions = questions; return this; }

        public SurveySection build() {
            return new SurveySection(sectionId, title, questions);
        }
    }
}
