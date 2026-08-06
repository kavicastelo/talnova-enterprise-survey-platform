package com.talnova.tesp.distservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload containing AI predicted optimal dispatch hour and confidence score")
public class OptimalDispatchPredictionResponseDTO {

    private String employeeId;
    private int recommendedHour;
    private String recommendedTimeString;
    private double confidenceScore;
    private String reasoning;

    public OptimalDispatchPredictionResponseDTO() {
    }

    public OptimalDispatchPredictionResponseDTO(String employeeId, int recommendedHour, String recommendedTimeString, double confidenceScore, String reasoning) {
        this.employeeId = employeeId;
        this.recommendedHour = recommendedHour;
        this.recommendedTimeString = recommendedTimeString;
        this.confidenceScore = confidenceScore;
        this.reasoning = reasoning;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public int getRecommendedHour() { return recommendedHour; }
    public void setRecommendedHour(int recommendedHour) { this.recommendedHour = recommendedHour; }

    public String getRecommendedTimeString() { return recommendedTimeString; }
    public void setRecommendedTimeString(String recommendedTimeString) { this.recommendedTimeString = recommendedTimeString; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public static class Builder {
        private String employeeId;
        private int recommendedHour;
        private String recommendedTimeString;
        private double confidenceScore;
        private String reasoning;

        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder recommendedHour(int recommendedHour) { this.recommendedHour = recommendedHour; return this; }
        public Builder recommendedTimeString(String recommendedTimeString) { this.recommendedTimeString = recommendedTimeString; return this; }
        public Builder confidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public Builder reasoning(String reasoning) { this.reasoning = reasoning; return this; }

        public OptimalDispatchPredictionResponseDTO build() {
            return new OptimalDispatchPredictionResponseDTO(employeeId, recommendedHour, recommendedTimeString, confidenceScore, reasoning);
        }
    }
}
