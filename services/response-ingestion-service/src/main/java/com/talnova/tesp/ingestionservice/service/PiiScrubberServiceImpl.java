package com.talnova.tesp.ingestionservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PiiScrubberServiceImpl implements PiiScrubberService {

    private static final Logger log = LoggerFactory.getLogger(PiiScrubberServiceImpl.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?:\\+?\\d{1,3}[- .]?)?\\(?\\d{2,4}\\)?[- .]?\\d{3,4}[- .]?\\d{3,4}");

    @Override
    public String sanitizeOpenText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return rawText;
        }

        String scrubbed = EMAIL_PATTERN.matcher(rawText).replaceAll("[REDACTED_EMAIL]");
        scrubbed = PHONE_PATTERN.matcher(scrubbed).replaceAll("[REDACTED_PHONE]");

        if (!scrubbed.equals(rawText)) {
            log.info("Sanitized in-flight PII text answer string per security specification");
        }

        return scrubbed;
    }
}
