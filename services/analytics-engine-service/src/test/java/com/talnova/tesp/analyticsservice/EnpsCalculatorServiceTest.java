package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.scoring.EnpsCalculatorServiceImpl;
import com.talnova.tesp.analyticsservice.scoring.EnpsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnpsCalculatorServiceTest {

    private EnpsCalculatorServiceImpl enpsCalculator;

    @BeforeEach
    void setUp() {
        enpsCalculator = new EnpsCalculatorServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-001 / TC-ANL-201-01: Calculate eNPS score for 50 Promoters, 30 Passives, 20 Detractors -> +30.0")
    void testCalculateEnpsStandardDataset() {
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
        assertEquals(50.0, result.getPromoterPercentage());
        assertEquals(20.0, result.getDetractorPercentage());
        assertEquals(30.0, result.getEnpsScore(), "eNPS must equal exactly +30.0 for 50% Promoters - 20% Detractors");
    }

    @Test
    @DisplayName("TC-ANL-201-02: Empty or null scores list safely returns eNPS = 0.0 without division by zero")
    void testCalculateEnpsEmptyList() {
        EnpsResult resultNull = enpsCalculator.calculateEnps(null);
        assertEquals(0, resultNull.getTotalResponses());
        assertEquals(0.0, resultNull.getEnpsScore());

        EnpsResult resultEmpty = enpsCalculator.calculateEnps(Collections.emptyList());
        assertEquals(0, resultEmpty.getTotalResponses());
        assertEquals(0.0, resultEmpty.getEnpsScore());
    }
}
