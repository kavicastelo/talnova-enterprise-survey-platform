package com.talnova.tesp.aiservice.adapter;

public interface AiAdapterFactoryService {

    /**
     * Resolves the appropriate AiProviderAdapter instance by name ("OPENAI", "GEMINI", "VLLM_LOCAL").
     */
    AiProviderAdapter getAdapter(String providerName);
}
