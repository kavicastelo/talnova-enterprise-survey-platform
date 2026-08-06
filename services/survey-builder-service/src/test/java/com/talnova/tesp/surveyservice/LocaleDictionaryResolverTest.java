package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.service.LocaleDictionaryResolverImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LocaleDictionaryResolverTest {

    private LocaleDictionaryResolverImpl localeResolver;

    @BeforeEach
    void setUp() {
        localeResolver = new LocaleDictionaryResolverImpl();
    }

    @Test
    @DisplayName("TC-SRV-501-A: Question prompt containing default locale passes validation")
    void testValidateSurveyLocalesSuccess() {
        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder().sectionId("SEC-1").questions(List.of(question)).build();
        SurveyPageDTO page = SurveyPageDTO.builder().pageId("PAGE-1").sections(List.of(section)).build();
        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder().pages(List.of(page)).build();

        assertDoesNotThrow(() -> localeResolver.validateSurveyLocales(draftDTO, "en-US"));
    }

    @Test
    @DisplayName("TC-SRV-501-B: Question prompt missing default locale throws SurveyValidationException")
    void testValidateSurveyLocalesMissingDefaultLocaleThrowsException() {
        SurveyQuestionDTO questionWithoutDefaultLocale = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .prompt(Map.of("si-LK", "මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.")) // Missing en-US!
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder().sectionId("SEC-1").questions(List.of(questionWithoutDefaultLocale)).build();
        SurveyPageDTO page = SurveyPageDTO.builder().pageId("PAGE-1").sections(List.of(section)).build();
        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder().pages(List.of(page)).build();

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> localeResolver.validateSurveyLocales(draftDTO, "en-US")
        );

        assertTrue(ex.getMessage().contains("Missing default locale prompt"));
    }

    @Test
    @DisplayName("TC-SRV-501-C: Resolve text targets requested locale and falls back to default locale if missing")
    void testResolveTextFallback() {
        Map<String, String> promptMap = new HashMap<>();
        promptMap.put("en-US", "My manager provides clear direction.");
        promptMap.put("si-LK", "මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.");

        // Target locale present
        assertEquals("මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.", localeResolver.resolveText(promptMap, "si-LK", "en-US"));

        // Target locale missing -> fallback to en-US
        assertEquals("My manager provides clear direction.", localeResolver.resolveText(promptMap, "ta-LK", "en-US"));
    }
}
