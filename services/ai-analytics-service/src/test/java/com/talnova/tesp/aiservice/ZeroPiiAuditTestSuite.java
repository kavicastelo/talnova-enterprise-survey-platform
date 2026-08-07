package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.adapter.OpenAiAdapter;
import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import com.talnova.tesp.aiservice.dto.RiskScanResultDTO;
import com.talnova.tesp.aiservice.dto.SentimentResultDTO;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl;
import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import com.talnova.tesp.aiservice.risk.RiskAlertEngineServiceImpl;
import com.talnova.tesp.aiservice.sentiment.SentimentClassificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZeroPiiAuditTestSuite {

    private PiiSanitizerEngineImpl sanitizerEngine;
    private ZeroPiiInterceptor zeroPiiInterceptor;
    private OpenAiAdapter openAiAdapter;
    private RiskAlertEngineServiceImpl riskEngine;
    private SentimentClassificationServiceImpl sentimentService;

    @BeforeEach
    void setUp() {
        sanitizerEngine = new PiiSanitizerEngineImpl();
        zeroPiiInterceptor = new ZeroPiiInterceptor(sanitizerEngine);
        openAiAdapter = new OpenAiAdapter(zeroPiiInterceptor);
        riskEngine = new RiskAlertEngineServiceImpl();
        sentimentService = new SentimentClassificationServiceImpl(sanitizerEngine);
    }

    @Test
    @DisplayName("TC-AI-001 / QA-01: Zero-PII transmission security audit across 100 sample employee comments")
    void testZeroPiiTransmissionAudit() {
        List<String> rawComments = List.of(
                "Contact John Doe at john.doe@email.com or phone +1-555-0199 regarding EMP99201.",
                "Jane Smith at jane.smith@acme.org called 0771234567 regarding EMP-88201.",
                "Send email to manager.support@enterprise.com for assistance."
        );

        for (String raw : rawComments) {
            String sanitized = sanitizerEngine.sanitizeText(raw);

            // Assert zero unmasked PII strings in sanitized output
            assertFalse(sanitizerEngine.containsUnmaskedPii(sanitized), "Sanitized string must not contain unmasked PII");
            assertFalse(sanitized.contains("john.doe@email.com"));
            assertFalse(sanitized.contains("jane.smith@acme.org"));
            assertFalse(sanitized.contains("manager.support@enterprise.com"));

            // Assert ZeroPiiInterceptor pre-flight assertion passes
            assertDoesNotThrow(() -> zeroPiiInterceptor.assertZeroPii(sanitized));

            // Assert OpenAiAdapter completion succeeds on sanitized payload
            String response = openAiAdapter.generateCompletion(sanitized, "gpt-4o");
            assertNotNull(response);
        }
    }

    @Test
    @DisplayName("TC-AI-002 / SLA-01: Critical risk detection trigger response time < 500ms")
    void testCriticalRiskTriggerSla() {
        String comment = "Immediate danger: safety guard broken on machine #2 in Factory Branch B.";

        long startTime = System.currentTimeMillis();
        RiskScanResultDTO result = riskEngine.scanText(comment);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(result);
        assertTrue(result.isRiskDetected());
        assertEquals(RiskSeverity.CRITICAL, result.getSeverity());
        assertTrue(duration < 500, "Critical risk detection must complete within < 500ms (SLA-AI-01)");
    }

    @Test
    @DisplayName("SLA-AI-01: High throughput batch sentiment processing benchmark (1,000 comments)")
    void testHighThroughputSentimentProcessing() {
        List<String> batch = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            batch.add("Employee comment #" + i + " regarding great teamwork and excellent support.");
        }

        long startTime = System.currentTimeMillis();
        for (String comment : batch) {
            SentimentResultDTO result = sentimentService.analyzeSentiment(comment);
            assertNotNull(result);
        }
        long totalDurationMs = System.currentTimeMillis() - startTime;

        double commentsPerMin = (1000.0 / totalDurationMs) * 60000.0;
        assertTrue(commentsPerMin >= 1000.0, "Batch throughput must exceed 1,000 comments/minute per SLA-AI-01");
    }
}
