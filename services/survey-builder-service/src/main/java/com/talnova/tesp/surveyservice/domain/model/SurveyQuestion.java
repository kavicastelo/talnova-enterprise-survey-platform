package com.talnova.tesp.surveyservice.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value object representing a survey question node in the AST hierarchy.
 */
public class SurveyQuestion {

    private String questionId;
    private QuestionType type;
    private String groupId;
    private Map<String, String> prompt = new HashMap<>();
    private boolean isMandatory;
    private List<LogicRule> logicRules = new ArrayList<>();

    public SurveyQuestion() {
    }

    public SurveyQuestion(String questionId, QuestionType type, String groupId, Map<String, String> prompt, boolean isMandatory, List<LogicRule> logicRules) {
        this.questionId = questionId;
        this.type = type;
        this.groupId = groupId;
        this.prompt = prompt != null ? prompt : new HashMap<>();
        this.isMandatory = isMandatory;
        this.logicRules = logicRules != null ? logicRules : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, String> getPrompt() {
        return prompt;
    }

    public void setPrompt(Map<String, String> prompt) {
        this.prompt = prompt;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public List<LogicRule> getLogicRules() {
        return logicRules;
    }

    public void setLogicRules(List<LogicRule> logicRules) {
        this.logicRules = logicRules;
    }

    public static class Builder {
        private String questionId;
        private QuestionType type;
        private String groupId;
        private Map<String, String> prompt = new HashMap<>();
        private boolean isMandatory;
        private List<LogicRule> logicRules = new ArrayList<>();

        public Builder questionId(String questionId) { this.questionId = questionId; return this; }
        public Builder type(QuestionType type) { this.type = type; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder prompt(Map<String, String> prompt) { this.prompt = prompt; return this; }
        public Builder isMandatory(boolean isMandatory) { this.isMandatory = isMandatory; return this; }
        public Builder logicRules(List<LogicRule> logicRules) { this.logicRules = logicRules; return this; }

        public SurveyQuestion build() {
            return new SurveyQuestion(questionId, type, groupId, prompt, isMandatory, logicRules);
        }
    }
}
