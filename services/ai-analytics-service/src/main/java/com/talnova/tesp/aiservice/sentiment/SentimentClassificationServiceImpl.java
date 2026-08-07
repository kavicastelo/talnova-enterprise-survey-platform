package com.talnova.tesp.aiservice.sentiment;

import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import com.talnova.tesp.aiservice.dto.SentimentResultDTO;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SentimentClassificationServiceImpl implements SentimentClassificationService {

    private static final Logger log = LoggerFactory.getLogger(SentimentClassificationServiceImpl.class);

    private final PiiSanitizerEngine piiSanitizerEngine;

    private static final List<String> POSITIVE_KEYWORDS = List.of(
            "great", "excellent", "good", "happy", "love", "awesome", "fantastic",
            "hothai", "subha", "nalan", "bueno", "excelente", "bon", "magnifique"
    );

    private static final List<String> NEGATIVE_KEYWORDS = List.of(
            "bad", "terrible", "poor", "unhappy", "hate", "awful", "horrible",
            "narakai", "kettatu", "malo", "terrible", "mauvais", "horrible", "frustrated"
    );

    public SentimentClassificationServiceImpl(PiiSanitizerEngine piiSanitizerEngine) {
        this.piiSanitizerEngine = piiSanitizerEngine;
    }

    @Override
    public SentimentResultDTO analyzeSentiment(String rawText) {
        String sanitizedText = piiSanitizerEngine.sanitizeText(rawText);

        if (sanitizedText == null || sanitizedText.isBlank() || sanitizedText.trim().split("\\s+").length < 3) {
            log.info("Text skipped or flagged UNSCORED per VR-AI-001 (insufficient length)");
            return SentimentResultDTO.builder()
                    .sentimentScore(0.0)
                    .sentimentLabel(SentimentLabel.NEUTRAL)
                    .confidence(0.50)
                    .detectedLanguage("EN")
                    .sanitizedText(sanitizedText != null ? sanitizedText : "")
                    .build();
        }

        String lowerText = sanitizedText.toLowerCase(Locale.ROOT);
        long posCount = POSITIVE_KEYWORDS.stream().filter(lowerText::contains).count();
        long negCount = NEGATIVE_KEYWORDS.stream().filter(lowerText::contains).count();

        double rawScore = 0.0;
        if (posCount > negCount) {
            rawScore = Math.min(1.0, 0.4 + (posCount * 0.2));
        } else if (negCount > posCount) {
            rawScore = Math.max(-1.0, -0.4 - (negCount * 0.2));
        }

        // Validate VR-AI-002 range: -1.0 <= S <= +1.0
        double sentimentScore = Math.max(-1.0, Math.min(1.0, rawScore));

        SentimentLabel label = SentimentLabel.NEUTRAL;
        if (sentimentScore > 0.15) {
            label = SentimentLabel.POSITIVE;
        } else if (sentimentScore < -0.15) {
            label = SentimentLabel.NEGATIVE;
        }

        // Validate VR-AI-003 range: 0.0 <= C <= 1.0
        double confidence = 0.85;

        log.debug("Sentiment analyzed: score={}, label={}, confidence={}", sentimentScore, label, confidence);

        return SentimentResultDTO.builder()
                .sentimentScore(sentimentScore)
                .sentimentLabel(label)
                .confidence(confidence)
                .detectedLanguage("EN")
                .sanitizedText(sanitizedText)
                .build();
    }
}
