package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import com.talnova.tesp.aiservice.dto.RiskScanResultDTO;
import com.talnova.tesp.aiservice.risk.RiskAlertEngineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskAlertEngineTest {

    private RiskAlertEngineServiceImpl riskEngine;

    @BeforeEach
    void setUp() {
        riskEngine = new RiskAlertEngineServiceImpl();
    }

    @Test
    @DisplayName("TC-AI-002 / TC-AI-401-01: Detect critical workplace safety risk flag")
    void testDetectCriticalSafetyRisk() {
        String comment = "The safety guard broken on machine #3 in Factory Branch B.";

        RiskScanResultDTO result = riskEngine.scanText(comment);

        assertNotNull(result);
        assertTrue(result.isRiskDetected());
        assertEquals("SAFETY", result.getCategory());
        assertEquals(RiskSeverity.CRITICAL, result.getSeverity());
        assertEquals("safety guard broken", result.getDetectedKeyword());
    }

    @Test
    @DisplayName("TC-AI-401-02: Detect high harassment risk flag")
    void testDetectHarassmentRisk() {
        String comment = "Experienced repeated discrimination and hostile environment in the department.";

        RiskScanResultDTO result = riskEngine.scanText(comment);

        assertNotNull(result);
        assertTrue(result.isRiskDetected());
        assertEquals("HARASSMENT", result.getCategory());
        assertEquals(RiskSeverity.HIGH, result.getSeverity());
    }

    @Test
    @DisplayName("TC-AI-401-03: No risk detected for safe comment")
    void testSafeCommentNoRisk() {
        String comment = "Great teamwork and excellent project execution.";

        RiskScanResultDTO result = riskEngine.scanText(comment);

        assertNotNull(result);
        assertFalse(result.isRiskDetected());
        assertEquals(RiskSeverity.LOW, result.getSeverity());
    }
}
