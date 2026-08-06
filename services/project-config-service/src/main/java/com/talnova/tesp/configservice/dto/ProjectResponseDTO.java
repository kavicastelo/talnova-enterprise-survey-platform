package com.talnova.tesp.configservice.dto;

import com.talnova.tesp.configservice.domain.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Project Workspace Configuration Details Response")
public class ProjectResponseDTO {

    @Schema(example = "66b26d8f8a84a51e3c8b4567")
    private String id;

    @Schema(example = "PRJ-99201")
    private String projectId;

    @Schema(example = "Aitken Spence Enterprise Workspace")
    private String name;

    @Schema(example = "ACTIVE")
    private ProjectStatus status;

    private BrandingDTO branding;

    private List<String> supportedLocales;

    @Schema(example = "en-US")
    private String defaultLocale;

    private FeatureFlagsDTO features;

    private List<CustomAttributeDefDTO> customAttributeDefinitions;

    @Schema(example = "1")
    private Integer version;

    @Schema(example = "2026-08-06T10:00:00Z")
    private Instant createdAt;

    @Schema(example = "2026-08-06T10:00:00Z")
    private Instant updatedAt;

    public ProjectResponseDTO() {
    }

    public ProjectResponseDTO(String id, String projectId, String name, ProjectStatus status, BrandingDTO branding,
                              List<String> supportedLocales, String defaultLocale, FeatureFlagsDTO features,
                              List<CustomAttributeDefDTO> customAttributeDefinitions, Integer version,
                              Instant createdAt, Instant updatedAt) {
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public BrandingDTO getBranding() { return branding; }
    public void setBranding(BrandingDTO branding) { this.branding = branding; }

    public List<String> getSupportedLocales() { return supportedLocales; }
    public void setSupportedLocales(List<String> supportedLocales) { this.supportedLocales = supportedLocales; }

    public String getDefaultLocale() { return defaultLocale; }
    public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }

    public FeatureFlagsDTO getFeatures() { return features; }
    public void setFeatures(FeatureFlagsDTO features) { this.features = features; }

    public List<CustomAttributeDefDTO> getCustomAttributeDefinitions() { return customAttributeDefinitions; }
    public void setCustomAttributeDefinitions(List<CustomAttributeDefDTO> customAttributeDefinitions) { this.customAttributeDefinitions = customAttributeDefinitions; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String id;
        private String projectId;
        private String name;
        private ProjectStatus status;
        private BrandingDTO branding;
        private List<String> supportedLocales;
        private String defaultLocale;
        private FeatureFlagsDTO features;
        private List<CustomAttributeDefDTO> customAttributeDefinitions;
        private Integer version;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder status(ProjectStatus status) { this.status = status; return this; }
        public Builder branding(BrandingDTO branding) { this.branding = branding; return this; }
        public Builder supportedLocales(List<String> supportedLocales) { this.supportedLocales = supportedLocales; return this; }
        public Builder defaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; return this; }
        public Builder features(FeatureFlagsDTO features) { this.features = features; return this; }
        public Builder customAttributeDefinitions(List<CustomAttributeDefDTO> customAttributeDefinitions) { this.customAttributeDefinitions = customAttributeDefinitions; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public ProjectResponseDTO build() {
            return new ProjectResponseDTO(id, projectId, name, status, branding, supportedLocales, defaultLocale, features, customAttributeDefinitions, version, createdAt, updatedAt);
        }
    }
}
