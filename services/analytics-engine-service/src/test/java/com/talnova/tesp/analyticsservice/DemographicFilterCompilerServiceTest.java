package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.exception.ExcessiveFilterParametersException;
import com.talnova.tesp.analyticsservice.filter.DemographicFilterCompilerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DemographicFilterCompilerServiceTest {

    private DemographicFilterCompilerServiceImpl filterCompiler;

    @BeforeEach
    void setUp() {
        filterCompiler = new DemographicFilterCompilerServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-402-01: Successfully compile up to 5 valid demographic filters while ignoring reserved query parameters")
    void testCompileFiltersSuccess() {
        Map<String, String> rawParams = Map.of(
                "campaignId", "CMP-1001",
                "Tenure", "1-3 Years",
                "Gender", "Female",
                "Department", "Engineering",
                "Location", "Factory B",
                "AgeGroup", "25-34"
        );

        Map<String, String> compiled = filterCompiler.compileFilters(rawParams);

        assertNotNull(compiled);
        assertEquals(5, compiled.size());
        assertFalse(compiled.containsKey("campaignId"), "Reserved parameter 'campaignId' must be excluded");
        assertEquals("1-3 Years", compiled.get("Tenure"));
        assertEquals("Female", compiled.get("Gender"));
    }

    @Test
    @DisplayName("TC-ANL-402-02: Throw ExcessiveFilterParametersException when > 5 demographic filters are provided per VR-ANL-004")
    void testCompileFiltersExceeded() {
        Map<String, String> rawParams = new HashMap<>();
        rawParams.put("Tenure", "1-3 Years");
        rawParams.put("Gender", "Female");
        rawParams.put("Department", "Engineering");
        rawParams.put("Location", "Factory B");
        rawParams.put("AgeGroup", "25-34");
        rawParams.put("CustomAttribute", "ValueX"); // 6th filter

        ExcessiveFilterParametersException ex = assertThrows(ExcessiveFilterParametersException.class,
                () -> filterCompiler.compileFilters(rawParams));

        assertTrue(ex.getMessage().contains("Maximum 5 demographic filters allowed"));
    }
}
