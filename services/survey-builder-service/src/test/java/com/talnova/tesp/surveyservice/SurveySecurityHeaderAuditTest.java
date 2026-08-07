package com.talnova.tesp.surveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.controller.SurveyBuilderController;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.service.SurveyBuilderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SurveyBuilderController.class)
@ContextConfiguration(classes = {SurveyBuilderController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class SurveySecurityHeaderAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SurveyBuilderService surveyBuilderService;

    @Test
    @DisplayName("X-Project-ID Header Fallback: GET /api/v1/surveys/SRV-5001 retrieves AST without query parameter")
    void testHeaderFallbackGetSurvey() throws Exception {
        SurveyResponseDTO mockResponse = SurveyResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.DRAFT)
                .title(Map.of("en-US", "2026 Engagement Survey"))
                .pages(Collections.emptyList())
                .build();

        when(surveyBuilderService.getSurvey("PRJ-99201", "SRV-5001")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/surveys/SRV-5001")
                        .header("X-Project-ID", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.surveyId").value("SRV-5001"));
    }

    @Test
    @DisplayName("TC-SRV-001: Attempting structural edits on a published survey returns 400 Bad Request")
    void testPublishedSurveyEditLock() throws Exception {
        when(surveyBuilderService.saveDraft(eq("SRV-5001"), any()))
                .thenThrow(new SurveyValidationException("Cannot modify structural design of a published survey version"));

        String body = """
                {
                  "projectId": "PRJ-99201",
                  "surveyId": "SRV-5001",
                  "title": { "en-US": "Updated Title" },
                  "pages": []
                }
                """;

        mockMvc.perform(put("/api/v1/surveys/SRV-5001")
                        .header("X-Project-ID", "PRJ-99201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Survey Validation Failure"))
                .andExpect(jsonPath("$.detail").value("Cannot modify structural design of a published survey version"));
    }
}
