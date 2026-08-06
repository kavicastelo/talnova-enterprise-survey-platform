package com.talnova.tesp.surveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.controller.SurveyBuilderController;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.surveyservice.exception.SurveyNotFoundException;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.service.SurveyBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SurveyBuilderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SurveyBuilderService surveyBuilderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SurveyBuilderController controller = new SurveyBuilderController(surveyBuilderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-SRV-202-E: PUT /api/v1/surveys/{surveyId} returns 200 OK for valid draft")
    void testSaveDraftEndpointSuccess() throws Exception {
        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-1")
                .questions(List.of(question))
                .build();

        SurveyPageDTO page = SurveyPageDTO.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(section))
                .build();

        SurveySaveDraftDTO requestDTO = SurveySaveDraftDTO.builder()
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .title(Map.of("en-US", "2026 Annual Employee Engagement Survey"))
                .pages(List.of(page))
                .build();

        SurveyResponseDTO responseDTO = SurveyResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.DRAFT)
                .build();

        when(surveyBuilderService.saveDraft(eq("SRV-5001"), any(SurveySaveDraftDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/surveys/SRV-5001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.surveyId").value("SRV-5001"))
                .andExpect(jsonPath("$.data.projectId").value("PRJ-99201"))
                .andExpect(jsonPath("$.data.version").value(1));
    }

    @Test
    @DisplayName("TC-SRV-202-F: PUT /api/v1/surveys/{surveyId} returns 400 Bad Request on validation failure")
    void testSaveDraftValidationFailure() throws Exception {
        SurveySaveDraftDTO requestDTO = SurveySaveDraftDTO.builder()
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .build();

        when(surveyBuilderService.saveDraft(eq("SRV-5001"), any(SurveySaveDraftDTO.class)))
                .thenThrow(new SurveyValidationException("Mandatory Question Group missing for quantitative question type: LIKERT (questionId: Q-101)"));

        mockMvc.perform(put("/api/v1/surveys/SRV-5001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Survey Validation Failure"))
                .andExpect(jsonPath("$.detail").value("Mandatory Question Group missing for quantitative question type: LIKERT (questionId: Q-101)"));
    }

    @Test
    @DisplayName("TC-SRV-202-G: GET /api/v1/surveys/{surveyId} returns 200 OK")
    void testGetSurveyEndpointSuccess() throws Exception {
        SurveyResponseDTO responseDTO = SurveyResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.DRAFT)
                .build();

        when(surveyBuilderService.getSurvey("PRJ-99201", "SRV-5001")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/surveys/SRV-5001")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.surveyId").value("SRV-5001"));
    }

    @Test
    @DisplayName("TC-SRV-202-H: GET /api/v1/surveys/{surveyId} returns 404 Not Found when survey does not exist")
    void testGetSurveyEndpointNotFound() throws Exception {
        when(surveyBuilderService.getSurvey("PRJ-99201", "SRV-9999"))
                .thenThrow(new SurveyNotFoundException("Survey not found for surveyId: SRV-9999 in project: PRJ-99201"));

        mockMvc.perform(get("/api/v1/surveys/SRV-9999")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Survey AST Not Found"));
    }

    @Test
    @DisplayName("TC-SRV-203-E: POST /api/v1/surveys/{surveyId}/publish returns 200 OK and published payload")
    void testPublishSurveyEndpointSuccess() throws Exception {
        SurveyResponseDTO responseDTO = SurveyResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.PUBLISHED)
                .build();

        when(surveyBuilderService.publishSurvey("PRJ-99201", "SRV-5001")).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/surveys/SRV-5001/publish")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
    }

    @Test
    @DisplayName("TC-SRV-203-F: POST /api/v1/surveys/{surveyId}/new-version returns 200 OK and new draft version payload")
    void testCreateDraftVersionEndpointSuccess() throws Exception {
        SurveyResponseDTO responseDTO = SurveyResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b4568")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(2)
                .status(SurveyStatus.DRAFT)
                .build();

        when(surveyBuilderService.createDraftVersion("PRJ-99201", "SRV-5001")).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/surveys/SRV-5001/new-version")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.version").value(2))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }
}
