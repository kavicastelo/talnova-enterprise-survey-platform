package com.talnova.tesp.configservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.configservice.dto.ContrastValidationRequestDTO;
import com.talnova.tesp.configservice.dto.ContrastValidationResponseDTO;
import com.talnova.tesp.configservice.dto.FeatureFlagsDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.dto.ProjectResponseDTO;
import com.talnova.tesp.configservice.dto.PublicThemeDTO;
import com.talnova.tesp.configservice.service.ProjectConfigService;
import com.talnova.tesp.configservice.validation.WcagColorAccessibilityValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project Configuration API", description = "Endpoints for managing multi-tenant Project workspaces, branding, and WCAG accessibility validation")
public class ProjectConfigController {

    private final ProjectConfigService projectConfigService;
    private final WcagColorAccessibilityValidator accessibilityValidator;

    public ProjectConfigController(ProjectConfigService projectConfigService,
                                   WcagColorAccessibilityValidator accessibilityValidator) {
        this.projectConfigService = projectConfigService;
        this.accessibilityValidator = accessibilityValidator;
    }

    @PostMapping
    @Operation(summary = "Provision a new Project Workspace", description = "Creates a new Project workspace metadata configuration document.")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(
            @Valid @RequestBody ProjectCreateDTO createDTO,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        ProjectResponseDTO response = projectConfigService.createProject(createDTO);
        URI location = URI.create("/api/v1/projects/" + response.getProjectId());
        return ResponseEntity.created(location)
                .body(ApiResponse.success(response, "Project workspace provisioned successfully", correlationId));
    }

    @PostMapping("/validate-theme")
    @Operation(summary = "Validate WCAG 2.1 AA Theme Accessibility", description = "Calculates relative luminance and contrast ratio between primary text and background colors.")
    public ResponseEntity<ApiResponse<ContrastValidationResponseDTO>> validateThemeAccessibility(
            @Valid @RequestBody ContrastValidationRequestDTO requestDTO,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        ContrastValidationResponseDTO response = accessibilityValidator.validateColorContrast(
                requestDTO.getPrimaryColor(), requestDTO.getBackgroundColor());
        return ResponseEntity.ok(ApiResponse.success(response, "Theme accessibility validation evaluated", correlationId));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Fetch Project Configuration", description = "Retrieves active configuration settings for the specified projectId.")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProject(
            @PathVariable("projectId") String projectId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        ProjectResponseDTO response = projectConfigService.getProjectByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.success(response, "Project workspace retrieved", correlationId));
    }

    @GetMapping("/{projectId}/public-theme")
    @Operation(summary = "Fetch Public Project Theme", description = "Unauthenticated lightweight endpoint fetching project branding colors, logo URL, and default locale.")
    public ResponseEntity<ApiResponse<PublicThemeDTO>> getPublicTheme(
            @PathVariable("projectId") String projectId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        PublicThemeDTO response = projectConfigService.getPublicTheme(projectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=60")
                .body(ApiResponse.success(response, "Public theme metadata retrieved", correlationId));
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update Project Configuration", description = "Updates branding, locales, feature flags, and custom attributes for a project.")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(
            @PathVariable("projectId") String projectId,
            @Valid @RequestBody ProjectCreateDTO updateDTO,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        ProjectResponseDTO response = projectConfigService.updateProject(projectId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Project workspace configuration updated", correlationId));
    }

    @PatchMapping("/{projectId}/features")
    @Operation(summary = "Toggle Feature Flags", description = "Updates module feature activation flags for a project.")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateFeatureFlags(
            @PathVariable("projectId") String projectId,
            @Valid @RequestBody FeatureFlagsDTO featureFlagsDTO,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        ProjectResponseDTO response = projectConfigService.updateFeatureFlags(projectId, featureFlagsDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Feature flags updated", correlationId));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Soft Delete Project Workspace", description = "Flags a project workspace as deleted.")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable("projectId") String projectId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        projectConfigService.deleteProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(null, "Project workspace soft deleted", correlationId));
    }
}
