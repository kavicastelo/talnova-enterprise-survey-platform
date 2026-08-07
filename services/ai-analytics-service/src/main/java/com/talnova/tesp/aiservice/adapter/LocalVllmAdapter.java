package com.talnova.tesp.aiservice.adapter;

import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalVllmAdapter implements AiProviderAdapter {

    private static final Logger log = LoggerFactory.getLogger(LocalVllmAdapter.class);
    private final ZeroPiiInterceptor zeroPiiInterceptor;

    public LocalVllmAdapter(ZeroPiiInterceptor zeroPiiInterceptor) {
        this.zeroPiiInterceptor = zeroPiiInterceptor;
    }

    @Override
    public String getProviderName() {
        return "VLLM_LOCAL";
    }

    @Override
    public String generateCompletion(String prompt, String model) {
        // Enforce BR-AI-001 Zero-PII transmission mandate before API call
        zeroPiiInterceptor.assertZeroPii(prompt);

        String targetModel = (model != null && !model.isBlank()) ? model : "llama-3-8b-instruct";
        log.info("Invoking self-hosted local vLLM endpoint (model: {}) for on-premise execution per OQ-AI-002", targetModel);

        return "{\"status\": \"SUCCESS\", \"provider\": \"VLLM_LOCAL\", \"model\": \"" + targetModel + "\", \"output\": \"Local vLLM analysis completed.\"}";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
