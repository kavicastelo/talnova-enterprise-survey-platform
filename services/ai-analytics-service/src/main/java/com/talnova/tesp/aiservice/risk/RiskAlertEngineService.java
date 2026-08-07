package com.talnova.tesp.aiservice.risk;

import com.talnova.tesp.aiservice.dto.RiskScanResultDTO;

public interface RiskAlertEngineService {

    /**
     * Scans sanitized text comments for workplace safety breaches, harassment, burnout, or compliance keywords per FR-AI-004.
     */
    RiskScanResultDTO scanText(String sanitizedText);
}
