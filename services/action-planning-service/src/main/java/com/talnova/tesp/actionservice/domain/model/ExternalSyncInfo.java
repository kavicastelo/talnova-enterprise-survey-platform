package com.talnova.tesp.actionservice.domain.model;

import java.time.Instant;

public class ExternalSyncInfo {

    private String system; // JIRA, MS_PLANNER
    private String externalKey;
    private Instant lastSyncedAt;

    public ExternalSyncInfo() {}

    public ExternalSyncInfo(String system, String externalKey, Instant lastSyncedAt) {
        this.system = system;
        this.externalKey = externalKey;
        this.lastSyncedAt = lastSyncedAt;
    }

    public static Builder builder() { return new Builder(); }

    public String getSystem() { return system; }
    public void setSystem(String system) { this.system = system; }

    public String getExternalKey() { return externalKey; }
    public void setExternalKey(String externalKey) { this.externalKey = externalKey; }

    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Instant lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }

    public static class Builder {
        private String system;
        private String externalKey;
        private Instant lastSyncedAt;

        public Builder system(String system) { this.system = system; return this; }
        public Builder externalKey(String externalKey) { this.externalKey = externalKey; return this; }
        public Builder lastSyncedAt(Instant lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; return this; }

        public ExternalSyncInfo build() {
            return new ExternalSyncInfo(system, externalKey, lastSyncedAt);
        }
    }
}
