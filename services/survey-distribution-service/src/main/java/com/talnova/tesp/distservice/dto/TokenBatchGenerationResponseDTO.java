package com.talnova.tesp.distservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Response payload containing generated cryptographic tokens")
public class TokenBatchGenerationResponseDTO {

    private String campaignId;
    private int totalGenerated;
    private List<GeneratedTokenDTO> tokens = new ArrayList<>();
    private long generationDurationMs;

    public TokenBatchGenerationResponseDTO() {
    }

    public TokenBatchGenerationResponseDTO(String campaignId, int totalGenerated, List<GeneratedTokenDTO> tokens, long generationDurationMs) {
        this.campaignId = campaignId;
        this.totalGenerated = totalGenerated;
        this.tokens = tokens != null ? tokens : new ArrayList<>();
        this.generationDurationMs = generationDurationMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public int getTotalGenerated() { return totalGenerated; }
    public void setTotalGenerated(int totalGenerated) { this.totalGenerated = totalGenerated; }

    public List<GeneratedTokenDTO> getTokens() { return tokens; }
    public void setTokens(List<GeneratedTokenDTO> tokens) { this.tokens = tokens; }

    public long getGenerationDurationMs() { return generationDurationMs; }
    public void setGenerationDurationMs(long generationDurationMs) { this.generationDurationMs = generationDurationMs; }

    public static class Builder {
        private String campaignId;
        private int totalGenerated;
        private List<GeneratedTokenDTO> tokens = new ArrayList<>();
        private long generationDurationMs;

        public Builder campaignId(String campaignId) { this.campaignId = campaignId; return this; }
        public Builder totalGenerated(int totalGenerated) { this.totalGenerated = totalGenerated; return this; }
        public Builder tokens(List<GeneratedTokenDTO> tokens) { this.tokens = tokens; return this; }
        public Builder generationDurationMs(long generationDurationMs) { this.generationDurationMs = generationDurationMs; return this; }

        public TokenBatchGenerationResponseDTO build() {
            return new TokenBatchGenerationResponseDTO(campaignId, totalGenerated, tokens, generationDurationMs);
        }
    }
}
