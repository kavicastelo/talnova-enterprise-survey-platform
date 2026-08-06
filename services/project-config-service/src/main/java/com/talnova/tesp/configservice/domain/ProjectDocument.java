package com.talnova.tesp.configservice.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "projects")
@CompoundIndex(name = "idx_project_lookup", def = "{'projectId': 1, 'isDeleted': 1, 'status': 1}")
public class ProjectDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private String name;

    private ProjectStatus status;

    private BrandingConfig branding;

    private List<String> supportedLocales;

    private String defaultLocale;

    private FeatureFlags features;

    private List<CustomAttributeDefinition> customAttributeDefinitions;

    @Version
    private Integer version;

    private boolean isDeleted;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public ProjectDocument() {
    }

    public ProjectDocument(String id, String projectId, String name, ProjectStatus status, BrandingConfig branding,
                           List<String> supportedLocales, String defaultLocale, FeatureFlags features,
                           List<CustomAttributeDefinition> customAttributeDefinitions, Integer version,
                           boolean isDeleted, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.status = status;
        this.branding = branding;
        this.supportedLocales = supportedLocales;
        this.defaultLocale = defaultLocale;
        this.features = features;
        this.customAttributeDefinitions = customAttributeDefinitions;
        this.version = version;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public BrandingConfig getBranding() {
        return branding;
    }

    public void setBranding(BrandingConfig branding) {
        this.branding = branding;
    }

    public List<String> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(List<String> supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public FeatureFlags getFeatures() {
        return features;
    }

    public void setFeatures(FeatureFlags features) {
        this.features = features;
    }

    public List<CustomAttributeDefinition> getCustomAttributeDefinitions() {
        return customAttributeDefinitions;
    }

    public void setCustomAttributeDefinitions(List<CustomAttributeDefinition> customAttributeDefinitions) {
        this.customAttributeDefinitions = customAttributeDefinitions;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private String id;
        private String projectId;
        private String name;
        private ProjectStatus status;
        private BrandingConfig branding;
        private List<String> supportedLocales;
        private String defaultLocale;
        private FeatureFlags features;
        private List<CustomAttributeDefinition> customAttributeDefinitions;
        private Integer version;
        private boolean isDeleted;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder status(ProjectStatus status) { this.status = status; return this; }
        public Builder branding(BrandingConfig branding) { this.branding = branding; return this; }
        public Builder supportedLocales(List<String> supportedLocales) { this.supportedLocales = supportedLocales; return this; }
        public Builder defaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; return this; }
        public Builder features(FeatureFlags features) { this.features = features; return this; }
        public Builder customAttributeDefinitions(List<CustomAttributeDefinition> customAttributeDefinitions) { this.customAttributeDefinitions = customAttributeDefinitions; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder isDeleted(boolean isDeleted) { this.isDeleted = isDeleted; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public ProjectDocument build() {
            return new ProjectDocument(id, projectId, name, status, branding, supportedLocales, defaultLocale, features, customAttributeDefinitions, version, isDeleted, createdAt, updatedAt);
        }
    }
}
