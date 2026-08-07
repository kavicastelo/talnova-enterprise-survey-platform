package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.domain.model.NodeAggregate;
import com.talnova.tesp.analyticsservice.domain.model.NodeAggregateStatus;
import com.talnova.tesp.analyticsservice.privacy.PrivacyGuardServiceImpl;
import com.talnova.tesp.analyticsservice.scoring.EngagementIndexCalculatorServiceImpl;
import com.talnova.tesp.analyticsservice.scoring.EnpsCalculatorServiceImpl;
import com.talnova.tesp.analyticsservice.scoring.EnpsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsEngineLoadTest {

    private EnpsCalculatorServiceImpl enpsCalculator;
    private EngagementIndexCalculatorServiceImpl engagementCalculator;
    private PrivacyGuardServiceImpl privacyGuard;

    @BeforeEach
    void setUp() {
        enpsCalculator = new EnpsCalculatorServiceImpl();
        engagementCalculator = new EngagementIndexCalculatorServiceImpl();
        privacyGuard = new PrivacyGuardServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-001 / TC-ANL-901-01: eNPS Score Accuracy Verification (50 Promoters, 30 Passives, 20 Detractors -> +30.0)")
    void testEnpsScoreAccuracy() {
        List<Double> scores = new ArrayList<>();
        for (int i = 0; i < 50; i++) scores.add(10.0); // Promoters
        for (int i = 0; i < 30; i++) scores.add(8.0);  // Passives
        for (int i = 0; i < 20; i++) scores.add(5.0);  // Detractors

        EnpsResult result = enpsCalculator.calculateEnps(scores);

        assertNotNull(result);
        assertEquals(100, result.getTotalResponses());
        assertEquals(50, result.getPromoterCount());
        assertEquals(30, result.getPassiveCount());
        assertEquals(20, result.getDetractorCount());
        assertEquals(30.0, result.getEnpsScore(), "eNPS score must equal exactly +30.0");
    }

    @Test
    @DisplayName("TC-ANL-002 / TC-ANL-901-02: PrivacyGuard Anonymity Suppression Verification (N = 4 < 5)")
    void testPrivacyGuardSuppression() {
        NodeAggregate rawNode = NodeAggregate.builder()
                .nodeId("N-SECRET")
                .responseCount(4)
                .status(NodeAggregateStatus.VALID)
                .enps(45.0)
                .engagementIndex(78.0)
                .build();

        NodeAggregate sanitized = privacyGuard.sanitizeNodeAggregate(rawNode);

        assertNotNull(sanitized);
        assertEquals(NodeAggregateStatus.SUPPRESSED, sanitized.getStatus());
        assertNull(sanitized.getEnps(), "eNPS score must be null for N < 5");
        assertNull(sanitized.getEngagementIndex(), "Engagement index must be null for N < 5");
    }

    @Test
    @DisplayName("TC-ANL-901-03: 100k Response Aggregation Performance Benchmark (< 250ms latency threshold)")
    void test100kResponseAggregationPerformance() {
        int responseCount = 100_000;
        List<Double> largeScoreDataset = new ArrayList<>(responseCount);

        for (int i = 0; i < responseCount; i++) {
            largeScoreDataset.add((double) (i % 11)); // Scores 0 to 10
        }

        long startTime = System.currentTimeMillis();
        EnpsResult enpsResult = enpsCalculator.calculateEnps(largeScoreDataset);
        double engagementIndex = engagementCalculator.calculateMeanEngagementIndex(largeScoreDataset);
        long durationMs = System.currentTimeMillis() - startTime;

        assertNotNull(enpsResult);
        assertTrue(engagementIndex > 0.0);
        assertTrue(durationMs < 250, "100k response aggregation latency (" + durationMs + "ms) must be under 250ms threshold");
    }
}
