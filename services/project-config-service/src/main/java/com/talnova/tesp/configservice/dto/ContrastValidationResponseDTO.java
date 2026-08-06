package com.talnova.tesp.configservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "WCAG 2.1 AA Color Contrast Accessibility Validation Response")
public class ContrastValidationResponseDTO {

    @Schema(example = "true")
    private boolean passed;

    @Schema(example = "21.0")
    private double contrastRatio;

    @Schema(example = "AAA")
    private String wcagLevel;

    @Schema(example = "Color contrast meets WCAG 2.1 AA and AAA standards.")
    private String recommendation;

    public ContrastValidationResponseDTO() {
    }

    public ContrastValidationResponseDTO(boolean passed, double contrastRatio, String wcagLevel, String recommendation) {
        this.passed = passed;
        this.contrastRatio = contrastRatio;
        this.wcagLevel = wcagLevel;
        this.recommendation = recommendation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public double getContrastRatio() { return contrastRatio; }
    public void setContrastRatio(double contrastRatio) { this.contrastRatio = contrastRatio; }

    public String getWcagLevel() { return wcagLevel; }
    public void setWcagLevel(String wcagLevel) { this.wcagLevel = wcagLevel; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public static class Builder {
        private boolean passed;
        private double contrastRatio;
        private String wcagLevel;
        private String recommendation;

        public Builder passed(boolean passed) { this.passed = passed; return this; }
        public Builder contrastRatio(double contrastRatio) { this.contrastRatio = contrastRatio; return this; }
        public Builder wcagLevel(String wcagLevel) { this.wcagLevel = wcagLevel; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }

        public ContrastValidationResponseDTO build() {
            return new ContrastValidationResponseDTO(passed, contrastRatio, wcagLevel, recommendation);
        }
    }
}
