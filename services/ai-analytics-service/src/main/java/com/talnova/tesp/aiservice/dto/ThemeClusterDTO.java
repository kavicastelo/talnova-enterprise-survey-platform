package com.talnova.tesp.aiservice.dto;

import com.talnova.tesp.aiservice.domain.model.SentimentLabel;

public class ThemeClusterDTO {

    private String themeName;
    private int frequency;
    private SentimentLabel dominantSentiment;

    public ThemeClusterDTO() {}

    public ThemeClusterDTO(String themeName, int frequency, SentimentLabel dominantSentiment) {
        this.themeName = themeName;
        this.frequency = frequency;
        this.dominantSentiment = dominantSentiment;
    }

    public static Builder builder() { return new Builder(); }

    public String getThemeName() { return themeName; }
    public void setThemeName(String themeName) { this.themeName = themeName; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public SentimentLabel getDominantSentiment() { return dominantSentiment; }
    public void setDominantSentiment(SentimentLabel dominantSentiment) { this.dominantSentiment = dominantSentiment; }

    public static class Builder {
        private String themeName;
        private int frequency;
        private SentimentLabel dominantSentiment;

        public Builder themeName(String themeName) { this.themeName = themeName; return this; }
        public Builder frequency(int frequency) { this.frequency = frequency; return this; }
        public Builder dominantSentiment(SentimentLabel dominantSentiment) { this.dominantSentiment = dominantSentiment; return this; }

        public ThemeClusterDTO build() {
            return new ThemeClusterDTO(themeName, frequency, dominantSentiment);
        }
    }
}
