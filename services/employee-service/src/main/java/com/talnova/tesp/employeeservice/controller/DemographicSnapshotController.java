package com.talnova.tesp.employeeservice.controller;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.employeeservice.dto.CompileSnapshotRequestDTO;
import com.talnova.tesp.employeeservice.dto.DemographicSnapshotDTO;
import com.talnova.tesp.employeeservice.service.DemographicSnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/snapshots")
@Tag(name = "Demographic Snapshot Compiler", description = "Immutable demographic snapshot compiler for longitudinal survey analysis")
public class DemographicSnapshotController {

    private final DemographicSnapshotService snapshotService;

    public DemographicSnapshotController(DemographicSnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @PostMapping("/compile")
    @Operation(summary = "Compile Survey Demographic Snapshot", description = "Compiles and freezes immutable demographic attributes for listed employees at survey deployment time.")
    public ResponseEntity<ApiResponse<List<DemographicSnapshotDTO>>> compileSnapshot(@Valid @RequestBody CompileSnapshotRequestDTO request) {
        List<DemographicSnapshotDTO> response = snapshotService.compileSurveySnapshot(request);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Survey demographic snapshot compiled successfully", correlationId));
    }

    @GetMapping("/{snapshotId}")
    @Operation(summary = "Fetch Demographic Snapshot", description = "Retrieves an immutable frozen demographic snapshot record by snapshotId.")
    public ResponseEntity<ApiResponse<DemographicSnapshotDTO>> getSnapshot(@PathVariable("snapshotId") String snapshotId) {
        DemographicSnapshotDTO response = snapshotService.getSnapshot(snapshotId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Demographic snapshot retrieved successfully", correlationId));
    }
}
