package com.talnova.tesp.configservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Unauthenticated Public Theme Metadata Response")
public class PublicThemeDTO {

    @Schema(example = "PRJ-99201")
    private String projectId;

    @Schema(example = "Aitken Spence PLC")
    private String companyName;

    @Schema(example = "https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png")
    private String logoUrl;

    @Schema(example = "#1E3A8A")
    private String primaryColor;

    @Schema(example = "#3B82F6")
    private String secondaryColor;

    @Schema(example = "https://s3.amazonaws.com/tesp-assets/prj-99201/custom.css")
    private String customCssUrl;

    @Schema(example = "en-US")
    private String defaultLocale;

    public PublicThemeDTO() {
    }

    public PublicThemeDTO(String projectId, String companyName, String logoUrl, String primaryColor,
                          String secondaryColor, String customCssUrl, String defaultLocale) {
        this.projectId = projectId;
        this.companyName = companyName;
        this.logoUrl = logoUrl;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.customCssUrl = customCssUrl;
        this.defaultLocale = defaultLocale;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }

    public String getCustomCssUrl() { return customCssUrl; }
    public void setCustomCssUrl(String customCssUrl) { this.customCssUrl = customCssUrl; }

    public String getDefaultLocale() { return defaultLocale; }
    public void setDefaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; }

    public static class Builder {
        private String projectId;
        private String companyName;
        private String logoUrl;
        private String primaryColor;
        private String secondaryColor;
        private String customCssUrl;
        private String defaultLocale;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder secondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; return this; }
        public Builder customCssUrl(String customCssUrl) { this.customCssUrl = customCssUrl; return this; }
        public Builder defaultLocale(String defaultLocale) { this.defaultLocale = defaultLocale; return this; }

        public PublicThemeDTO build() {
            return new PublicThemeDTO(projectId, companyName, logoUrl, primaryColor, secondaryColor, customCssUrl, defaultLocale);
        }
    }
}
