package com.talnova.tesp.analyticsservice.pipeline;

import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.util.Map;

public interface MongoAggregationPipelineBuilder {

    /**
     * Constructs a dynamic MongoDB $facet aggregation pipeline cross-tabulating 2D matrix metrics
     * (Overall KPIs, Node Aggregates, and Theme-level Question Group Aggregates).
     *
     * @param projectId Tenant project identifier
     * @param campaignId Survey campaign identifier
     * @param nodePathScope Authorized materialized path prefix for ABAC scoping
     * @param demographicFilters Map of demographic attribute filters (max 5)
     * @return Configured Spring Data MongoDB Aggregation pipeline object.
     */
    Aggregation buildHeatmapFacetPipeline(String projectId, String campaignId, String nodePathScope, Map<String, String> demographicFilters);
}
