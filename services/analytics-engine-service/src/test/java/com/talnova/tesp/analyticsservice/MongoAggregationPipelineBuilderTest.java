package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.pipeline.MongoAggregationPipelineBuilderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MongoAggregationPipelineBuilderTest {

    private MongoAggregationPipelineBuilderImpl builder;

    @BeforeEach
    void setUp() {
        builder = new MongoAggregationPipelineBuilderImpl();
    }

    @Test
    @DisplayName("TC-ANL-301-01: Build $facet cross-tabulation pipeline with match criteria and demographic filters")
    void testBuildHeatmapFacetPipeline() {
        Map<String, String> filters = Map.of("Tenure", "1-3 Years", "Gender", "Female");
        Aggregation aggregation = builder.buildHeatmapFacetPipeline("PRJ-99201", "CMP-1001", ",N-100,N-201,", filters);

        assertNotNull(aggregation);
        assertNotNull(aggregation.toString());
        assertTrue(aggregation.toString().contains("projectId"), "Pipeline must contain projectId match criterion");
        assertTrue(aggregation.toString().contains("CMP-1001"), "Pipeline must contain campaignId match criterion");
        assertTrue(aggregation.toString().contains("$facet"), "Pipeline must include $facet stage for cross-tabulation");
    }
}
