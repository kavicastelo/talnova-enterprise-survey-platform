package com.talnova.tesp.configservice.domain;

public class FeatureFlags {
    private boolean aiAnalyticsEnabled;
    private boolean actionPlanningEnabled;
    private boolean kioskModeEnabled;
    private boolean smsDistributionEnabled;

    public FeatureFlags() {
    }

    public FeatureFlags(boolean aiAnalyticsEnabled, boolean actionPlanningEnabled, boolean kioskModeEnabled, boolean smsDistributionEnabled) {
        this.aiAnalyticsEnabled = aiAnalyticsEnabled;
        this.actionPlanningEnabled = actionPlanningEnabled;
        this.kioskModeEnabled = kioskModeEnabled;
        this.smsDistributionEnabled = smsDistributionEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isAiAnalyticsEnabled() { return aiAnalyticsEnabled; }
    public void setAiAnalyticsEnabled(boolean aiAnalyticsEnabled) { this.aiAnalyticsEnabled = aiAnalyticsEnabled; }

    public boolean isActionPlanningEnabled() { return actionPlanningEnabled; }
    public void setActionPlanningEnabled(boolean actionPlanningEnabled) { this.actionPlanningEnabled = actionPlanningEnabled; }

    public boolean isKioskModeEnabled() { return kioskModeEnabled; }
    public void setKioskModeEnabled(boolean kioskModeEnabled) { this.kioskModeEnabled = kioskModeEnabled; }

    public boolean isSmsDistributionEnabled() { return smsDistributionEnabled; }
    public void setSmsDistributionEnabled(boolean smsDistributionEnabled) { this.smsDistributionEnabled = smsDistributionEnabled; }

    public static class Builder {
        private boolean aiAnalyticsEnabled;
        private boolean actionPlanningEnabled;
        private boolean kioskModeEnabled;
        private boolean smsDistributionEnabled;

        public Builder aiAnalyticsEnabled(boolean aiAnalyticsEnabled) { this.aiAnalyticsEnabled = aiAnalyticsEnabled; return this; }
        public Builder actionPlanningEnabled(boolean actionPlanningEnabled) { this.actionPlanningEnabled = actionPlanningEnabled; return this; }
        public Builder kioskModeEnabled(boolean kioskModeEnabled) { this.kioskModeEnabled = kioskModeEnabled; return this; }
        public Builder smsDistributionEnabled(boolean smsDistributionEnabled) { this.smsDistributionEnabled = smsDistributionEnabled; return this; }

        public FeatureFlags build() {
            return new FeatureFlags(aiAnalyticsEnabled, actionPlanningEnabled, kioskModeEnabled, smsDistributionEnabled);
        }
    }
}
