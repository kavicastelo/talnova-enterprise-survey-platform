package com.talnova.tesp.analyticsservice.dto;

public class KeyDriverDTO {

    private String themeGroupId;
    private String themeName;
    private Double importanceWeight;
    private Double correlationScore;
    private String impactCategory; // HIGH_IMPACT, MEDIUM_IMPACT, LOW_IMPACT

    public KeyDriverDTO() {}

    public KeyDriverDTO(String themeGroupId, String themeName, Double importanceWeight, Double correlationScore, String impactCategory) {
        this.themeGroupId = themeGroupId;
        this.themeName = themeName;
        this.importanceWeight = importanceWeight;
        this.correlationScore = correlationScore;
        this.impactCategory = impactCategory;
    }

    public static Builder builder() { return new Builder(); }

    public String getThemeGroupId() { return themeGroupId; }
    public void setThemeGroupId(String themeGroupId) { this.themeGroupId = themeGroupId; }

    public String getThemeName() { return themeName; }
    public void setThemeName(String themeName) { this.themeName = themeName; }

    public Double getImportanceWeight() { return importanceWeight; }
    public void setImportanceWeight(Double importanceWeight) { this.importanceWeight = importanceWeight; }

    public Double getCorrelationScore() { return correlationScore; }
    public void setCorrelationScore(Double correlationScore) { this.correlationScore = correlationScore; }

    public String getImpactCategory() { return impactCategory; }
    public void setImpactCategory(String impactCategory) { this.impactCategory = impactCategory; }

    public static class Builder {
        private String themeGroupId;
        private String themeName;
        private Double importanceWeight;
        private Double correlationScore;
        private String impactCategory;

        public Builder themeGroupId(String themeGroupId) { this.themeGroupId = themeGroupId; return this; }
        public Builder themeName(String themeName) { this.themeName = themeName; return this; }
        public Builder importanceWeight(Double importanceWeight) { this.importanceWeight = importanceWeight; return this; }
        public Builder correlationScore(Double correlationScore) { this.correlationScore = correlationScore; return this; }
        public Builder impactCategory(String impactCategory) { this.impactCategory = impactCategory; return this; }

        public KeyDriverDTO build() {
            return new KeyDriverDTO(themeGroupId, themeName, importanceWeight, correlationScore, impactCategory);
        }
    }
}
