package com.talnova.tesp.configservice.dto;

import com.talnova.tesp.configservice.validation.ValidSupportedLocales;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@ValidSupportedLocales
@Schema(description = "Project Workspace Provisioning Request Payload")
public class ProjectCreateDTO {

    @NotBlank(message = "projectId is mandatory")
    @Pattern(regexp = "^PRJ-[A-Z0-9]{4,10}$", message = "projectId must match pattern ^PRJ-[A-Z0-9]{4,10}$")
    @Schema(example = "PRJ-99201")
    private String projectId;

    @NotBlank(message = "Project name is mandatory")
    @Schema(example = "Aitken Spence Enterprise Workspace")
    private String name;

    @Valid
    @Schema(description = "Branding configuration")
    private BrandingDTO branding;

    @NotEmpty(message = "At least one supported locale is required")
    @Schema(example = "[\"en-US\", \"si-LK\", \"ta-LK\"]")
    private List<String> supportedLocales;

    @NotBlank(message = "Default locale is required")
    @Schema(example = "en-US")
    private String defaultLocale;

    @Schema(description = "Feature flags configuration")
    private FeatureFlagsDTO features;

    @Valid
    @Schema(description = "Custom attribute definitions")
    private List<CustomAttributeDefDTO> customAttributeDefinitions;

    public ProjectCreateDTO() {
    }

    public ProjectCreateDTO(String projectId, String name, BrandingDTO branding, List<String> supportedLocales,
                            String defaultLocale, FeatureFlagsDTO features, List<CustomAttributeDefDTO> customAttributeDefinitions) {
        this.projectId = projectId;
        this.name = name;
        this.branding = branding;
        this.supportedLocales = supportedLocales;
        this.defaultLocale = defaultLocale;
        this.features = features;
        this.customAttributeDefinitions = customAttributeDefinitions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public static class Builder {
        private String projectId;
        private String name;
        private BrandingDTO branding;
        private List<String> supportedLocales;
        private String defaultLocale;
        private FeatureFlagsDTO features;
        private List<CustomAttributeDefDTO> customAttributeDefinitions;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder branding(BrandingDTO branding) { this.branding = branding; return this; }
        public Builder supportedLocales(List<String> supportedLocales) { this.supportedLocales = supportedLocales; return this; }
        public Builder defaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; return this; }
        public Builder features(FeatureFlagsDTO features) { this.features = features; return this; }
        public Builder customAttributeDefinitions(List<CustomAttributeDefDTO> customAttributeDefinitions) { this.customAttributeDefinitions = customAttributeDefinitions; return this; }

        public ProjectCreateDTO build() {
            return new ProjectCreateDTO(projectId, name, branding, supportedLocales, defaultLocale, features, customAttributeDefinitions);
        }
    }
}
