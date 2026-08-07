package com.talnova.tesp.aiservice.domain.model;

import java.time.Instant;

public class HumanOverride {

    private String overriddenBy;
    private String originalLabel;
    private String newLabel;
    private String reason;
    private Instant overriddenAt;

    public HumanOverride() {}

    public HumanOverride(String overriddenBy, String originalLabel, String newLabel, String reason, Instant overriddenAt) {
        this.overriddenBy = overriddenBy;
        this.originalLabel = originalLabel;
        this.newLabel = newLabel;
        this.reason = reason;
        this.overriddenAt = overriddenAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getOverriddenBy() { return overriddenBy; }
    public void setOverriddenBy(String overriddenBy) { this.overriddenBy = overriddenBy; }

    public String getOriginalLabel() { return originalLabel; }
    public void setOriginalLabel(String originalLabel) { this.originalLabel = originalLabel; }

    public String getNewLabel() { return newLabel; }
    public void setNewLabel(String newLabel) { this.newLabel = newLabel; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Instant getOverriddenAt() { return overriddenAt; }
    public void setOverriddenAt(Instant overriddenAt) { this.overriddenAt = overriddenAt; }

    public static class Builder {
        private String overriddenBy;
        private String originalLabel;
        private String newLabel;
        private String reason;
        private Instant overriddenAt;

        public Builder overriddenBy(String overriddenBy) { this.overriddenBy = overriddenBy; return this; }
        public Builder originalLabel(String originalLabel) { this.originalLabel = originalLabel; return this; }
        public Builder newLabel(String newLabel) { this.newLabel = newLabel; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder overriddenAt(Instant overriddenAt) { this.overriddenAt = overriddenAt; return this; }

        public HumanOverride build() {
            return new HumanOverride(overriddenBy, originalLabel, newLabel, reason, overriddenAt);
        }
    }
}
