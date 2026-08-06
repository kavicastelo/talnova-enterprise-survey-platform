package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionRequestDTO;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionResponseDTO;

import java.util.List;

public interface AiOptimalDispatchPredictorService {

    OptimalDispatchPredictionResponseDTO predictOptimalDispatchHour(OptimalDispatchPredictionRequestDTO request);

    List<OptimalDispatchPredictionResponseDTO> predictBatchOptimalDispatchHours(String projectId, List<OptimalDispatchPredictionRequestDTO> requests);
}
