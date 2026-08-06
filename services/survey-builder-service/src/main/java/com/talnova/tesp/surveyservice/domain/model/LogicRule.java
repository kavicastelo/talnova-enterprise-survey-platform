package com.talnova.tesp.surveyservice.domain.model;

/**
 * Value object representing a declarative branching skip logic rule inside a question.
 */
public class LogicRule {

    private String ruleId;
    private LogicOperator operator;
    private String comparisonValue;
    private String targetPageId;

    public LogicRule() {
    }

    public LogicRule(String ruleId, LogicOperator operator, String comparisonValue, String targetPageId) {
        this.ruleId = ruleId;
        this.operator = operator;
        this.comparisonValue = comparisonValue;
        this.targetPageId = targetPageId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public LogicOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicOperator operator) {
        this.operator = operator;
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    public String getTargetPageId() {
        return targetPageId;
    }

    public void setTargetPageId(String targetPageId) {
        this.targetPageId = targetPageId;
    }

    public static class Builder {
        private String ruleId;
        private LogicOperator operator;
        private String comparisonValue;
        private String targetPageId;

        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder operator(LogicOperator operator) { this.operator = operator; return this; }
        public Builder comparisonValue(String comparisonValue) { this.comparisonValue = comparisonValue; return this; }
        public Builder targetPageId(String targetPageId) { this.targetPageId = targetPageId; return this; }

        public LogicRule build() {
            return new LogicRule(ruleId, operator, comparisonValue, targetPageId);
        }
    }
}
