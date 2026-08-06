package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisRequestDTO;
import com.talnova.tesp.surveyservice.dto.AIBiasAnalysisResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of AIBiasInspectorService utilizing pattern heuristics to flag leading questions, double-barreled prompts, and loaded terminology.
 */
@Service
public class AIBiasInspectorServiceImpl implements AIBiasInspectorService {

    private static final Logger log = LoggerFactory.getLogger(AIBiasInspectorServiceImpl.class);

    private static final List<String> LEADING_WORDS = List.of(
            "great", "amazing", "wonderful", "excellent", "terrible", "awful", "obviously", "unquestionably", "fantastic"
    );

    private static final List<String> DOUBLE_BARRELED_INDICATORS = List.of(
            " and ", " as well as ", " alongside "
    );

    @Override
    public AIBiasAnalysisResponseDTO analyzeQuestionBias(AIBiasAnalysisRequestDTO request) {
        if (request == null || request.getPromptText() == null || request.getPromptText().isBlank()) {
            return AIBiasAnalysisResponseDTO.builder()
                    .isBiased(false)
                    .biasType("NEUTRAL")
                    .score(0.0)
                    .suggestion("No prompt provided")
                    .explanation("Empty prompt submitted for bias analysis.")
                    .build();
        }

        String prompt = request.getPromptText().trim();
        String lowerPrompt = prompt.toLowerCase(Locale.ENGLISH);

        log.info("Analyzing AI bias heuristics for prompt: '{}'", prompt);

        // Check 1: Leading Question
        for (String leadingWord : LEADING_WORDS) {
            if (lowerPrompt.contains(leadingWord)) {
                String suggestion = rephraseLeadingQuestion(prompt, leadingWord);
                return AIBiasAnalysisResponseDTO.builder()
                        .isBiased(true)
                        .biasType("LEADING_QUESTION")
                        .score(0.85)
                        .suggestion(suggestion)
                        .explanation("The question contains emotionally suggestive adjective '" + leadingWord + "', which nudges respondents toward a positive or negative response.")
                        .build();
            }
        }

        // Check 2: Double-Barreled Question
        for (String indicator : DOUBLE_BARRELED_INDICATORS) {
            if (lowerPrompt.contains(indicator)) {
                return AIBiasAnalysisResponseDTO.builder()
                        .isBiased(true)
                        .biasType("DOUBLE_BARRELED")
                        .score(0.75)
                        .suggestion("Split into two separate questions to measure each concept independently.")
                        .explanation("The question combines multiple topics using '" + indicator.trim() + "', confusing respondents who feel differently about each concept.")
                        .build();
            }
        }

        return AIBiasAnalysisResponseDTO.builder()
                .isBiased(false)
                .biasType("NEUTRAL")
                .score(0.0)
                .suggestion("No bias detected.")
                .explanation("The question prompt uses neutral framing and balanced structure.")
                .build();
    }

    private String rephraseLeadingQuestion(String originalPrompt, String biasedWord) {
        if (biasedWord.equalsIgnoreCase("great") || biasedWord.equalsIgnoreCase("amazing") || biasedWord.equalsIgnoreCase("excellent")) {
            return "Rephrase to: 'How would you rate the effectiveness of our leadership team?'";
        }
        return "Rephrase using a neutral rating scale (e.g. 'How satisfied or dissatisfied are you with...')";
    }
}
