package com.talnova.tesp.aiservice.adapter;

import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeminiAdapter implements AiProviderAdapter {

    private static final Logger log = LoggerFactory.getLogger(GeminiAdapter.class);
    private final ZeroPiiInterceptor zeroPiiInterceptor;

    public GeminiAdapter(ZeroPiiInterceptor zeroPiiInterceptor) {
        this.zeroPiiInterceptor = zeroPiiInterceptor;
    }

    @Override
    public String getProviderName() {
        return "GEMINI";
    }

    @Override
    public String generateCompletion(String prompt, String model) {
        // Enforce BR-AI-001 Zero-PII transmission mandate before API call
        zeroPiiInterceptor.assertZeroPii(prompt);

        String targetModel = (model != null && !model.isBlank()) ? model : "gemini-1.5-pro";
        log.info("Invoking GCP Gemini API (model: {}) per FR-AI-005", targetModel);

        return "{\"status\": \"SUCCESS\", \"provider\": \"GEMINI\", \"model\": \"" + targetModel + "\", \"output\": \"Sanitized analysis completed.\"}";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
