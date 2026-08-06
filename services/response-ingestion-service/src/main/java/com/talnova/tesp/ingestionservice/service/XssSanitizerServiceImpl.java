package com.talnova.tesp.ingestionservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class XssSanitizerServiceImpl implements XssSanitizerService {

    private static final Logger log = LoggerFactory.getLogger(XssSanitizerServiceImpl.class);

    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("(?i)<script.*?>.*?</script>", Pattern.DOTALL);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern JAVASCRIPT_SCHEME_PATTERN = Pattern.compile("(?i)javascript:");

    @Override
    public String sanitizeText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return rawText;
        }

        String sanitized = SCRIPT_TAG_PATTERN.matcher(rawText).replaceAll("");
        sanitized = HTML_TAG_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = JAVASCRIPT_SCHEME_PATTERN.matcher(sanitized).replaceAll("");

        if (!sanitized.equals(rawText)) {
            log.info("Sanitized XSS tags/vectors from user text input");
        }

        return sanitized;
    }
}
