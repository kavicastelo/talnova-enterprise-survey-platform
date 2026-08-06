package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisRequestDTO;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisResponseDTO;
import com.talnova.tesp.surveyservice.service.AIBiasInspectorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AIBiasInspectorServiceTest {

    private AIBiasInspectorServiceImpl aiBiasInspectorService;

    @BeforeEach
    void setUp() {
        aiBiasInspectorService = new AIBiasInspectorServiceImpl();
    }

    @Test
    @DisplayName("TC-SRV-701-A: AI Service flags leading question ('How great is our leadership team?') with high bias score")
    void testAnalyzeLeadingQuestionBias() {
        AIBiasAnalysisRequestDTO request = AIBiasAnalysisRequestDTO.builder()
                .promptText("How great is our leadership team?")
                .questionType(QuestionType.LIKERT)
                .build();

        AIBiasAnalysisResponseDTO response = aiBiasInspectorService.analyzeQuestionBias(request);

        assertNotNull(response);
        assertTrue(response.isBiased());
        assertEquals("LEADING_QUESTION", response.getBiasType());
        assertTrue(response.getScore() >= 0.8);
        assertTrue(response.getSuggestion().contains("How would you rate the effectiveness"));
    }

    @Test
    @DisplayName("TC-SRV-701-B: AI Service flags double-barreled question prompt")
    void testAnalyzeDoubleBarreledQuestionBias() {
        AIBiasAnalysisRequestDTO request = AIBiasAnalysisRequestDTO.builder()
                .promptText("My manager provides clear direction and recognizes my work.")
                .questionType(QuestionType.LIKERT)
                .build();

        AIBiasAnalysisResponseDTO response = aiBiasInspectorService.analyzeQuestionBias(request);

        assertNotNull(response);
        assertTrue(response.isBiased());
        assertEquals("DOUBLE_BARRELED", response.getBiasType());
        assertTrue(response.getSuggestion().contains("Split into two separate questions"));
    }

    @Test
    @DisplayName("TC-SRV-701-C: AI Service evaluates neutral question prompt cleanly")
    void testAnalyzeNeutralQuestion() {
        AIBiasAnalysisRequestDTO request = AIBiasAnalysisRequestDTO.builder()
                .promptText("How satisfied or dissatisfied are you with the communication from leadership?")
                .questionType(QuestionType.LIKERT)
                .build();

        AIBiasAnalysisResponseDTO response = aiBiasInspectorService.analyzeQuestionBias(request);

        assertNotNull(response);
        assertFalse(response.isBiased());
        assertEquals("NEUTRAL", response.getBiasType());
        assertEquals(0.0, response.getScore());
    }
}
