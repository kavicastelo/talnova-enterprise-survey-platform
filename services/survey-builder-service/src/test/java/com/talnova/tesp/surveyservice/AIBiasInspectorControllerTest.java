package com.talnova.tesp.surveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.controller.AIBiasInspectorController;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisRequestDTO;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisResponseDTO;
import com.talnova.tesp.surveyservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.surveyservice.service.AIBiasInspectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AIBiasInspectorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AIBiasInspectorService aiBiasInspectorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        AIBiasInspectorController controller = new AIBiasInspectorController(aiBiasInspectorService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-SRV-701-D: POST /api/v1/surveys/ai/analyze-bias returns 200 OK")
    void testAnalyzeBiasEndpointSuccess() throws Exception {
        AIBiasAnalysisRequestDTO requestDTO = AIBiasAnalysisRequestDTO.builder()
                .promptText("How great is our leadership team?")
                .questionType(QuestionType.LIKERT)
                .build();

        AIBiasAnalysisResponseDTO responseDTO = AIBiasAnalysisResponseDTO.builder()
                .isBiased(true)
                .biasType("LEADING_QUESTION")
                .score(0.85)
                .suggestion("Rephrase to: 'How would you rate the effectiveness of our leadership team?'")
                .explanation("Emotionally loaded leading word detected.")
                .build();

        when(aiBiasInspectorService.analyzeQuestionBias(any(AIBiasAnalysisRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/surveys/ai/analyze-bias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isBiased").value(true))
                .andExpect(jsonPath("$.data.biasType").value("LEADING_QUESTION"))
                .andExpect(jsonPath("$.data.score").value(0.85));
    }
}
