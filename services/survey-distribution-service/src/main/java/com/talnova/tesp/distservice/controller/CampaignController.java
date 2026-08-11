package com.talnova.tesp.distservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@Tag(name = "Survey Campaign Management API", description = "Endpoints for creating, scheduling, and managing campaign state transitions")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    @Operation(summary = "List Campaigns for Project", description = "Retrieves all active survey distribution campaigns for a given project tenant")
    public ResponseEntity<ApiResponse<List<CampaignResponseDTO>>> getCampaigns(
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            jakarta.servlet.http.HttpServletRequest request) {
        String projectId = resolveProjectId(projectIdParam, request);
        List<CampaignResponseDTO> list = campaignService.getCampaignsByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Project campaigns retrieved successfully", correlationId));
    }

    @PostMapping
    @Operation(summary = "Create & Launch Survey Campaign", description = "Launches a new survey distribution campaign to target audience nodes")
    public ResponseEntity<ApiResponse<CampaignResponseDTO>> createCampaign(
            @Valid @RequestBody CampaignCreateDTO dto,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            jakarta.servlet.http.HttpServletRequest request) {
        String resolvedProjectId = resolveProjectId(dto.getProjectId(), request);
        if (dto.getProjectId() == null || dto.getProjectId().isBlank()) {
            dto.setProjectId(resolvedProjectId);
        }
        CampaignResponseDTO created = campaignService.createCampaign(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Campaign launched successfully", correlationId));
    }

    @GetMapping("/{campaignId}")
    @Operation(summary = "Get Campaign Details", description = "Fetches survey distribution campaign configuration and real-time metrics")
    public ResponseEntity<ApiResponse<CampaignResponseDTO>> getCampaign(
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @PathVariable("campaignId") String campaignId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            jakarta.servlet.http.HttpServletRequest request) {
        String projectId = resolveProjectId(projectIdParam, request);
        CampaignResponseDTO response = campaignService.getCampaign(projectId, campaignId);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign details retrieved successfully", correlationId));
    }

    @PatchMapping("/{campaignId}/status")
    @Operation(summary = "Update Campaign Status", description = "Transitions campaign state (ACTIVE, PAUSED, COMPLETED, CANCELLED)")
    public ResponseEntity<ApiResponse<CampaignResponseDTO>> updateCampaignStatus(
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @PathVariable("campaignId") String campaignId,
            @RequestParam("status") CampaignStatus status,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            jakarta.servlet.http.HttpServletRequest request) {
        String projectId = resolveProjectId(projectIdParam, request);
        CampaignResponseDTO updated = campaignService.updateCampaignStatus(projectId, campaignId, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Campaign status updated successfully", correlationId));
    }

    private String resolveProjectId(String queryParam, jakarta.servlet.http.HttpServletRequest request) {
        if (queryParam != null && !queryParam.isBlank()) {
            return queryParam;
        }
        String contextProjectId = com.talnova.tesp.common.context.ProjectContextHolder.getProjectId();
        if (contextProjectId != null && !contextProjectId.isBlank()) {
            return contextProjectId;
        }
        if (request != null) {
            String headerProjectId = request.getHeader("X-Project-ID");
            if (headerProjectId != null && !headerProjectId.isBlank()) {
                return headerProjectId;
            }
        }
        return queryParam;
    }
}
