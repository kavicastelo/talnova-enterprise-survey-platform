package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.ai.KeyDriverAnalyzerServiceImpl;
import com.talnova.tesp.analyticsservice.dto.KeyDriverDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeyDriverAnalyzerServiceTest {

    private KeyDriverAnalyzerServiceImpl analyzerService;

    @BeforeEach
    void setUp() {
        analyzerService = new KeyDriverAnalyzerServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-702-01: Calculate Multiple Linear Regression key driver importance weights and categorize impact levels")
    void testAnalyzeKeyDrivers() {
        // Synthetic dataset of 10 observations with 2 themes (Leadership, Wellbeing)
        double[] y = {70.0, 80.0, 60.0, 90.0, 75.0, 85.0, 65.0, 95.0, 50.0, 88.0};
        double[][] x = {
                {4.0, 3.0},
                {5.0, 4.0},
                {3.0, 2.0},
                {5.0, 5.0},
                {4.0, 4.0},
                {5.0, 4.0},
                {3.0, 3.0},
                {5.0, 5.0},
                {2.0, 2.0},
                {5.0, 4.0}
        };
        List<String> themeNames = List.of("Leadership", "Wellbeing");

        List<KeyDriverDTO> drivers = analyzerService.analyzeKeyDrivers(y, x, themeNames);

        assertNotNull(drivers);
        assertEquals(2, drivers.size());
        assertNotNull(drivers.get(0).getImportanceWeight());
        assertNotNull(drivers.get(0).getImpactCategory());
        assertTrue(List.of("HIGH_IMPACT", "MEDIUM_IMPACT", "LOW_IMPACT").contains(drivers.get(0).getImpactCategory()));
    }
}
