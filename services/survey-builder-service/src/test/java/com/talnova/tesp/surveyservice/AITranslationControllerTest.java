package com.talnova.tesp.surveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.controller.AITranslationController;
import com.talnova.tesp.surveyservice.dto.AITranslateRequestDTO;
import com.talnova.tesp.surveyservice.dto.AITranslateResponseDTO;
import com.talnova.tesp.surveyservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.surveyservice.service.AITranslationService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AITranslationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AITranslationService aiTranslationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        AITranslationController controller = new AITranslationController(aiTranslationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-SRV-702-C: POST /api/v1/surveys/ai/translate returns 200 OK with translations")
    void testTranslateTextEndpointSuccess() throws Exception {
        AITranslateRequestDTO requestDTO = AITranslateRequestDTO.builder()
                .sourceText("My direct manager provides clear feedback.")
                .sourceLocale("en-US")
                .targetLocales(List.of("si-LK", "es-ES"))
                .build();

        AITranslateResponseDTO responseDTO = AITranslateResponseDTO.builder()
                .translations(Map.of(
                        "si-LK", "මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.",
                        "es-ES", "Mi gerente directo proporciona comentarios claros."
                ))
                .build();

        when(aiTranslationService.translateText(any(AITranslateRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/surveys/ai/translate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.translations.si-LK").value("මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි."))
                .andExpect(jsonPath("$.data.translations.es-ES").value("Mi gerente directo proporciona comentarios claros."));
    }
}
