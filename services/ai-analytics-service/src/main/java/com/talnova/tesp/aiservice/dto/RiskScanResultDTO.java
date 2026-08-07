package com.talnova.tesp.aiservice.dto;

import com.talnova.tesp.aiservice.domain.model.RiskSeverity;

public class RiskScanResultDTO {

    private boolean riskDetected;
    private String category;
    private RiskSeverity severity;
    private String detectedKeyword;

    public RiskScanResultDTO() {}

    public RiskScanResultDTO(boolean riskDetected, String category, RiskSeverity severity, String detectedKeyword) {
        this.riskDetected = riskDetected;
        this.category = category;
        this.severity = severity;
        this.detectedKeyword = detectedKeyword;
    }

    public static Builder builder() { return new Builder(); }

    public boolean isRiskDetected() { return riskDetected; }
    public void setRiskDetected(boolean riskDetected) { this.riskDetected = riskDetected; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public RiskSeverity getSeverity() { return severity; }
    public void setSeverity(RiskSeverity severity) { this.severity = severity; }

    public String getDetectedKeyword() { return detectedKeyword; }
    public void setDetectedKeyword(String detectedKeyword) { this.detectedKeyword = detectedKeyword; }

    public static class Builder {
        private boolean riskDetected;
        private String category;
        private RiskSeverity severity;
        private String detectedKeyword;

        public Builder riskDetected(boolean riskDetected) { this.riskDetected = riskDetected; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder severity(RiskSeverity severity) { this.severity = severity; return this; }
        public Builder detectedKeyword(String detectedKeyword) { this.detectedKeyword = detectedKeyword; return this; }

        public RiskScanResultDTO build() {
            return new RiskScanResultDTO(riskDetected, category, severity, detectedKeyword);
        }
    }
}
