package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisRequestDTO;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisResponseDTO;

/**
 * Service interface for analyzing survey questions for leading bias, loaded words, or double-barreled prompts.
 */
public interface AIBiasInspectorService {

    /**
     * Inspects a question prompt text and returns bias analysis, severity score, and rephrasing suggestions.
     *
     * @param request Analysis request payload
     * @return AIBiasAnalysisResponseDTO
     */
    AIBiasAnalysisResponseDTO analyzeQuestionBias(AIBiasAnalysisRequestDTO request);
}
