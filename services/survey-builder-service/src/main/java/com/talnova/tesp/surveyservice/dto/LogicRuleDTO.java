package com.talnova.tesp.surveyservice.dto;

import com.talnova.tesp.surveyservice.domain.model.LogicOperator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for declarative branching skip logic rule")
public class LogicRuleDTO {

    @NotBlank(message = "ruleId is mandatory")
    @Schema(description = "Unique logic rule identifier", example = "RULE-101")
    private String ruleId;

    @NotNull(message = "operator is mandatory")
    @Schema(description = "Comparison operator", example = "LESS_THAN")
    private LogicOperator operator;

    @Schema(description = "Value to compare answer against", example = "7")
    private String comparisonValue;

    @NotBlank(message = "targetPageId is mandatory")
    @Schema(description = "Target page ID to skip to", example = "PAGE-3")
    private String targetPageId;

    public LogicRuleDTO() {
    }

    public LogicRuleDTO(String ruleId, LogicOperator operator, String comparisonValue, String targetPageId) {
        this.ruleId = ruleId;
        this.operator = operator;
        this.comparisonValue = comparisonValue;
        this.targetPageId = targetPageId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public LogicOperator getOperator() { return operator; }
    public void setOperator(LogicOperator operator) { this.operator = operator; }

    public String getComparisonValue() { return comparisonValue; }
    public void setComparisonValue(String comparisonValue) { this.comparisonValue = comparisonValue; }

    public String getTargetPageId() { return targetPageId; }
    public void setTargetPageId(String targetPageId) { this.targetPageId = targetPageId; }

    public static class Builder {
        private String ruleId;
        private LogicOperator operator;
        private String comparisonValue;
        private String targetPageId;

        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder operator(LogicOperator operator) { this.operator = operator; return this; }
        public Builder comparisonValue(String comparisonValue) { this.comparisonValue = comparisonValue; return this; }
        public Builder targetPageId(String targetPageId) { this.targetPageId = targetPageId; return this; }

        public LogicRuleDTO build() {
            return new LogicRuleDTO(ruleId, operator, comparisonValue, targetPageId);
        }
    }
}
