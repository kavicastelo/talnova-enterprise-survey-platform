package com.talnova.tesp.aiservice.dto;

import com.talnova.tesp.aiservice.domain.model.SentimentLabel;

public class SentimentResultDTO {

    private Double sentimentScore;
    private SentimentLabel sentimentLabel;
    private Double confidence;
    private String detectedLanguage;
    private String sanitizedText;

    public SentimentResultDTO() {}

    public SentimentResultDTO(Double sentimentScore, SentimentLabel sentimentLabel, Double confidence, String detectedLanguage, String sanitizedText) {
        this.sentimentScore = sentimentScore;
        this.sentimentLabel = sentimentLabel;
        this.confidence = confidence;
        this.detectedLanguage = detectedLanguage;
        this.sanitizedText = sanitizedText;
    }

    public static Builder builder() { return new Builder(); }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }

    public SentimentLabel getSentimentLabel() { return sentimentLabel; }
    public void setSentimentLabel(SentimentLabel sentimentLabel) { this.sentimentLabel = sentimentLabel; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getDetectedLanguage() { return detectedLanguage; }
    public void setDetectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; }

    public String getSanitizedText() { return sanitizedText; }
    public void setSanitizedText(String sanitizedText) { this.sanitizedText = sanitizedText; }

    public static class Builder {
        private Double sentimentScore;
        private SentimentLabel sentimentLabel;
        private Double confidence;
        private String detectedLanguage;
        private String sanitizedText;

        public Builder sentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; return this; }
        public Builder sentimentLabel(SentimentLabel sentimentLabel) { this.sentimentLabel = sentimentLabel; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder detectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; return this; }
        public Builder sanitizedText(String sanitizedText) { this.sanitizedText = sanitizedText; return this; }

        public SentimentResultDTO build() {
            return new SentimentResultDTO(sentimentScore, sentimentLabel, confidence, detectedLanguage, sanitizedText);
        }
    }
}
