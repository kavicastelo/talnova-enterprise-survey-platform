package com.talnova.tesp.distservice.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "identity_token_vault")
@CompoundIndexes({
        @CompoundIndex(name = "idx_project_campaign_token_unique", def = "{'projectId': 1, 'campaignId': 1, 'token': 1}", unique = true),
        @CompoundIndex(name = "idx_campaign_employee", def = "{'campaignId': 1, 'employeeId': 1}"),
        @CompoundIndex(name = "idx_campaign_burned", def = "{'campaignId': 1, 'isBurned': 1}")
})
public class IdentityTokenVaultDocument {

    @Id
    private String id;

    private String projectId;
    private String campaignId;
    private String employeeId;
    private String token;
    private String kioskPin;
    private AnonymityLevel anonymityLevel;

    private boolean isBurned = false;
    private Instant burnedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public IdentityTokenVaultDocument() {
    }

    public IdentityTokenVaultDocument(String id, String projectId, String campaignId, String employeeId, String token, String kioskPin, AnonymityLevel anonymityLevel, boolean isBurned, Instant burnedAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.campaignId = campaignId;
        this.employeeId = employeeId;
        this.token = token;
        this.kioskPin = kioskPin;
        this.anonymityLevel = anonymityLevel;
        this.isBurned = isBurned;
        this.burnedAt = burnedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public boolean isBurned() { return isBurned; }
    public void setBurned(boolean burned) { isBurned = burned; }

    public Instant getBurnedAt() { return burnedAt; }
    public void setBurnedAt(Instant burnedAt) { this.burnedAt = burnedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String campaignId;
        private String employeeId;
        private String token;
        private String kioskPin;
        private AnonymityLevel anonymityLevel;
        private boolean isBurned = false;
        private Instant burnedAt;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder isBurned(boolean isBurned) { this.isBurned = isBurned; return this; }
        public Builder burnedAt(Instant burnedAt) { this.burnedAt = burnedAt; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public IdentityTokenVaultDocument build() {
            return new IdentityTokenVaultDocument(id, projectId, campaignId, employeeId, token, kioskPin, anonymityLevel, isBurned, burnedAt, createdAt, updatedAt);
        }
    }
}
