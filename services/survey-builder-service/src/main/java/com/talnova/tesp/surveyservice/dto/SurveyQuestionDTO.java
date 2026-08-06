package com.talnova.tesp.surveyservice.dto;

import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Data Transfer Object for survey question node")
public class SurveyQuestionDTO {

    @NotBlank(message = "questionId is mandatory")
    @Schema(description = "Unique question identifier", example = "Q-101")
    private String questionId;

    @NotNull(message = "type is mandatory")
    @Schema(description = "Supported question type", example = "LIKERT")
    private QuestionType type;

    @Schema(description = "Analytical theme question group ID", example = "GRP-LEADERSHIP")
    private String groupId;

    @Schema(description = "Localized question prompt map", example = "{\"en-US\": \"My manager provides clear direction.\"}")
    private Map<String, String> prompt = new HashMap<>();

    @Schema(description = "Flag indicating mandatory question", example = "true")
    private boolean isMandatory;

    @Schema(description = "List of branching logic rules")
    private List<LogicRuleDTO> logicRules = new ArrayList<>();

    public SurveyQuestionDTO() {
    }

    public SurveyQuestionDTO(String questionId, QuestionType type, String groupId, Map<String, String> prompt, boolean isMandatory, List<LogicRuleDTO> logicRules) {
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

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public Map<String, String> getPrompt() { return prompt; }
    public void setPrompt(Map<String, String> prompt) { this.prompt = prompt; }

    public boolean isMandatory() { return isMandatory; }
    public void setMandatory(boolean mandatory) { isMandatory = mandatory; }

    public List<LogicRuleDTO> getLogicRules() { return logicRules; }
    public void setLogicRules(List<LogicRuleDTO> logicRules) { this.logicRules = logicRules; }

    public static class Builder {
        private String questionId;
        private QuestionType type;
        private String groupId;
        private Map<String, String> prompt = new HashMap<>();
        private boolean isMandatory;
        private List<LogicRuleDTO> logicRules = new ArrayList<>();

        public Builder questionId(String questionId) { this.questionId = questionId; return this; }
        public Builder type(QuestionType type) { this.type = type; return this; }
        public Builder groupId(String groupId) { this.groupId = groupId; return this; }
        public Builder prompt(Map<String, String> prompt) { this.prompt = prompt; return this; }
        public Builder isMandatory(boolean isMandatory) { this.isMandatory = isMandatory; return this; }
        public Builder logicRules(List<LogicRuleDTO> logicRules) { this.logicRules = logicRules; return this; }

        public SurveyQuestionDTO build() {
            return new SurveyQuestionDTO(questionId, type, groupId, prompt, isMandatory, logicRules);
        }
    }
}
