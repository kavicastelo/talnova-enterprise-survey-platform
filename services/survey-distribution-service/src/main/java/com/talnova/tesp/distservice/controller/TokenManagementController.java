package com.talnova.tesp.distservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationRequestDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationResponseDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.service.CryptographicTokenGenerator;
import com.talnova.tesp.distservice.service.RedisTokenCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tokens")
@Tag(name = "Survey Token Security API", description = "Endpoints for generating HMAC-SHA256 tokens, Kiosk PINs, and single-use token burn operations")
public class TokenManagementController {

    private final CryptographicTokenGenerator tokenGenerator;
    private final RedisTokenCacheService tokenCacheService;

    public TokenManagementController(CryptographicTokenGenerator tokenGenerator, RedisTokenCacheService tokenCacheService) {
        this.tokenGenerator = tokenGenerator;
        this.tokenCacheService = tokenCacheService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate Cryptographic Survey Tokens", description = "Generates HMAC-SHA256 tokens and optional 6-digit Kiosk PINs using Java 21 Virtual Threads")
    public ResponseEntity<ApiResponse<TokenBatchGenerationResponseDTO>> generateTokens(
            @Valid @RequestBody TokenBatchGenerationRequestDTO request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        TokenBatchGenerationResponseDTO response = tokenGenerator.generateTokensForCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Token batch generated successfully", correlationId));
    }

    @PostMapping("/burn")
    @Operation(summary = "Atomically Burn Single-Use Token", description = "Atomically validates and invalidates/burns a survey token in Redis using Lua script (BR-DST-001)")
    public ResponseEntity<ApiResponse<TokenCachePayloadDTO>> burnToken(
            @RequestParam String token,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        TokenCachePayloadDTO payload = tokenCacheService.validateAndBurnToken(token)
                .orElseThrow(() -> new CampaignValidationException("BR-DST-001 / BR-DST-003: Token is invalid, expired, or already burned"));
        return ResponseEntity.ok(ApiResponse.success(payload, "Token successfully invalidated and burned", correlationId));
    }
}
