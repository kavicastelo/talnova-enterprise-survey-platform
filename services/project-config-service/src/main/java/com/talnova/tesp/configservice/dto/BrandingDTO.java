package com.talnova.tesp.configservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Branding Configuration Payload")
public class BrandingDTO {

    @NotBlank(message = "Company name is required")
    @Schema(example = "Aitken Spence PLC")
    private String companyName;

    @Schema(example = "https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png")
    private String logoUrl;

    @NotBlank(message = "Primary color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid HEX color format")
    @Schema(example = "#1E3A8A")
    private String primaryColor;

    @NotBlank(message = "Secondary color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid HEX color format")
    @Schema(example = "#3B82F6")
    private String secondaryColor;

    @Schema(example = "https://s3.amazonaws.com/tesp-assets/prj-99201/custom.css")
    private String customCssUrl;

    public BrandingDTO() {
    }

    public BrandingDTO(String companyName, String logoUrl, String primaryColor, String secondaryColor, String customCssUrl) {
        this.companyName = companyName;
        this.logoUrl = logoUrl;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.customCssUrl = customCssUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

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

    public static class Builder {
        private String companyName;
        private String logoUrl;
        private String primaryColor;
        private String secondaryColor;
        private String customCssUrl;

        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder secondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; return this; }
        public Builder customCssUrl(String customCssUrl) { this.customCssUrl = customCssUrl; return this; }

        public BrandingDTO build() {
            return new BrandingDTO(companyName, logoUrl, primaryColor, secondaryColor, customCssUrl);
        }
    }
}
