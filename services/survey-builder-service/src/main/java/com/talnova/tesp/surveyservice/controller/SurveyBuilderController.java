package com.talnova.tesp.surveyservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.service.SurveyBuilderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "Survey Builder API", description = "Endpoints for metadata-driven survey questionnaire construction, AST editing, and versioning")
public class SurveyBuilderController {

    private final SurveyBuilderService surveyBuilderService;

    public SurveyBuilderController(SurveyBuilderService surveyBuilderService) {
        this.surveyBuilderService = surveyBuilderService;
    }

    @PutMapping("/{surveyId}")
    @Operation(summary = "Save or Update Survey AST Draft", description = "Saves or updates a draft survey JSON Abstract Syntax Tree for a project tenant.")
    public ResponseEntity<ApiResponse<SurveyResponseDTO>> saveDraft(
            @PathVariable("surveyId") String surveyId,
            @Valid @RequestBody SurveySaveDraftDTO dto,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        SurveyResponseDTO response = surveyBuilderService.saveDraft(surveyId, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Survey draft saved successfully", correlationId));
    }

    @GetMapping("/{surveyId}")
    @Operation(summary = "Fetch Survey AST Payload", description = "Retrieves the latest survey AST payload for a specified surveyId and tenant projectId.")
    public ResponseEntity<ApiResponse<SurveyResponseDTO>> getSurvey(
            @PathVariable("surveyId") String surveyId,
            @RequestParam("projectId") String projectId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        SurveyResponseDTO response = surveyBuilderService.getSurvey(projectId, surveyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Survey AST payload retrieved successfully", correlationId));
    }

    @PostMapping("/{surveyId}/publish")
    @Operation(summary = "Publish Survey Version", description = "Validates and publishes a draft survey version, locking structural edits and setting status to PUBLISHED.")
    public ResponseEntity<ApiResponse<SurveyResponseDTO>> publishSurvey(
            @PathVariable("surveyId") String surveyId,
            @RequestParam("projectId") String projectId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        SurveyResponseDTO response = surveyBuilderService.publishSurvey(projectId, surveyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Survey published successfully", correlationId));
    }

    @PostMapping("/{surveyId}/new-version")
    @Operation(summary = "Create New Draft Version", description = "Creates a new editable draft version (N+1) from an existing published survey.")
    public ResponseEntity<ApiResponse<SurveyResponseDTO>> createDraftVersion(
            @PathVariable("surveyId") String surveyId,
            @RequestParam("projectId") String projectId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        SurveyResponseDTO response = surveyBuilderService.createDraftVersion(projectId, surveyId);
        return ResponseEntity.ok(ApiResponse.success(response, "New draft version created successfully", correlationId));
    }
}
