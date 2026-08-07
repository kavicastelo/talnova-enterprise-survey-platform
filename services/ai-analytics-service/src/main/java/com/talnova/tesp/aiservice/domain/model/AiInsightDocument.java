package com.talnova.tesp.aiservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "ai_insights")
@CompoundIndexes({
        @CompoundIndex(name = "idx_proj_campaign", def = "{'projectId': 1, 'campaignId': 1}"),
        @CompoundIndex(name = "idx_response_id", def = "{'responseId': 1}"),
        @CompoundIndex(name = "idx_proj_risk_severity", def = "{'projectId': 1, 'riskSeverity': 1}")
})
public class AiInsightDocument {

    @Id
    private String id;
    private String projectId;
    private String campaignId;
    private String responseId;
    private String questionId;
    private String sanitizedText;
    private Double sentimentScore;
    private SentimentLabel sentimentLabel;
    private Double confidence;
    private List<String> themes;
    private String riskCategory;
    private RiskSeverity riskSeverity;
    private HumanOverride humanOverride;
    private Instant createdAt;

    public AiInsightDocument() {}

    public AiInsightDocument(String id, String projectId, String campaignId, String responseId, String questionId, String sanitizedText, Double sentimentScore, SentimentLabel sentimentLabel, Double confidence, List<String> themes, String riskCategory, RiskSeverity riskSeverity, HumanOverride humanOverride, Instant createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.responseId = responseId;
        this.questionId = questionId;
        this.sanitizedText = sanitizedText;
        this.sentimentScore = sentimentScore;
        this.sentimentLabel = sentimentLabel;
        this.confidence = confidence;
        this.themes = themes;
        this.riskCategory = riskCategory;
        this.riskSeverity = riskSeverity;
        this.humanOverride = humanOverride;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getSanitizedText() { return sanitizedText; }
    public void setSanitizedText(String sanitizedText) { this.sanitizedText = sanitizedText; }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }

    public SentimentLabel getSentimentLabel() { return sentimentLabel; }
    public void setSentimentLabel(SentimentLabel sentimentLabel) { this.sentimentLabel = sentimentLabel; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public RiskSeverity getRiskSeverity() { return riskSeverity; }
    public void setRiskSeverity(RiskSeverity riskSeverity) { this.riskSeverity = riskSeverity; }

    public HumanOverride getHumanOverride() { return humanOverride; }
    public void setHumanOverride(HumanOverride humanOverride) { this.humanOverride = humanOverride; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String campaignId;
        private String responseId;
        private String questionId;
        private String sanitizedText;
        private Double sentimentScore;
        private SentimentLabel sentimentLabel;
        private Double confidence;
        private List<String> themes;
        private String riskCategory;
        private RiskSeverity riskSeverity;
        private HumanOverride humanOverride;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder responseId(String responseId) { this.responseId = responseId; return this; }
        public Builder questionId(String questionId) { this.questionId = questionId; return this; }
        public Builder sanitizedText(String sanitizedText) { this.sanitizedText = sanitizedText; return this; }
        public Builder sentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; return this; }
        public Builder sentimentLabel(SentimentLabel sentimentLabel) { this.sentimentLabel = sentimentLabel; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder themes(List<String> themes) { this.themes = themes; return this; }
        public Builder riskCategory(String riskCategory) { this.riskCategory = riskCategory; return this; }
        public Builder riskSeverity(RiskSeverity riskSeverity) { this.riskSeverity = riskSeverity; return this; }
        public Builder humanOverride(HumanOverride humanOverride) { this.humanOverride = humanOverride; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AiInsightDocument build() {
            return new AiInsightDocument(id, projectId, campaignId, responseId, questionId, sanitizedText, sentimentScore, sentimentLabel, confidence, themes, riskCategory, riskSeverity, humanOverride, createdAt);
        }
    }
}
