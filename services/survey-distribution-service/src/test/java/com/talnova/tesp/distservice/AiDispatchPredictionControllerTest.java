package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.controller.AiDispatchPredictionController;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionRequestDTO;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionResponseDTO;
import com.talnova.tesp.distservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.distservice.service.AiOptimalDispatchPredictorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class AiDispatchPredictionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AiOptimalDispatchPredictorService predictorService;

    @InjectMocks
    private AiDispatchPredictionController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-DST-701-04: POST /api/v1/distribution/ai-optimal-time returns 200 OK")
    void testPredictOptimalTimeEndpoint() throws Exception {
        OptimalDispatchPredictionRequestDTO req = OptimalDispatchPredictionRequestDTO.builder()
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .department("HR")
                .build();

        OptimalDispatchPredictionResponseDTO res = OptimalDispatchPredictionResponseDTO.builder()
                .employeeId("EMP-10020")
                .recommendedHour(9)
                .recommendedTimeString("09:00")
                .confidenceScore(0.92)
                .reasoning("Corporate office check-in window")
                .build();

        when(predictorService.predictOptimalDispatchHour(any())).thenReturn(res);

        mockMvc.perform(post("/api/v1/distribution/ai-optimal-time")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.recommendedTimeString").value("09:00"))
                .andExpect(jsonPath("$.data.confidenceScore").value(0.92));
    }
}
