package com.talnova.tesp.analyticsservice.service;

import com.talnova.tesp.analyticsservice.dto.DashboardMetricsDTO;
import com.talnova.tesp.analyticsservice.dto.HeatmapMatrixDTO;

import java.util.Map;

public interface HeatmapAggregatorService {

    /**
     * Computes real-time overview dashboard metrics for campaign and organization node scope.
     */
    DashboardMetricsDTO getDashboardMetrics(String projectId, String campaignId, String nodeId, Map<String, String> filters);

    /**
     * Computes 2D comparative heatmap matrix of Department Nodes vs Question Group Themes.
     */
    HeatmapMatrixDTO computeHeatmapMatrix(String projectId, String campaignId, String parentNodeId, Map<String, String> filters);
}
