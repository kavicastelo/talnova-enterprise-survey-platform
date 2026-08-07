package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import com.talnova.tesp.aiservice.dto.SentimentResultDTO;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl;
import com.talnova.tesp.aiservice.sentiment.SentimentClassificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SentimentClassificationServiceTest {

    private SentimentClassificationServiceImpl sentimentService;

    @BeforeEach
    void setUp() {
        sentimentService = new SentimentClassificationServiceImpl(new PiiSanitizerEngineImpl());
    }

    @Test
    @DisplayName("TC-AI-301-01: Analyze positive sentiment comment with PII scrubbing")
    void testAnalyzePositiveSentiment() {
        String rawComment = "Working with John Doe at john.doe@acme.com is a great and excellent experience!";

        SentimentResultDTO result = sentimentService.analyzeSentiment(rawComment);

        assertNotNull(result);
        assertEquals(SentimentLabel.POSITIVE, result.getSentimentLabel());
        assertTrue(result.getSentimentScore() > 0.0, "Score must be positive");
        assertTrue(result.getSentimentScore() <= 1.0, "Score must not exceed 1.0 (VR-AI-002)");
        assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 1.0, "Confidence range (VR-AI-003)");
        assertFalse(result.getSanitizedText().contains("john.doe@acme.com"), "PII scrubbed before scoring");
    }

    @Test
    @DisplayName("TC-AI-301-02: Analyze negative sentiment comment")
    void testAnalyzeNegativeSentiment() {
        String rawComment = "The communication from management is terrible and very poor overall.";

        SentimentResultDTO result = sentimentService.analyzeSentiment(rawComment);

        assertNotNull(result);
        assertEquals(SentimentLabel.NEGATIVE, result.getSentimentLabel());
        assertTrue(result.getSentimentScore() < 0.0, "Score must be negative");
    }

    @Test
    @DisplayName("TC-AI-301-03: Insufficient length returns default NEUTRAL score per VR-AI-001")
    void testShortCommentVR001() {
        SentimentResultDTO result = sentimentService.analyzeSentiment("Good job");

        assertNotNull(result);
        assertEquals(SentimentLabel.NEUTRAL, result.getSentimentLabel());
    }
}
