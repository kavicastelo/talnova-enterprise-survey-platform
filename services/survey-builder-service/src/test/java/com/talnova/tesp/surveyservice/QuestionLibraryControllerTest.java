package com.talnova.tesp.surveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.controller.QuestionLibraryController;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryCreateDTO;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryResponseDTO;
import com.talnova.tesp.surveyservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.surveyservice.service.QuestionLibraryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuestionLibraryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuestionLibraryService questionLibraryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        QuestionLibraryController controller = new QuestionLibraryController(questionLibraryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-SRV-401-D: POST /api/v1/question-library returns 201 Created")
    void testCreateQuestionTemplateEndpointSuccess() throws Exception {
        QuestionLibraryCreateDTO requestDTO = QuestionLibraryCreateDTO.builder()
                .libraryId("LIB-LEAD-001")
                .category("LEADERSHIP")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .build();

        QuestionLibraryResponseDTO responseDTO = QuestionLibraryResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b9999")
                .libraryId("LIB-LEAD-001")
                .category("LEADERSHIP")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .build();

        when(questionLibraryService.createQuestionTemplate(any(QuestionLibraryCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/question-library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/question-library/LIB-LEAD-001"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.libraryId").value("LIB-LEAD-001"));
    }

    @Test
    @DisplayName("TC-SRV-401-E: GET /api/v1/question-library returns 200 OK with list of templates")
    void testSearchQuestionsEndpointSuccess() throws Exception {
        QuestionLibraryResponseDTO item1 = QuestionLibraryResponseDTO.builder()
                .libraryId("LIB-LEAD-001")
                .category("LEADERSHIP")
                .type(QuestionType.LIKERT)
                .build();

        when(questionLibraryService.searchQuestions(eq("LEADERSHIP"), eq("direction"))).thenReturn(List.of(item1));

        mockMvc.perform(get("/api/v1/question-library")
                        .param("category", "LEADERSHIP")
                        .param("search", "direction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].libraryId").value("LIB-LEAD-001"));
    }
}
