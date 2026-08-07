package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.domain.model.AiInsightDocument;
import com.talnova.tesp.aiservice.domain.model.HumanOverride;
import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import com.talnova.tesp.aiservice.domain.model.SentimentLabel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AiInsightRepositoryTest {

    @Test
    @DisplayName("TC-AI-101-01: Build and verify AiInsightDocument model fields and enums")
    void testAiInsightDocumentBuilder() {
        Instant now = Instant.now();

        HumanOverride override = HumanOverride.builder()
                .overriddenBy("USR-HR-001")
                .originalLabel("NEGATIVE")
                .newLabel("NEUTRAL")
                .reason("Context adjustment")
                .overriddenAt(now)
                .build();

        AiInsightDocument doc = AiInsightDocument.builder()
                .id("INSIGHT-001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .responseId("RESP-8820")
                .questionId("Q-104")
                .sanitizedText("Workload is manageable but deadlines are tight.")
                .sentimentScore(0.25)
                .sentimentLabel(SentimentLabel.NEUTRAL)
                .confidence(0.92)
                .themes(List.of("Workload", "Deadlines"))
                .riskCategory("BURNOUT")
                .riskSeverity(RiskSeverity.LOW)
                .humanOverride(override)
                .createdAt(now)
                .build();

        assertNotNull(doc);
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals(SentimentLabel.NEUTRAL, doc.getSentimentLabel());
        assertEquals(RiskSeverity.LOW, doc.getRiskSeverity());
        assertNotNull(doc.getHumanOverride());
        assertEquals("USR-HR-001", doc.getHumanOverride().getOverriddenBy());
    }
}
