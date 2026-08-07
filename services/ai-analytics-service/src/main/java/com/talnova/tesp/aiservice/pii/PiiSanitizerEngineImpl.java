package com.talnova.tesp.aiservice.pii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PiiSanitizerEngineImpl implements PiiSanitizerEngine {

    private static final Logger log = LoggerFactory.getLogger(PiiSanitizerEngineImpl.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+?\\d{1,3}[- .]?)?\\(?\\d{3,4}\\)?[- .]?\\d{3,4}[- .]?\\d{3,4}|(?:\\+?\\d{1,3}[- .]?)?\\d{3,4}[- .]?\\d{3,4}|\\b0\\d{9}\\b"
    );

    private static final Pattern EMP_ID_PATTERN = Pattern.compile(
            "\\b(?:EMP|EMP_|ID-)\\s*\\d{3,6}\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern FULL_NAME_PATTERN = Pattern.compile(
            "\\b[A-Z][a-z]+\\s+[A-Z][a-z]+\\b"
    );

    @Override
    public String sanitizeText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return "";
        }

        String sanitized = rawText;

        // 1. Mask Emails
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("[MASKED_EMAIL]");

        // 2. Mask Phone Numbers
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll("[MASKED_PHONE]");

        // 3. Mask Employee IDs
        sanitized = EMP_ID_PATTERN.matcher(sanitized).replaceAll("[MASKED_EMP_ID]");

        // 4. Mask Proper Names
        sanitized = FULL_NAME_PATTERN.matcher(sanitized).replaceAll("[MASKED_NAME]");

        log.debug("Sanitized raw text comment for zero-PII compliance (BR-AI-001)");
        return sanitized;
    }

    @Override
    public boolean containsUnmaskedPii(String text) {
        if (text == null || text.isBlank()) return false;
        return EMAIL_PATTERN.matcher(text).find() || PHONE_PATTERN.matcher(text).find();
    }
}
