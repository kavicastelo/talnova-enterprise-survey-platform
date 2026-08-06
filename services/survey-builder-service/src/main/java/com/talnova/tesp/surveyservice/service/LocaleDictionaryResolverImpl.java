package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of LocaleDictionaryResolver for verifying default locale prompts and resolving translations.
 */
@Component
public class LocaleDictionaryResolverImpl implements LocaleDictionaryResolver {

    private static final String DEFAULT_FALLBACK_LOCALE = "en-US";

    @Override
    public void validateSurveyLocales(SurveySaveDraftDTO dto, String defaultLocale) {
        String requiredLocale = (defaultLocale != null && !defaultLocale.isBlank())
                ? defaultLocale.trim()
                : DEFAULT_FALLBACK_LOCALE;

        if (dto.getPages() == null) return;

        for (SurveyPageDTO page : dto.getPages()) {
            if (page.getSections() == null) continue;
            for (SurveySectionDTO section : page.getSections()) {
                if (section.getQuestions() == null) continue;
                for (SurveyQuestionDTO question : section.getQuestions()) {
                    Map<String, String> promptMap = question.getPrompt();
                    if (promptMap == null || !promptMap.containsKey(requiredLocale)
                            || promptMap.get(requiredLocale) == null
                            || promptMap.get(requiredLocale).isBlank()) {
                        throw new SurveyValidationException(
                                "Missing default locale prompt: prompt for question '" + question.getQuestionId()
                                        + "' must contain entry for default locale '" + requiredLocale + "'"
                        );
                    }
                }
            }
        }
    }

    @Override
    public String resolveText(Map<String, String> localizedMap, String targetLocale, String defaultLocale) {
        if (localizedMap == null || localizedMap.isEmpty()) {
            return "";
        }

        if (targetLocale != null && localizedMap.containsKey(targetLocale)) {
            String targetVal = localizedMap.get(targetLocale);
            if (targetVal != null && !targetVal.isBlank()) {
                return targetVal.trim();
            }
        }

        String effectiveDefault = (defaultLocale != null && !defaultLocale.isBlank())
                ? defaultLocale.trim()
                : DEFAULT_FALLBACK_LOCALE;

        if (localizedMap.containsKey(effectiveDefault)) {
            String defaultVal = localizedMap.get(effectiveDefault);
            if (defaultVal != null && !defaultVal.isBlank()) {
                return defaultVal.trim();
            }
        }

        return localizedMap.values().stream()
                .filter(val -> val != null && !val.isBlank())
                .findFirst()
                .orElse("")
                .trim();
    }
}
