package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;

import java.util.Map;

/**
 * Service component for resolving multi-language localized text dictionaries and enforcing default locale prompts.
 */
public interface LocaleDictionaryResolver {

    /**
     * Validates that all question prompts in survey draft contain an entry for the default locale.
     *
     * @param dto           Survey draft payload
     * @param defaultLocale Project default locale tag (e.g. "en-US")
     */
    void validateSurveyLocales(SurveySaveDraftDTO dto, String defaultLocale);

    /**
     * Resolves a localized text string from a prompt map for a target locale with fallback to default locale.
     *
     * @param localizedMap  Map of BCP-47 locale tags to text strings
     * @param targetLocale  Requested target locale (e.g. "si-LK")
     * @param defaultLocale Fallback default locale (e.g. "en-US")
     * @return Resolved string
     */
    String resolveText(Map<String, String> localizedMap, String targetLocale, String defaultLocale);
}
