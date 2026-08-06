package com.talnova.tesp.ingestionservice.service;

public interface XssSanitizerService {

    /**
     * Sanitizes open-ended text answers by stripping HTML script tags, dangerous entities, and XSS vectors.
     *
     * @param rawText Unsanitized input text string
     * @return Sanitized XSS-safe text string.
     */
    String sanitizeText(String rawText);
}
