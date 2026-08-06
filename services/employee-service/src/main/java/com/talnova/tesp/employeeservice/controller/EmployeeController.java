package com.talnova.tesp.employeeservice.controller;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import com.talnova.tesp.employeeservice.dto.HrisSyncRequestDTO;
import com.talnova.tesp.employeeservice.dto.UpdateEmployeeDTO;
import com.talnova.tesp.employeeservice.service.BulkImportService;
import com.talnova.tesp.employeeservice.service.EmployeeService;
import com.talnova.tesp.employeeservice.service.hris.HrisSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Roster Management", description = "Employee profile CRUD and demographic attribute APIs")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final BulkImportService bulkImportService;
    private final HrisSyncService hrisSyncService;

    public EmployeeController(EmployeeService employeeService,
                              BulkImportService bulkImportService,
                              HrisSyncService hrisSyncService) {
        this.employeeService = employeeService;
        this.bulkImportService = bulkImportService;
        this.hrisSyncService = hrisSyncService;
    }

    @PostMapping
    @Operation(summary = "Create Employee Profile", description = "Creates a new employee profile with CSFLE encrypted PII and dynamic demographic attributes.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody CreateEmployeeDTO request) {
        EmployeeResponseDTO response = employeeService.createEmployee(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{employeeId}")
                .buildAndExpand(response.getEmployeeId())
                .toUri();

        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.created(location)
                .body(ApiResponse.success(response, "Employee profile created successfully", correlationId));
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Fetch Employee Profile", description = "Fetches employee profile details by projectId and employeeId.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployee(
            @PathVariable("employeeId") String employeeId,
            @RequestParam("projectId") String projectId) {

        EmployeeResponseDTO response = employeeService.getEmployeeByProjectIdAndEmployeeId(projectId, employeeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully", correlationId));
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update Employee Profile", description = "Updates an employee profile, demographic attributes, and primary/matrix organizational assignments.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable("employeeId") String employeeId,
            @RequestParam("projectId") String projectId,
            @Valid @RequestBody UpdateEmployeeDTO request) {

        EmployeeResponseDTO response = employeeService.updateEmployee(projectId, employeeId, request);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile updated successfully", correlationId));
    }

    @PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Bulk CSV Roster Ingestion", description = "High-speed streaming CSV roster import processing records in 5,000 batches.")
    public ResponseEntity<ApiResponse<BulkImportResultDTO>> bulkImportCsv(
            @RequestParam("projectId") String projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "autoTerminateMissing", defaultValue = "false") boolean autoTerminateMissing) throws Exception {

        BulkImportResultDTO result = bulkImportService.processCsvImport(projectId, file.getInputStream(), autoTerminateMissing);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(result, "Bulk CSV roster import completed", correlationId));
    }

    @PostMapping("/hris/sync")
    @Operation(summary = "Automated HRIS Roster Sync", description = "Automated roster synchronization from Workday RaaS API or SAP SuccessFactors OData API.")
    public ResponseEntity<ApiResponse<BulkImportResultDTO>> syncHrisRoster(@Valid @RequestBody HrisSyncRequestDTO request) {
        BulkImportResultDTO result = hrisSyncService.syncHrisRoster(request);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(result, "HRIS roster sync completed successfully", correlationId));
    }

    @PostMapping("/{employeeId}/gdpr-anonymize")
    @Operation(summary = "GDPR Right-to-be-Forgotten Anonymization", description = "Overwrites all sensitive PII with anonymized scramble hashes and flags profile as soft-deleted.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> anonymizeEmployee(
            @PathVariable("employeeId") String employeeId,
            @RequestParam("projectId") String projectId) {

        EmployeeResponseDTO response = employeeService.forgetEmployeeProfile(projectId, employeeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Employee PII anonymized for GDPR Right-to-be-Forgotten compliance", correlationId));
    }
}
