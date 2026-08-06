package com.talnova.tesp.ingestionservice.service;

public interface PiiScrubberService {

    /**
     * In-flight open-text sanitizer replacing PII email addresses ([REDACTED_EMAIL]) and phone numbers ([REDACTED_PHONE]).
     *
     * @param rawText Unsanitized user text answer string
     * @return PII-sanitized text answer string.
     */
    String sanitizeOpenText(String rawText);
}
