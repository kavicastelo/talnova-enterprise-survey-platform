package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.dto.AITranslateRequestDTO;
import com.talnova.tesp.surveyservice.dto.AITranslateResponseDTO;
import com.talnova.tesp.surveyservice.service.AITranslationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AITranslationServiceTest {

    private AITranslationServiceImpl aiTranslationService;

    @BeforeEach
    void setUp() {
        aiTranslationService = new AITranslationServiceImpl();
    }

    @Test
    @DisplayName("TC-SRV-702-A: Multi-language translation returns populated translations map for target locales")
    void testTranslateTextSuccess() {
        AITranslateRequestDTO request = AITranslateRequestDTO.builder()
                .sourceText("My direct manager provides clear feedback.")
                .sourceLocale("en-US")
                .targetLocales(List.of("si-LK", "ta-LK", "es-ES"))
                .build();

        AITranslateResponseDTO response = aiTranslationService.translateText(request);

        assertNotNull(response);
        assertNotNull(response.getTranslations());
        assertEquals(3, response.getTranslations().size());
        assertEquals("මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.", response.getTranslations().get("si-LK"));
        assertEquals("எனது மேலாளர் தெளிவான கருத்துக்களை வழங்குகிறார்.", response.getTranslations().get("ta-LK"));
        assertEquals("Mi gerente directo proporciona comentarios claros.", response.getTranslations().get("es-ES"));
    }

    @Test
    @DisplayName("TC-SRV-702-B: Unmapped string uses fallback translation engine format")
    void testTranslateTextFallback() {
        AITranslateRequestDTO request = AITranslateRequestDTO.builder()
                .sourceText("Custom unmapped engagement prompt.")
                .sourceLocale("en-US")
                .targetLocales(List.of("fr-FR"))
                .build();

        AITranslateResponseDTO response = aiTranslationService.translateText(request);

        assertNotNull(response);
        assertEquals("[fr-FR] Custom unmapped engagement prompt.", response.getTranslations().get("fr-FR"));
    }
}
