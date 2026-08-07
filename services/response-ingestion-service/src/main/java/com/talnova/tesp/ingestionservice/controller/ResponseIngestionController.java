package com.talnova.tesp.ingestionservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.ingestionservice.dto.IngestionResponseDTO;
import com.talnova.tesp.ingestionservice.dto.ResponseSubmissionDTO;
import com.talnova.tesp.ingestionservice.service.ResponseIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/responses")
@Tag(name = "Response Ingestion API", description = "High-speed reactive endpoints for survey response submission intake")
public class ResponseIngestionController {

    private final ResponseIngestionService responseIngestionService;

    public ResponseIngestionController(ResponseIngestionService responseIngestionService) {
        this.responseIngestionService = responseIngestionService;
    }

    @PostMapping
    @Operation(summary = "Submit Survey Response", description = "Ingests survey response payload asynchronously returning HTTP 202 Accepted (< 50ms SLA)")
    public Mono<ResponseEntity<ApiResponse<IngestionResponseDTO>>> submitResponse(
            @Valid @RequestBody ResponseSubmissionDTO submission,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            @RequestHeader(value = "X-Project-ID", required = false) String projectIdHeader) {
        if (submission.getProjectId() == null || submission.getProjectId().isBlank()) {
            String contextProjectId = com.talnova.tesp.common.context.ProjectContextHolder.getProjectId();
            if (contextProjectId != null && !contextProjectId.isBlank()) {
                submission.setProjectId(contextProjectId);
            } else if (projectIdHeader != null && !projectIdHeader.isBlank()) {
                submission.setProjectId(projectIdHeader);
            }
        }
        return responseIngestionService.ingestResponse(submission)
                .map(result -> ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(ApiResponse.success(result, "Survey response ingested successfully", correlationId)));
    }
}
