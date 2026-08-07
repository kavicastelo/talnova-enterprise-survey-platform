package com.talnova.tesp.aiservice.sentiment;

import com.talnova.tesp.aiservice.dto.SentimentResultDTO;

public interface SentimentClassificationService {

    /**
     * Classifies raw text sentiment polarity (-1.0 to +1.0) and assigns label (POSITIVE, NEUTRAL, NEGATIVE)
     * with confidence score (0.0 to 1.0) per FR-AI-002, VR-AI-002, and VR-AI-003 after PII sanitization.
     */
    SentimentResultDTO analyzeSentiment(String rawText);
}
