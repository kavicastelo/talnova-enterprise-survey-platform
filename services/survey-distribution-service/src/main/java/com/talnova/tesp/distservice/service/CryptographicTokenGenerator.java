package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.dto.TokenBatchGenerationRequestDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationResponseDTO;

public interface CryptographicTokenGenerator {

    TokenBatchGenerationResponseDTO generateTokensForCampaign(TokenBatchGenerationRequestDTO request);

    String generateHmacSha256Token(String employeeId, String campaignSalt);

    String generateKioskPin(java.util.Set<String> existingPins);
}
