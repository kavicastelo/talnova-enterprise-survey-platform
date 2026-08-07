package com.talnova.tesp.reportingservice.dto;

import java.util.List;

public class ExecutiveReportDataDTO {

    private ProjectBrandingDTO branding;
    private String campaignTitle;
    private String reportDate;
    private double enpsScore;
    private double responseRate;
    private List<String> strengths;
    private List<String> concerns;
    private List<String> recommendations;

    public ExecutiveReportDataDTO() {}

    public ExecutiveReportDataDTO(ProjectBrandingDTO branding, String campaignTitle, String reportDate, double enpsScore, double responseRate, List<String> strengths, List<String> concerns, List<String> recommendations) {
        this.branding = branding;
        this.campaignTitle = campaignTitle;
        this.reportDate = reportDate;
        this.enpsScore = enpsScore;
        this.responseRate = responseRate;
        this.strengths = strengths;
        this.concerns = concerns;
        this.recommendations = recommendations;
    }

    public static Builder builder() { return new Builder(); }

    public ProjectBrandingDTO getBranding() { return branding; }
    public void setBranding(ProjectBrandingDTO branding) { this.branding = branding; }

    public String getCampaignTitle() { return campaignTitle; }
    public void setCampaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; }

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }

    public double getEnpsScore() { return enpsScore; }
    public void setEnpsScore(double enpsScore) { this.enpsScore = enpsScore; }

    public double getResponseRate() { return responseRate; }
    public void setResponseRate(double responseRate) { this.responseRate = responseRate; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getConcerns() { return concerns; }
    public void setConcerns(List<String> concerns) { this.concerns = concerns; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public static class Builder {
        private ProjectBrandingDTO branding;
        private String campaignTitle;
        private String reportDate;
        private double enpsScore;
        private double responseRate;
        private List<String> strengths;
        private List<String> concerns;
        private List<String> recommendations;

        public Builder branding(ProjectBrandingDTO branding) { this.branding = branding; return this; }
        public Builder campaignTitle(String campaignTitle) { this.campaignTitle = campaignTitle; return this; }
        public Builder reportDate(String reportDate) { this.reportDate = reportDate; return this; }
        public Builder enpsScore(double enpsScore) { this.enpsScore = enpsScore; return this; }
        public Builder responseRate(double responseRate) { this.responseRate = responseRate; return this; }
        public Builder strengths(List<String> strengths) { this.strengths = strengths; return this; }
        public Builder concerns(List<String> concerns) { this.concerns = concerns; return this; }
        public Builder recommendations(List<String> recommendations) { this.recommendations = recommendations; return this; }

        public ExecutiveReportDataDTO build() {
            return new ExecutiveReportDataDTO(branding, campaignTitle, reportDate, enpsScore, responseRate, strengths, concerns, recommendations);
        }
    }
}
