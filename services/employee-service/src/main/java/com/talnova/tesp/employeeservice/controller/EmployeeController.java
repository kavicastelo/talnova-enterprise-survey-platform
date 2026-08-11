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
import com.talnova.tesp.employeeservice.security.PiiMaskingUtil;
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
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody CreateEmployeeDTO request,
            @RequestHeader(value = "X-User-Roles", required = false) String userRolesHeader) {
        EmployeeResponseDTO response = employeeService.createEmployee(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{employeeId}")
                .buildAndExpand(response.getEmployeeId())
                .toUri();

        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.created(location)
                .body(ApiResponse.success(applyPiiMaskingIfRequired(response, userRolesHeader), "Employee profile created successfully", correlationId));
    }

    @GetMapping
    @Operation(summary = "List Employee Directory Roster", description = "Fetches employee roster profiles for active project with optional filtering by nodeId and status.")
    public ResponseEntity<ApiResponse<java.util.List<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @RequestParam(value = "nodeId", required = false) String nodeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader(value = "X-User-Roles", required = false) String userRolesHeader,
            jakarta.servlet.http.HttpServletRequest servletRequest) {

        String projectId = resolveProjectId(projectIdParam, servletRequest);
        java.util.List<EmployeeResponseDTO> list = employeeService.getAllEmployees(projectId, nodeId, status);
        java.util.List<EmployeeResponseDTO> maskedList = list.stream()
                .map(emp -> applyPiiMaskingIfRequired(emp, userRolesHeader))
                .toList();

        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(maskedList, "Employee roster retrieved successfully", correlationId));
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Fetch Employee Profile", description = "Fetches employee profile details by projectId and employeeId.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployee(
            @PathVariable("employeeId") String employeeId,
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @RequestHeader(value = "X-User-Roles", required = false) String userRolesHeader,
            jakarta.servlet.http.HttpServletRequest servletRequest) {

        String projectId = resolveProjectId(projectIdParam, servletRequest);
        EmployeeResponseDTO response = employeeService.getEmployeeByProjectIdAndEmployeeId(projectId, employeeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(applyPiiMaskingIfRequired(response, userRolesHeader), "Employee profile retrieved successfully", correlationId));
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update Employee Profile", description = "Updates an employee profile, demographic attributes, and primary/matrix organizational assignments.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable("employeeId") String employeeId,
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @Valid @RequestBody UpdateEmployeeDTO request,
            @RequestHeader(value = "X-User-Roles", required = false) String userRolesHeader,
            jakarta.servlet.http.HttpServletRequest servletRequest) {

        String projectId = resolveProjectId(projectIdParam, servletRequest);
        EmployeeResponseDTO response = employeeService.updateEmployee(projectId, employeeId, request);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(applyPiiMaskingIfRequired(response, userRolesHeader), "Employee profile updated successfully", correlationId));
    }

    @PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Bulk CSV Roster Ingestion", description = "High-speed streaming CSV roster import processing records in 5,000 batches.")
    public ResponseEntity<ApiResponse<BulkImportResultDTO>> bulkImportCsv(
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "autoTerminateMissing", defaultValue = "false") boolean autoTerminateMissing,
            jakarta.servlet.http.HttpServletRequest servletRequest) throws Exception {

        String projectId = resolveProjectId(projectIdParam, servletRequest);
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
            @RequestParam(value = "projectId", required = false) String projectIdParam,
            jakarta.servlet.http.HttpServletRequest servletRequest) {

        String projectId = resolveProjectId(projectIdParam, servletRequest);
        EmployeeResponseDTO response = employeeService.forgetEmployeeProfile(projectId, employeeId);
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "Employee PII anonymized for GDPR Right-to-be-Forgotten compliance", correlationId));
    }

    private String resolveProjectId(String queryParam, jakarta.servlet.http.HttpServletRequest request) {
        if (queryParam != null && !queryParam.isBlank()) {
            return queryParam;
        }
        String contextProjectId = ProjectContextHolder.getProjectId();
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

    private EmployeeResponseDTO applyPiiMaskingIfRequired(EmployeeResponseDTO response, String userRolesHeader) {
        if (response == null) {
            return null;
        }
        if (userRolesHeader != null && userRolesHeader.contains("DEPARTMENT_MANAGER")
                && !userRolesHeader.contains("PROJECT_ADMIN")
                && !userRolesHeader.contains("HR_MANAGER")) {
            return EmployeeResponseDTO.builder()
                    .id(response.getId())
                    .projectId(response.getProjectId())
                    .employeeId(response.getEmployeeId())
                    .email(PiiMaskingUtil.maskEmail(response.getEmail()))
                    .fullName(PiiMaskingUtil.maskFullName(response.getFullName()))
                    .phoneNumber(PiiMaskingUtil.maskPhoneNumber(response.getPhoneNumber()))
                    .nodeId(response.getNodeId())
                    .matrixNodeIds(response.getMatrixNodeIds())
                    .status(response.getStatus())
                    .attributes(response.getAttributes())
                    .version(response.getVersion())
                    .createdAt(response.getCreatedAt())
                    .updatedAt(response.getUpdatedAt())
                    .build();
        }
        return response;
    }
}
