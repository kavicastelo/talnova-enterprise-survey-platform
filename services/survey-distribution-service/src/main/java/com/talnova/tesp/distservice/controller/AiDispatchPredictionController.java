package com.talnova.tesp.distservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionRequestDTO;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionResponseDTO;
import com.talnova.tesp.distservice.service.AiOptimalDispatchPredictorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/distribution")
@Tag(name = "AI Distribution Intelligence API", description = "Endpoints for AI-driven optimal recipient dispatch hour predictions")
public class AiDispatchPredictionController {

    private final AiOptimalDispatchPredictorService predictorService;

    public AiDispatchPredictionController(AiOptimalDispatchPredictorService predictorService) {
        this.predictorService = predictorService;
    }

    @PostMapping("/ai-optimal-time")
    @Operation(summary = "Predict Optimal Recipient Dispatch Hour", description = "AI heuristic prediction of optimal dispatch hour to maximize survey open and response rates")
    public ResponseEntity<ApiResponse<OptimalDispatchPredictionResponseDTO>> predictOptimalTime(
            @Valid @RequestBody OptimalDispatchPredictionRequestDTO request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        OptimalDispatchPredictionResponseDTO prediction = predictorService.predictOptimalDispatchHour(request);
        return ResponseEntity.ok(ApiResponse.success(prediction, "AI optimal dispatch hour predicted successfully", correlationId));
    }
}
