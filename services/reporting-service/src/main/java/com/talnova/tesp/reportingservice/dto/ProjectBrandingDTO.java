package com.talnova.tesp.reportingservice.dto;

public class ProjectBrandingDTO {

    private String projectId;
    private String companyName;
    private String logoUrl;
    private String primaryColorHex;
    private String secondaryColorHex;
    private String fontFamily;
    private String footerText;

    public ProjectBrandingDTO() {}

    public ProjectBrandingDTO(String projectId, String companyName, String logoUrl, String primaryColorHex, String secondaryColorHex, String fontFamily, String footerText) {
        this.projectId = projectId;
        this.companyName = companyName;
        this.logoUrl = logoUrl;
        this.primaryColorHex = primaryColorHex;
        this.secondaryColorHex = secondaryColorHex;
        this.fontFamily = fontFamily;
        this.footerText = footerText;
    }

    public static Builder builder() { return new Builder(); }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getPrimaryColorHex() { return primaryColorHex; }
    public void setPrimaryColorHex(String primaryColorHex) { this.primaryColorHex = primaryColorHex; }

    public String getSecondaryColorHex() { return secondaryColorHex; }
    public void setSecondaryColorHex(String secondaryColorHex) { this.secondaryColorHex = secondaryColorHex; }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }

    public static class Builder {
        private String projectId;
        private String companyName;
        private String logoUrl;
        private String primaryColorHex;
        private String secondaryColorHex;
        private String fontFamily;
        private String footerText;

        public Builder projectId(String projectId) { this.projectId = projectId; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder primaryColorHex(String primaryColorHex) { this.primaryColorHex = primaryColorHex; return this; }
        public Builder secondaryColorHex(String secondaryColorHex) { this.secondaryColorHex = secondaryColorHex; return this; }
        public Builder fontFamily(String fontFamily) { this.fontFamily = fontFamily; return this; }
        public Builder footerText(String footerText) { this.footerText = footerText; return this; }

        public ProjectBrandingDTO build() {
            return new ProjectBrandingDTO(projectId, companyName, logoUrl, primaryColorHex, secondaryColorHex, fontFamily, footerText);
        }
    }
}
