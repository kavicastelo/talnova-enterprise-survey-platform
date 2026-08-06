package com.talnova.tesp.surveyservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload from AI leading question & bias inspection")
public class AIBiasAnalysisResponseDTO {

    @JsonProperty("isBiased")
    private boolean isBiased;
    private String biasType;
    private double score;
    private String suggestion;
    private String explanation;

    public AIBiasAnalysisResponseDTO() {
    }

    public AIBiasAnalysisResponseDTO(boolean isBiased, String biasType, double score, String suggestion, String explanation) {
        this.isBiased = isBiased;
        this.biasType = biasType;
        this.score = score;
        this.suggestion = suggestion;
        this.explanation = explanation;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty("isBiased")
    public boolean isBiased() { return isBiased; }
    public void setBiased(boolean biased) { isBiased = biased; }

    public String getBiasType() { return biasType; }
    public void setBiasType(String biasType) { this.biasType = biasType; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public static class Builder {
        private boolean isBiased;
        private String biasType;
        private double score;
        private String suggestion;
        private String explanation;

        public Builder isBiased(boolean isBiased) { this.isBiased = isBiased; return this; }
        public Builder biasType(String biasType) { this.biasType = biasType; return this; }
        public Builder score(double score) { this.score = score; return this; }
        public Builder suggestion(String suggestion) { this.suggestion = suggestion; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }

        public AIBiasAnalysisResponseDTO build() {
            return new AIBiasAnalysisResponseDTO(isBiased, biasType, score, suggestion, explanation);
        }
    }
}
