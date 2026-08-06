package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationRequestDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationResponseDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class CryptographicTokenGeneratorImpl implements CryptographicTokenGenerator {

    private static final Logger log = LoggerFactory.getLogger(CryptographicTokenGeneratorImpl.class);
    private static final String DEFAULT_HMAC_ALGORITHM = "HmacSHA256";
    private static final String DEFAULT_SALT = "TESP-SECRET-CAMPAIGN-SALT-2026";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public TokenBatchGenerationResponseDTO generateTokensForCampaign(TokenBatchGenerationRequestDTO request) {
        long startTime = System.currentTimeMillis();
        log.info("Generating token batch for campaignId: {}, employeeCount: {}", request.getCampaignId(), request.getEmployeeIds().size());

        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new CampaignValidationException("Employee IDs list must not be empty for token generation");
        }

        String salt = (request.getCampaignSalt() != null && !request.getCampaignSalt().isBlank())
                ? request.getCampaignSalt()
                : DEFAULT_SALT;

        Set<String> generatedPins = ConcurrentHashMap.newKeySet();
        List<GeneratedTokenDTO> tokenList = Collections.synchronizedList(new ArrayList<>());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (String employeeId : request.getEmployeeIds()) {
                futures.add(executor.submit(() -> {
                    String token = generateHmacSha256Token(employeeId, salt + "-" + request.getCampaignId());
                    String pin = null;

                    if (request.getAnonymityLevel() == AnonymityLevel.KIOSK) {
                        pin = generateKioskPin(generatedPins);
                    }

                    GeneratedTokenDTO tokenDTO = GeneratedTokenDTO.builder()
                            .token(token)
                            .employeeId(request.getAnonymityLevel() == AnonymityLevel.FULLY_ANONYMOUS ? null : employeeId)
                            .campaignId(request.getCampaignId())
                            .anonymityLevel(request.getAnonymityLevel())
                            .kioskPin(pin)
                            .build();

                    tokenList.add(tokenDTO);
                }));
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            log.error("Error generating token batch concurrently: {}", e.getMessage(), e);
            throw new CampaignValidationException("Token batch generation failed: " + e.getMessage());
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Successfully generated {} tokens for campaignId: {} in {} ms", tokenList.size(), request.getCampaignId(), duration);

        return TokenBatchGenerationResponseDTO.builder()
                .campaignId(request.getCampaignId())
                .totalGenerated(tokenList.size())
                .tokens(tokenList)
                .generationDurationMs(duration)
                .build();
    }

    @Override
    public String generateHmacSha256Token(String employeeId, String campaignSalt) {
        try {
            Mac mac = Mac.getInstance(DEFAULT_HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(campaignSalt.getBytes(StandardCharsets.UTF_8), DEFAULT_HMAC_ALGORITHM);
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(employeeId.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (Exception e) {
            log.error("Failed to compute HMAC-SHA256 token for employeeId {}: {}", employeeId, e.getMessage());
            throw new RuntimeException("Cryptographic hashing error", e);
        }
    }

    @Override
    public String generateKioskPin(Set<String> existingPins) {
        String pin;
        int attempts = 0;
        do {
            int num = 100000 + secureRandom.nextInt(900000);
            pin = String.valueOf(num);
            attempts++;
            if (attempts > 10000) {
                throw new CampaignValidationException("PIN space exhausted for campaign");
            }
        } while (existingPins.contains(pin));

        existingPins.add(pin);
        return pin;
    }
}
