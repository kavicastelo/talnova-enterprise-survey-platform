package com.talnova.tesp.employeeservice.controller;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.employeeservice.dto.HeaderMappingRequestDTO;
import com.talnova.tesp.employeeservice.dto.HeaderMappingResponseDTO;
import com.talnova.tesp.employeeservice.service.ai.AiHeaderMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees/ai")
@Tag(name = "AI Header Mapper Engine", description = "AI Fuzzy CSV Header-to-Attribute Mapping API")
public class AiHeaderMappingController {

    private final AiHeaderMappingService headerMappingService;

    public AiHeaderMappingController(AiHeaderMappingService headerMappingService) {
        this.headerMappingService = headerMappingService;
    }

    @PostMapping("/map-headers")
    @Operation(summary = "AI Fuzzy CSV Header Mapping", description = "Analyzes unstandardized CSV column headers and recommends attribute mappings with confidence scores.")
    public ResponseEntity<ApiResponse<HeaderMappingResponseDTO>> mapHeaders(@Valid @RequestBody HeaderMappingRequestDTO request) {
        HeaderMappingResponseDTO response = headerMappingService.mapHeaders(request.getHeaders());
        String correlationId = ProjectContextHolder.getCorrelationId();
        return ResponseEntity.ok(ApiResponse.success(response, "CSV headers mapped successfully", correlationId));
    }
}
