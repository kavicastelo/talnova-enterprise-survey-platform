package com.talnova.tesp.surveyservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.surveyservice.dto.AITranslateRequestDTO;
import com.talnova.tesp.surveyservice.dto.AITranslateResponseDTO;
import com.talnova.tesp.surveyservice.service.AITranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/surveys/ai")
@Tag(name = "AI Survey Assistant API", description = "Endpoints for AI-powered leading question & bias inspection and translation")
public class AITranslationController {

    private final AITranslationService aiTranslationService;

    public AITranslationController(AITranslationService aiTranslationService) {
        this.aiTranslationService = aiTranslationService;
    }

    @PostMapping("/translate")
    @Operation(summary = "One-Click Multi-Language Translation", description = "Translates a source question prompt into multiple target BCP-47 locales automatically.")
    public ResponseEntity<ApiResponse<AITranslateResponseDTO>> translateText(
            @Valid @RequestBody AITranslateRequestDTO dto,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        AITranslateResponseDTO response = aiTranslationService.translateText(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Multi-language translations generated successfully", correlationId));
    }
}
