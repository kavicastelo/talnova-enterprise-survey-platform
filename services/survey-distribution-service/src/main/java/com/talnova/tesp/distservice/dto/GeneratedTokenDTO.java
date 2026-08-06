package com.talnova.tesp.distservice.dto;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a generated single-use survey token")
public class GeneratedTokenDTO {

    @Schema(description = "Cryptographic survey access token", example = "a7b8c9d0e1f2...")
    private String token;

    @Schema(description = "Employee ID (null for fully anonymous campaigns)", example = "EMP-10020")
    private String employeeId;

    @Schema(description = "Target campaign ID", example = "CMP-1001")
    private String campaignId;

    @Schema(description = "Campaign anonymity protection level", example = "SEMI_ANONYMOUS")
    private AnonymityLevel anonymityLevel;

    @Schema(description = "6-digit Kiosk PIN number if channel includes KIOSK_PIN", example = "849201")
    private String kioskPin;

    public GeneratedTokenDTO() {
    }

    public GeneratedTokenDTO(String token, String employeeId, String campaignId, AnonymityLevel anonymityLevel, String kioskPin) {
        this.token = token;
        this.employeeId = employeeId;
        this.campaignId = campaignId;
        this.anonymityLevel = anonymityLevel;
        this.kioskPin = kioskPin;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public AnonymityLevel getAnonymityLevel() { return anonymityLevel; }
    public void setAnonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; }

    public String getKioskPin() { return kioskPin; }
    public void setKioskPin(String kioskPin) { this.kioskPin = kioskPin; }

    public static class Builder {
        private String token;
        private String employeeId;
        private String campaignId;
        private AnonymityLevel anonymityLevel;
        private String kioskPin;

        public Builder token(String token) { this.token = token; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder anonymityLevel(AnonymityLevel anonymityLevel) { this.anonymityLevel = anonymityLevel; return this; }
        public Builder kioskPin(String kioskPin) { this.kioskPin = kioskPin; return this; }

        public GeneratedTokenDTO build() {
            return new GeneratedTokenDTO(token, employeeId, campaignId, anonymityLevel, kioskPin);
        }
    }
}
