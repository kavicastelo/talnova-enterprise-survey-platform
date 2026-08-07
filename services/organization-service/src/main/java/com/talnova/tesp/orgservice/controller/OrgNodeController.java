package com.talnova.tesp.orgservice.controller;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.HierarchyAnomalyReportDTO;
import com.talnova.tesp.orgservice.dto.MoveNodeRequestDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import com.talnova.tesp.orgservice.service.HierarchyAnomalyInspector;
import com.talnova.tesp.orgservice.service.OrgNodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
@Tag(name = "Organization Node Management", description = "Materialized path organizational hierarchy tree APIs")
public class OrgNodeController {

    private final OrgNodeService nodeService;
    private final HierarchyAnomalyInspector anomalyInspector;

    public OrgNodeController(OrgNodeService nodeService, HierarchyAnomalyInspector anomalyInspector) {
        this.nodeService = nodeService;
        this.anomalyInspector = anomalyInspector;
    }

    @PostMapping
    @Operation(summary = "Create Organization Node", description = "Creates a new organization node and materializes its full path lineage.")
    public ResponseEntity<ApiResponse<OrgNodeResponseDTO>> createNode(@Valid @RequestBody CreateNodeDTO request) {
        OrgNodeResponseDTO response = nodeService.createNode(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{nodeId}")
                .buildAndExpand(response.getNodeId())
                .toUri();

        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.created(location)
                .body(ApiResponse.success(response, "Organization node created successfully", correlationId));
    }

    @GetMapping("/{nodeId}")
    @Operation(summary = "Fetch Organization Node", description = "Fetches organization node details by projectId and nodeId.")
    public ResponseEntity<ApiResponse<OrgNodeResponseDTO>> getNode(
            @PathVariable("nodeId") String nodeId,
            @RequestParam(value = "projectId", required = false) String projectIdParam) {

        String projectId = resolveProjectId(projectIdParam);
        OrgNodeResponseDTO response = nodeService.getNodeByProjectIdAndNodeId(projectId, nodeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Organization node retrieved successfully", correlationId));
    }

    @PostMapping("/{nodeId}/move")
    @Operation(summary = "Re-parent Organization Node", description = "Atomically re-parents a node and bulk updates all descendant sub-tree materialized paths.")
    public ResponseEntity<ApiResponse<OrgNodeResponseDTO>> moveNode(
            @PathVariable("nodeId") String nodeId,
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @RequestBody(required = false) MoveNodeRequestDTO moveRequest) {

        String projectId = resolveProjectId(projectIdParam);
        OrgNodeResponseDTO response = nodeService.moveNode(projectId, nodeId, moveRequest);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Organization node re-parented successfully", correlationId));
    }

    @GetMapping("/{nodeId}/subtree")
    @Operation(summary = "Fetch Sub-Tree Nodes", description = "Fetches all descendant child nodes matching the materialized path prefix of the specified node.")
    public ResponseEntity<ApiResponse<List<OrgNodeResponseDTO>>> getSubTree(
            @PathVariable("nodeId") String nodeId,
            @RequestParam(value = "projectId", required = false) String projectIdParam) {

        String projectId = resolveProjectId(projectIdParam);
        List<OrgNodeResponseDTO> response = nodeService.getSubTree(projectId, nodeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Sub-tree nodes retrieved successfully", correlationId));
    }

    @GetMapping("/{nodeId}/lineage")
    @Operation(summary = "Fetch Ancestor Lineage Path", description = "Returns ordered array of ancestor nodes from Root down to requested nodeId.")
    public ResponseEntity<ApiResponse<List<OrgNodeResponseDTO>>> getLineage(
            @PathVariable("nodeId") String nodeId,
            @RequestParam(value = "projectId", required = false) String projectIdParam) {

        String projectId = resolveProjectId(projectIdParam);
        List<OrgNodeResponseDTO> response = nodeService.getLineage(projectId, nodeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Ancestor lineage path retrieved successfully", correlationId));
    }

    @GetMapping("/anomalies")
    @Operation(summary = "AI Hierarchy Anomaly Inspection", description = "Scans organizational tree for extreme depth, orphan sub-trees, or single-child chain anomalies.")
    public ResponseEntity<ApiResponse<HierarchyAnomalyReportDTO>> inspectAnomalies(
            @RequestParam(value = "projectId", required = false) String projectIdParam) {
        String projectId = resolveProjectId(projectIdParam);
        HierarchyAnomalyReportDTO report = anomalyInspector.inspectAnomalies(projectId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(report, "Hierarchy anomaly inspection completed", correlationId));
    }

    private String resolveProjectId(String queryParam) {
        if (queryParam != null && !queryParam.isBlank()) {
            return queryParam;
        }
        String contextProjectId = ProjectContextHolder.getProjectId();
        if (contextProjectId != null && !contextProjectId.isBlank()) {
            return contextProjectId;
        }
        return queryParam;
    }
}
