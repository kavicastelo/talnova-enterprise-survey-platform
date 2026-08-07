package com.talnova.tesp.aiservice.dto;

import java.time.Instant;
import java.util.List;

public class ExecutiveSummaryDTO {

    private String nodeScope;
    private String summaryTitle;
    private List<String> topStrengths;
    private List<String> topConcerns;
    private List<String> recommendations;
    private Instant generatedAt;

    public ExecutiveSummaryDTO() {}

    public ExecutiveSummaryDTO(String nodeScope, String summaryTitle, List<String> topStrengths, List<String> topConcerns, List<String> recommendations, Instant generatedAt) {
        this.nodeScope = nodeScope;
        this.summaryTitle = summaryTitle;
        this.topStrengths = topStrengths;
        this.topConcerns = topConcerns;
        this.recommendations = recommendations;
        this.generatedAt = generatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getNodeScope() { return nodeScope; }
    public void setNodeScope(String nodeScope) { this.nodeScope = nodeScope; }

    public String getSummaryTitle() { return summaryTitle; }
    public void setSummaryTitle(String summaryTitle) { this.summaryTitle = summaryTitle; }

    public List<String> getTopStrengths() { return topStrengths; }
    public void setTopStrengths(List<String> topStrengths) { this.topStrengths = topStrengths; }

    public List<String> getTopConcerns() { return topConcerns; }
    public void setTopConcerns(List<String> topConcerns) { this.topConcerns = topConcerns; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public static class Builder {
        private String nodeScope;
        private String summaryTitle;
        private List<String> topStrengths;
        private List<String> topConcerns;
        private List<String> recommendations;
        private Instant generatedAt;

        public Builder nodeScope(String nodeScope) { this.nodeScope = nodeScope; return this; }
        public Builder summaryTitle(String summaryTitle) { this.summaryTitle = summaryTitle; return this; }
        public Builder topStrengths(List<String> topStrengths) { this.topStrengths = topStrengths; return this; }
        public Builder topConcerns(List<String> topConcerns) { this.topConcerns = topConcerns; return this; }
        public Builder recommendations(List<String> recommendations) { this.recommendations = recommendations; return this; }
        public Builder generatedAt(Instant generatedAt) { this.generatedAt = generatedAt; return this; }

        public ExecutiveSummaryDTO build() {
            return new ExecutiveSummaryDTO(nodeScope, summaryTitle, topStrengths, topConcerns, recommendations, generatedAt);
        }
    }
}
