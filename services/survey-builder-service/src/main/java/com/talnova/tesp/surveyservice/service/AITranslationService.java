package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.AITranslateRequestDTO;
import com.talnova.tesp.surveyservice.dto.AITranslateResponseDTO;

/**
 * Service interface for multi-language prompt translation.
 */
public interface AITranslationService {

    /**
     * Translates a source string into specified target BCP-47 locales.
     *
     * @param request Translation request payload
     * @return AITranslateResponseDTO
     */
    AITranslateResponseDTO translateText(AITranslateRequestDTO request);
}
