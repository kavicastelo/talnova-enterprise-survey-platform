package com.talnova.tesp.aiservice.adapter;

public interface AiProviderAdapter {

    /**
     * Unique identifier for the AI provider (e.g. "OPENAI", "GEMINI", "VLLM_LOCAL").
     */
    String getProviderName();

    /**
     * Executes completion call after enforcing Zero-PII pre-flight audit.
     *
     * @param prompt Sanitized prompt payload
     * @param model Model name (e.g. "gpt-4o", "gemini-1.5-pro")
     * @return Completion response string
     */
    String generateCompletion(String prompt, String model);

    boolean isAvailable();
}
