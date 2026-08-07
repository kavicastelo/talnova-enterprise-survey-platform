package com.talnova.tesp.aiservice.risk;

import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import com.talnova.tesp.aiservice.dto.RiskScanResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RiskAlertEngineServiceImpl implements RiskAlertEngineService {

    private static final Logger log = LoggerFactory.getLogger(RiskAlertEngineServiceImpl.class);

    private static final Map<String, List<String>> CRITICAL_KEYWORDS = Map.of(
            "SAFETY", List.of("safety guard broken", "fire hazard", "toxic fumes", "emergency exit blocked", "machinery defect"),
            "HARASSMENT", List.of("sexual harassment", "physical threat", "racial slur", "physical abuse")
    );

    private static final Map<String, List<String>> HIGH_KEYWORDS = Map.of(
            "HARASSMENT", List.of("harassment", "discrimination", "hostile environment", "bullying"),
            "COMPLIANCE", List.of("fraud", "bribe", "illegal action", "compliance breach")
    );

    private static final Map<String, List<String>> MEDIUM_KEYWORDS = Map.of(
            "BURNOUT", List.of("extreme stress", "exhausted", "resigning", "unbearable workload")
    );

    @Override
    public RiskScanResultDTO scanText(String sanitizedText) {
        if (sanitizedText == null || sanitizedText.isBlank()) {
            return RiskScanResultDTO.builder()
                    .riskDetected(false)
                    .severity(RiskSeverity.LOW)
                    .build();
        }

        String lowerText = sanitizedText.toLowerCase(Locale.ROOT);

        // 1. Check CRITICAL keywords
        for (Map.Entry<String, List<String>> entry : CRITICAL_KEYWORDS.entrySet()) {
            for (String kw : entry.getValue()) {
                if (lowerText.contains(kw)) {
                    log.warn("CRITICAL workplace risk detected: category={}, keyword='{}' per FR-AI-004", entry.getKey(), kw);
                    return RiskScanResultDTO.builder()
                            .riskDetected(true)
                            .category(entry.getKey())
                            .severity(RiskSeverity.CRITICAL)
                            .detectedKeyword(kw)
                            .build();
                }
            }
        }

        // 2. Check HIGH keywords
        for (Map.Entry<String, List<String>> entry : HIGH_KEYWORDS.entrySet()) {
            for (String kw : entry.getValue()) {
                if (lowerText.contains(kw)) {
                    log.warn("HIGH workplace risk detected: category={}, keyword='{}'", entry.getKey(), kw);
                    return RiskScanResultDTO.builder()
                            .riskDetected(true)
                            .category(entry.getKey())
                            .severity(RiskSeverity.HIGH)
                            .detectedKeyword(kw)
                            .build();
                }
            }
        }

        // 3. Check MEDIUM keywords
        for (Map.Entry<String, List<String>> entry : MEDIUM_KEYWORDS.entrySet()) {
            for (String kw : entry.getValue()) {
                if (lowerText.contains(kw)) {
                    log.info("MEDIUM workplace risk detected: category={}, keyword='{}'", entry.getKey(), kw);
                    return RiskScanResultDTO.builder()
                            .riskDetected(true)
                            .category(entry.getKey())
                            .severity(RiskSeverity.MEDIUM)
                            .detectedKeyword(kw)
                            .build();
                }
            }
        }

        return RiskScanResultDTO.builder()
                .riskDetected(false)
                .severity(RiskSeverity.LOW)
                .build();
    }
}
