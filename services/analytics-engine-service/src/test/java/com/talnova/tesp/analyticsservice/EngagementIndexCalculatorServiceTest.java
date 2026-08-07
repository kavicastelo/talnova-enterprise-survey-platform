package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.domain.model.GroupScore;
import com.talnova.tesp.analyticsservice.scoring.EngagementIndexCalculatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EngagementIndexCalculatorServiceTest {

    private EngagementIndexCalculatorServiceImpl calculator;

    @BeforeEach
    void setUp() {
        calculator = new EngagementIndexCalculatorServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-202-01: Likert 4.0 raw score normalizes to 75.0% 100-point engagement index")
    void testNormalizeLikertScore() {
        double normalized = calculator.calculateNormalizedScore(4.0);
        assertEquals(75.0, normalized, "Likert 4.0 must normalize to 75.0%");

        double minScore = calculator.calculateNormalizedScore(1.0);
        assertEquals(0.0, minScore, "Likert 1.0 must normalize to 0.0%");

        double maxScore = calculator.calculateNormalizedScore(5.0);
        assertEquals(100.0, maxScore, "Likert 5.0 must normalize to 100.0%");
    }

    @Test
    @DisplayName("TC-ANL-202-02: Calculate group engagement index theme aggregate")
    void testCalculateGroupEngagementIndex() {
        GroupScore groupScore = calculator.calculateGroupEngagementIndex("GRP-LEADERSHIP", List.of(5.0, 4.0, 3.0, 4.0));

        assertNotNull(groupScore);
        assertEquals("GRP-LEADERSHIP", groupScore.getGroupId());
        // Scores 5.0 (100%), 4.0 (75%), 3.0 (50%), 4.0 (75%) -> Mean = (100+75+50+75)/4 = 75.0%
        assertEquals(75.0, groupScore.getScore());
    }
}
