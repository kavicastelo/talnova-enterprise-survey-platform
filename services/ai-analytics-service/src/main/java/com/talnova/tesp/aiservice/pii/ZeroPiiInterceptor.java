package com.talnova.tesp.aiservice.pii;

import com.talnova.tesp.aiservice.exception.PiiSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZeroPiiInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ZeroPiiInterceptor.class);

    private final PiiSanitizerEngine piiSanitizerEngine;

    public ZeroPiiInterceptor(PiiSanitizerEngine piiSanitizerEngine) {
        this.piiSanitizerEngine = piiSanitizerEngine;
    }

    /**
     * Asserts that outgoing text payload contains zero unmasked PII before invoking third-party cloud LLM APIs.
     * Throws PiiSecurityException if unmasked email or phone patterns are detected.
     *
     * @param payload Text payload intended for cloud LLM API
     */
    public void assertZeroPii(String payload) {
        if (payload == null || payload.isBlank()) {
            return;
        }

        if (piiSanitizerEngine.containsUnmaskedPii(payload)) {
            log.error("CRITICAL PRIVACY BREACH ATTEMPT BLOCKED: Outbound LLM payload contains unmasked PII per BR-AI-001");
            throw new PiiSecurityException("Outbound LLM prompt payload violates Zero-PII transmission mandate (BR-AI-001)");
        }

        log.info("Zero-PII Interceptor pre-flight audit passed. Outbound payload safe for LLM transmission.");
    }
}
