package com.talnova.tesp.ingestionservice.domain.model;

import java.util.List;

public class AnswerItem {

    private String questionId;
    private String questionType;
    private Double numericValue;
    private String textValue;
    private List<String> selectedOptions;

    public AnswerItem() {
    }

    public AnswerItem(String questionId, String questionType, Double numericValue, String textValue, List<String> selectedOptions) {
        this.questionId = questionId;
        this.questionType = questionType;
        this.numericValue = numericValue;
        this.textValue = textValue;
        this.selectedOptions = selectedOptions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public Double getNumericValue() { return numericValue; }
    public void setNumericValue(Double numericValue) { this.numericValue = numericValue; }

    public String getTextValue() { return textValue; }
    public void setTextValue(String textValue) { this.textValue = textValue; }

    public List<String> getSelectedOptions() { return selectedOptions; }
    public void setSelectedOptions(List<String> selectedOptions) { this.selectedOptions = selectedOptions; }

    public static class Builder {
        private String questionId;
        private String questionType;
        private Double numericValue;
        private String textValue;
        private List<String> selectedOptions;

        public Builder questionId(String questionId) { this.questionId = questionId; return this; }
        public Builder questionType(String questionType) { this.questionType = questionType; return this; }
        public Builder numericValue(Double numericValue) { this.numericValue = numericValue; return this; }
        public Builder textValue(String textValue) { this.textValue = textValue; return this; }
        public Builder selectedOptions(List<String> selectedOptions) { this.selectedOptions = selectedOptions; return this; }

        public AnswerItem build() {
            return new AnswerItem(questionId, questionType, numericValue, textValue, selectedOptions);
        }
    }
}
