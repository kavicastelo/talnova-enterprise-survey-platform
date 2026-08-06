package com.talnova.tesp.surveyservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisRequestDTO;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisResponseDTO;
import com.talnova.tesp.surveyservice.service.AIBiasInspectorService;
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
public class AIBiasInspectorController {

    private final AIBiasInspectorService aiBiasInspectorService;

    public AIBiasInspectorController(AIBiasInspectorService aiBiasInspectorService) {
        this.aiBiasInspectorService = aiBiasInspectorService;
    }

    @PostMapping("/analyze-bias")
    @Operation(summary = "Analyze Question Prompt Bias", description = "Analyzes a question prompt for leading bias, double-barreled structure, or loaded language, returning risk scores and suggestions.")
    public ResponseEntity<ApiResponse<AIBiasAnalysisResponseDTO>> analyzeQuestionBias(
            @Valid @RequestBody AIBiasAnalysisRequestDTO dto,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        AIBiasAnalysisResponseDTO response = aiBiasInspectorService.analyzeQuestionBias(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Question bias analysis completed", correlationId));
    }
}
