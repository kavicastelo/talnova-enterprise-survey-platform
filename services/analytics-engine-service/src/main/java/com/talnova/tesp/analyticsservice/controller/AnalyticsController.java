package com.talnova.tesp.analyticsservice.controller;

import com.talnova.tesp.analyticsservice.cache.AnalyticsCacheService;
import com.talnova.tesp.analyticsservice.dto.DashboardMetricsDTO;
import com.talnova.tesp.analyticsservice.dto.HeatmapMatrixDTO;
import com.talnova.tesp.analyticsservice.filter.DemographicFilterCompilerService;
import com.talnova.tesp.analyticsservice.security.NodeScopeAbacFilterService;
import com.talnova.tesp.analyticsservice.service.HeatmapAggregatorService;
import com.talnova.tesp.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics & Heatmap Engine", description = "Real-time engagement scoring, 2D heatmaps, and differential privacy suppression APIs")
public class AnalyticsController {

    private final HeatmapAggregatorService heatmapAggregatorService;
    private final NodeScopeAbacFilterService abacFilterService;
    private final DemographicFilterCompilerService filterCompilerService;
    private final AnalyticsCacheService cacheService;

    public AnalyticsController(HeatmapAggregatorService heatmapAggregatorService,
                               NodeScopeAbacFilterService abacFilterService,
                               DemographicFilterCompilerService filterCompilerService,
                               AnalyticsCacheService cacheService) {
        this.heatmapAggregatorService = heatmapAggregatorService;
        this.abacFilterService = abacFilterService;
        this.filterCompilerService = filterCompilerService;
        this.cacheService = cacheService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Fetch campaign real-time dashboard overview metrics")
    public ResponseEntity<ApiResponse<DashboardMetricsDTO>> getDashboardMetrics(
            @RequestHeader(value = "X-Project-ID", required = false) String projectIdHeader,
            @RequestHeader(value = "X-User-NodeScope", required = false) String userNodeScope,
            @RequestParam("campaignId") String campaignId,
            @RequestParam(value = "nodeId", required = false, defaultValue = "N-ROOT") String nodeId,
            @RequestParam Map<String, String> rawQueryParams) {

        String resolvedProjectId = resolveProjectId(projectIdHeader);
        abacFilterService.validateNodeScopeAccess(userNodeScope, nodeId);
        Map<String, String> demographicFilters = filterCompilerService.compileFilters(rawQueryParams);

        DashboardMetricsDTO metrics = heatmapAggregatorService.getDashboardMetrics(resolvedProjectId, campaignId, nodeId, demographicFilters);
        return ResponseEntity.ok(ApiResponse.success(metrics, "Dashboard metrics retrieved successfully", UUID.randomUUID().toString()));
    }

    @GetMapping("/heatmap")
    @Operation(summary = "Fetch 2D comparative organizational heatmap matrix")
    public ResponseEntity<ApiResponse<HeatmapMatrixDTO>> getHeatmapMatrix(
            @RequestHeader(value = "X-Project-ID", required = false) String projectIdHeader,
            @RequestHeader(value = "X-User-NodeScope", required = false) String userNodeScope,
            @RequestParam("campaignId") String campaignId,
            @RequestParam(value = "parentNodeId", required = false, defaultValue = "N-ROOT") String parentNodeId,
            @RequestParam Map<String, String> rawQueryParams) {

        String resolvedProjectId = resolveProjectId(projectIdHeader);
        abacFilterService.validateNodeScopeAccess(userNodeScope, parentNodeId);
        Map<String, String> demographicFilters = filterCompilerService.compileFilters(rawQueryParams);

        HeatmapMatrixDTO matrix = heatmapAggregatorService.computeHeatmapMatrix(resolvedProjectId, campaignId, parentNodeId, demographicFilters);
        return ResponseEntity.ok(ApiResponse.success(matrix, "2D Heatmap matrix retrieved successfully", UUID.randomUUID().toString()));
    }

    private String resolveProjectId(String headerProjectId) {
        String contextProjectId = com.talnova.tesp.common.context.ProjectContextHolder.getProjectId();
        if (contextProjectId != null && !contextProjectId.isBlank()) {
            return contextProjectId;
        }
        if (headerProjectId != null && !headerProjectId.isBlank() && !"PRJ-DEFAULT".equals(headerProjectId)) {
            return headerProjectId;
        }
        return "PRJ-DEFAULT";
    }
}
