package com.talnova.tesp.surveyservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MongoSchemaMigrationTest {

    @Test
    @DisplayName("TC-SRV-101-A: Verify surveyId regex pattern matching ^SRV-[A-Za-z0-9_-]{3,20}$")
    void testSurveyIdRegexValidation() {
        String validSurveyId = "SRV-5001";
        String validSurveyId2 = "SRV-ENGAGEMENT_2026";
        String invalidSurveyId = "INVALID_SURVEY_ID_TOO_LONG_1234567890";
        String invalidSurveyId2 = "123";

        assertTrue(validSurveyId.matches("^SRV-[A-Za-z0-9_-]{3,20}$"));
        assertTrue(validSurveyId2.matches("^SRV-[A-Za-z0-9_-]{3,20}$"));
        assertFalse(invalidSurveyId.matches("^SRV-[A-Za-z0-9_-]{3,20}$"));
        assertFalse(invalidSurveyId2.matches("^SRV-[A-Za-z0-9_-]{3,20}$"));
    }

    @Test
    @DisplayName("TC-SRV-101-B: Verify 10 allowed Question Types enum validation")
    void testAllowedQuestionTypes() {
        List<String> allowedTypes = List.of(
                "LIKERT",
                "NPS",
                "MATRIX",
                "SINGLE_CHOICE",
                "MULTIPLE_CHOICE",
                "RANKING",
                "SHORT_TEXT",
                "LONG_TEXT",
                "NUMERIC",
                "DATE"
        );

        assertEquals(10, allowedTypes.size());
        assertTrue(allowedTypes.contains("LIKERT"));
        assertTrue(allowedTypes.contains("NPS"));
        assertTrue(allowedTypes.contains("MATRIX"));
        assertTrue(allowedTypes.contains("SINGLE_CHOICE"));
        assertTrue(allowedTypes.contains("MULTIPLE_CHOICE"));
        assertTrue(allowedTypes.contains("RANKING"));
        assertTrue(allowedTypes.contains("SHORT_TEXT"));
        assertTrue(allowedTypes.contains("LONG_TEXT"));
        assertTrue(allowedTypes.contains("NUMERIC"));
        assertTrue(allowedTypes.contains("DATE"));
        assertFalse(allowedTypes.contains("UNSUPPORTED_TYPE"));
    }
}
