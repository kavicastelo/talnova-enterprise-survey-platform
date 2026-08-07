package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.adapter.*;
import com.talnova.tesp.aiservice.exception.PiiSecurityException;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl;
import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AiAdapterTest {

    private OpenAiAdapter openAiAdapter;
    private GeminiAdapter geminiAdapter;
    private LocalVllmAdapter localVllmAdapter;
    private AiAdapterFactoryServiceImpl factoryService;

    @BeforeEach
    void setUp() {
        ZeroPiiInterceptor zeroPiiInterceptor = new ZeroPiiInterceptor(new PiiSanitizerEngineImpl());
        openAiAdapter = new OpenAiAdapter(zeroPiiInterceptor);
        geminiAdapter = new GeminiAdapter(zeroPiiInterceptor);
        localVllmAdapter = new LocalVllmAdapter(zeroPiiInterceptor);
        factoryService = new AiAdapterFactoryServiceImpl(List.of(openAiAdapter, geminiAdapter, localVllmAdapter));
    }

    @Test
    @DisplayName("TC-AI-501-01: OpenAI GPT-4o Adapter completion with Zero-PII assertion")
    void testOpenAiAdapterCompletion() {
        String sanitizedPrompt = "Summarize feedback: [MASKED_NAME] reports [MASKED_EMAIL] is active.";

        String response = openAiAdapter.generateCompletion(sanitizedPrompt, "gpt-4o");

        assertNotNull(response);
        assertTrue(response.contains("OPENAI"));
        assertTrue(response.contains("gpt-4o"));
    }

    @Test
    @DisplayName("TC-AI-501-02: Gemini 1.5 Pro Adapter completion")
    void testGeminiAdapterCompletion() {
        String sanitizedPrompt = "Summarize feedback: great team environment.";

        String response = geminiAdapter.generateCompletion(sanitizedPrompt, "gemini-1.5-pro");

        assertNotNull(response);
        assertTrue(response.contains("GEMINI"));
        assertTrue(response.contains("gemini-1.5-pro"));
    }

    @Test
    @DisplayName("TC-AI-502-01: Local vLLM Llama 3 Adapter completion")
    void testLocalVllmAdapterCompletion() {
        String sanitizedPrompt = "Summarize feedback: excellent factory floor support.";

        String response = localVllmAdapter.generateCompletion(sanitizedPrompt, "llama-3-8b-instruct");

        assertNotNull(response);
        assertTrue(response.contains("VLLM_LOCAL"));
        assertTrue(response.contains("llama-3-8b-instruct"));
    }

    @Test
    @DisplayName("TC-AI-501-03 / TC-AI-502-02: Factory resolves all adapter instances")
    void testAdapterFactoryResolution() {
        AiProviderAdapter openAi = factoryService.getAdapter("OPENAI");
        assertEquals("OPENAI", openAi.getProviderName());

        AiProviderAdapter gemini = factoryService.getAdapter("GEMINI");
        assertEquals("GEMINI", gemini.getProviderName());

        AiProviderAdapter vllm = factoryService.getAdapter("VLLM_LOCAL");
        assertEquals("VLLM_LOCAL", vllm.getProviderName());
    }

    @Test
    @DisplayName("TC-AI-501-04 / TC-AI-502-03: All adapters throw PiiSecurityException if prompt contains unmasked email")
    void testUnmaskedPiiBlockedByAdapters() {
        String unsafePrompt = "Summarize feedback for john.doe@email.com";

        assertThrows(PiiSecurityException.class, () -> openAiAdapter.generateCompletion(unsafePrompt, "gpt-4o"));
        assertThrows(PiiSecurityException.class, () -> geminiAdapter.generateCompletion(unsafePrompt, "gemini-1.5-pro"));
        assertThrows(PiiSecurityException.class, () -> localVllmAdapter.generateCompletion(unsafePrompt, "llama-3-8b-instruct"));
    }
}
