package com.talnova.tesp.configservice.domain;

public class BrandingConfig {
    private String companyName;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String customCssUrl;

    public BrandingConfig() {
    }

    public BrandingConfig(String companyName, String logoUrl, String primaryColor, String secondaryColor, String customCssUrl) {
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

        public BrandingConfig build() {
            return new BrandingConfig(companyName, logoUrl, primaryColor, secondaryColor, customCssUrl);
        }
    }
}
