package com.talnova.tesp.aiservice.dto;

import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SentimentOverrideDTO {

    @NotBlank(message = "overriddenBy is required")
    private String overriddenBy;

    @NotNull(message = "newLabel is required")
    private SentimentLabel newLabel;

    private String reason;

    public SentimentOverrideDTO() {}

    public SentimentOverrideDTO(String overriddenBy, SentimentLabel newLabel, String reason) {
        this.overriddenBy = overriddenBy;
        this.newLabel = newLabel;
        this.reason = reason;
    }

    public static Builder builder() { return new Builder(); }

    public String getOverriddenBy() { return overriddenBy; }
    public void setOverriddenBy(String overriddenBy) { this.overriddenBy = overriddenBy; }

    public SentimentLabel getNewLabel() { return newLabel; }
    public void setNewLabel(SentimentLabel newLabel) { this.newLabel = newLabel; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public static class Builder {
        private String overriddenBy;
        private SentimentLabel newLabel;
        private String reason;

        public Builder overriddenBy(String overriddenBy) { this.overriddenBy = overriddenBy; return this; }
        public Builder newLabel(SentimentLabel newLabel) { this.newLabel = newLabel; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public SentimentOverrideDTO build() {
            return new SentimentOverrideDTO(overriddenBy, newLabel, reason);
        }
    }
}
