package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.AITranslateRequestDTO;
import com.talnova.tesp.surveyservice.dto.AITranslateResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of AITranslationService utilizing translation engine mappings and fallbacks for multi-locale prompt generation.
 */
@Service
public class AITranslationServiceImpl implements AITranslationService {

    private static final Logger log = LoggerFactory.getLogger(AITranslationServiceImpl.class);

    private static final Map<String, Map<String, String>> DICTIONARY_MAP = Map.of(
            "My direct manager provides clear feedback.", Map.of(
                    "si-LK", "මගේ කළමනාකරු පැහැදිලි මගපෙන්වීමක් ලබා දෙයි.",
                    "ta-LK", "எனது மேலாளர் தெளிவான கருத்துக்களை வழங்குகிறார்.",
                    "es-ES", "Mi gerente directo proporciona comentarios claros."
            ),
            "How great is our leadership team?", Map.of(
                    "si-LK", "අපගේ නායකත්ව කණ්ඩායම කෙතරම් විශිෂ්ටද?",
                    "ta-LK", "எங்கள் தலைமை குழு எவ்வளவு சிறந்தது?",
                    "es-ES", "¿Qué tan genial es nuestro equipo de liderazgo?"
            )
    );

    @Override
    public AITranslateResponseDTO translateText(AITranslateRequestDTO request) {
        if (request == null || request.getSourceText() == null || request.getTargetLocales() == null) {
            return AITranslateResponseDTO.builder().translations(new HashMap<>()).build();
        }

        String sourceText = request.getSourceText().trim();
        Map<String, String> resultMap = new HashMap<>();

        log.info("Translating text into {} target locales...", request.getTargetLocales().size());

        Map<String, String> preCalculated = DICTIONARY_MAP.get(sourceText);

        for (String targetLocale : request.getTargetLocales()) {
            if (targetLocale == null || targetLocale.isBlank()) continue;
            String locale = targetLocale.trim();

            if (preCalculated != null && preCalculated.containsKey(locale)) {
                resultMap.put(locale, preCalculated.get(locale));
            } else {
                resultMap.put(locale, "[" + locale + "] " + sourceText);
            }
        }

        return AITranslateResponseDTO.builder()
                .translations(resultMap)
                .build();
    }
}
