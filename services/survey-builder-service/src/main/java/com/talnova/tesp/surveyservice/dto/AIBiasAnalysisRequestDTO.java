package com.talnova.tesp.surveyservice.dto;

import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for AI leading question & bias inspection")
public class AIBiasAnalysisRequestDTO {

    @NotBlank(message = "promptText is mandatory")
    @Schema(description = "Question prompt text string to analyze", example = "How great is our leadership team?")
    private String promptText;

    @Schema(description = "Question type", example = "LIKERT")
    private QuestionType questionType;

    public AIBiasAnalysisRequestDTO() {
    }

    public AIBiasAnalysisRequestDTO(String promptText, QuestionType questionType) {
        this.promptText = promptText;
        this.questionType = questionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPromptText() { return promptText; }
    public void setPromptText(String promptText) { this.promptText = promptText; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public static class Builder {
        private String promptText;
        private QuestionType questionType;

        public Builder promptText(String promptText) { this.promptText = promptText; return this; }
        public Builder questionType(QuestionType questionType) { this.questionType = questionType; return this; }

        public AIBiasAnalysisRequestDTO build() {
            return new AIBiasAnalysisRequestDTO(promptText, questionType);
        }
    }
}
