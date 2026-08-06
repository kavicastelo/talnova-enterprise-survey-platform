package com.talnova.tesp.employeeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI Fuzzy CSV Header Mapping Result")
public class HeaderMapping {

    private String sourceHeader;
    private String targetAttributeKey;
    private double confidence;
    private boolean isCoreField;

    public HeaderMapping() {
    }

    public HeaderMapping(String sourceHeader, String targetAttributeKey, double confidence, boolean isCoreField) {
        this.sourceHeader = sourceHeader;
        this.targetAttributeKey = targetAttributeKey;
        this.confidence = confidence;
        this.isCoreField = isCoreField;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSourceHeader() { return sourceHeader; }
    public void setSourceHeader(String sourceHeader) { this.sourceHeader = sourceHeader; }

    public String getTargetAttributeKey() { return targetAttributeKey; }
    public void setTargetAttributeKey(String targetAttributeKey) { this.targetAttributeKey = targetAttributeKey; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public boolean isCoreField() { return isCoreField; }
    public void setCoreField(boolean coreField) { isCoreField = coreField; }

    public static class Builder {
        private String sourceHeader;
        private String targetAttributeKey;
        private double confidence;
        private boolean isCoreField;

        public Builder sourceHeader(String sourceHeader) { this.sourceHeader = sourceHeader; return this; }
        public Builder targetAttributeKey(String targetAttributeKey) { this.targetAttributeKey = targetAttributeKey; return this; }
        public Builder confidence(double confidence) { this.confidence = confidence; return this; }
        public Builder isCoreField(boolean isCoreField) { this.isCoreField = isCoreField; return this; }

        public HeaderMapping build() {
            return new HeaderMapping(sourceHeader, targetAttributeKey, confidence, isCoreField);
        }
    }
}
