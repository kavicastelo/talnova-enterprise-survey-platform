package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.dto.HeatmapMatrixDTO;
import com.talnova.tesp.analyticsservice.pipeline.MongoAggregationPipelineBuilder;
import com.talnova.tesp.analyticsservice.privacy.PrivacyGuardServiceImpl;
import com.talnova.tesp.analyticsservice.scoring.EngagementIndexCalculatorService;
import com.talnova.tesp.analyticsservice.scoring.EnpsCalculatorService;
import com.talnova.tesp.analyticsservice.service.HeatmapAggregatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HeatmapAggregatorServiceTest {

    @Mock
    private MongoAggregationPipelineBuilder pipelineBuilder;

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private EnpsCalculatorService enpsCalculatorService;

    @Mock
    private EngagementIndexCalculatorService engagementIndexCalculatorService;

    private HeatmapAggregatorServiceImpl aggregatorService;

    @BeforeEach
    void setUp() {
        aggregatorService = new HeatmapAggregatorServiceImpl(
                pipelineBuilder,
                mongoOperations,
                enpsCalculatorService,
                engagementIndexCalculatorService,
                new PrivacyGuardServiceImpl()
        );
    }

    @Test
    @DisplayName("TC-ANL-302-01: Verify 2D heatmap matrix formatting and color intensity assignment")
    void testComputeHeatmapMatrix() {
        HeatmapMatrixDTO matrix = aggregatorService.computeHeatmapMatrix("PRJ-99201", "CMP-1001", "N-100", Map.of());

        assertNotNull(matrix);
        assertEquals("CMP-1001", matrix.getCampaignId());
        assertEquals("N-100", matrix.getParentNodeId());
        assertFalse(matrix.getRowNodes().isEmpty());
        assertFalse(matrix.getColumnThemes().isEmpty());
        assertEquals(9, matrix.getCells().size(), "3x3 matrix must contain 9 cells");
    }

    @Test
    @DisplayName("TC-ANL-302-02: Verify color intensity classification logic")
    void testDetermineColorIntensity() {
        assertEquals("GREY", aggregatorService.determineColorIntensity(75.0, 4), "Sample size N < 5 must return GREY");
        assertEquals("RED", aggregatorService.determineColorIntensity(45.0, 10), "Score < 50% must return RED");
        assertEquals("YELLOW", aggregatorService.determineColorIntensity(65.0, 10), "Score 50-70% must return YELLOW");
        assertEquals("GREEN", aggregatorService.determineColorIntensity(85.0, 10), "Score > 70% must return GREEN");
    }
}
