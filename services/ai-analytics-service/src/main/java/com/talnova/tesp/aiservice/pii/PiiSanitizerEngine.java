package com.talnova.tesp.aiservice.pii;

public interface PiiSanitizerEngine {

    /**
     * Pass raw text comment through PII regex & entity scrubber, replacing emails ([MASKED_EMAIL]),
     * phone numbers ([MASKED_PHONE]), employee IDs ([MASKED_EMP_ID]), and names ([MASKED_NAME]).
     *
     * @param rawText Input raw text comment from employee
     * @return Sanitized text string with zero unmasked PII per BR-AI-001 & FR-AI-001.
     */
    String sanitizeText(String rawText);

    /**
     * Returns true if input text contains unmasked email, phone number, or employee ID PII patterns.
     */
    boolean containsUnmaskedPii(String text);
}
