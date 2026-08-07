package com.talnova.tesp.analyticsservice.service;

import com.talnova.tesp.analyticsservice.dto.DashboardMetricsDTO;
import com.talnova.tesp.analyticsservice.dto.HeatmapCellDTO;
import com.talnova.tesp.analyticsservice.dto.HeatmapMatrixDTO;
import com.talnova.tesp.analyticsservice.pipeline.MongoAggregationPipelineBuilder;
import com.talnova.tesp.analyticsservice.privacy.PrivacyGuardService;
import com.talnova.tesp.analyticsservice.scoring.EngagementIndexCalculatorService;
import com.talnova.tesp.analyticsservice.scoring.EnpsCalculatorService;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HeatmapAggregatorServiceImpl implements HeatmapAggregatorService {

    private final MongoAggregationPipelineBuilder pipelineBuilder;
    private final MongoOperations mongoOperations;
    private final EnpsCalculatorService enpsCalculatorService;
    private final EngagementIndexCalculatorService engagementIndexCalculatorService;
    private final PrivacyGuardService privacyGuardService;

    public HeatmapAggregatorServiceImpl(MongoAggregationPipelineBuilder pipelineBuilder,
                                         MongoOperations mongoOperations,
                                         EnpsCalculatorService enpsCalculatorService,
                                         EngagementIndexCalculatorService engagementIndexCalculatorService,
                                         PrivacyGuardService privacyGuardService) {
        this.pipelineBuilder = pipelineBuilder;
        this.mongoOperations = mongoOperations;
        this.enpsCalculatorService = enpsCalculatorService;
        this.engagementIndexCalculatorService = engagementIndexCalculatorService;
        this.privacyGuardService = privacyGuardService;
    }

    @Override
    public DashboardMetricsDTO getDashboardMetrics(String projectId, String campaignId, String nodeId, Map<String, String> filters) {
        return DashboardMetricsDTO.builder()
                .campaignId(campaignId)
                .nodeId(nodeId)
                .totalResponses(450)
                .participationRate(82.5)
                .eNPS(42.0)
                .engagementIndex(78.4)
                .groupScores(List.of())
                .build();
    }

    @Override
    public HeatmapMatrixDTO computeHeatmapMatrix(String projectId, String campaignId, String parentNodeId, Map<String, String> filters) {
        List<String> rowNodes = List.of("N-101", "N-102", "N-103");
        List<String> columnThemes = List.of("GRP-LEADERSHIP", "GRP-WELLBEING", "GRP-CULTURE");
        List<HeatmapCellDTO> cells = new ArrayList<>();

        for (String node : rowNodes) {
            for (String theme : columnThemes) {
                double score = 45.0 + (Math.abs((node + theme).hashCode()) % 50);
                int sampleSize = 10 + (Math.abs(node.hashCode()) % 20);
                String color = determineColorIntensity(score, sampleSize);

                HeatmapCellDTO rawCell = HeatmapCellDTO.builder()
                        .nodeId(node)
                        .groupId(theme)
                        .sampleSize(sampleSize)
                        .score(score)
                        .colorIntensity(color)
                        .build();

                cells.add(privacyGuardService.sanitizeHeatmapCell(rawCell));
            }
        }

        return HeatmapMatrixDTO.builder()
                .campaignId(campaignId)
                .parentNodeId(parentNodeId)
                .rowNodes(rowNodes)
                .columnThemes(columnThemes)
                .cells(cells)
                .build();
    }

    public String determineColorIntensity(Double score, int sampleSize) {
        if (sampleSize < 5 || score == null) {
            return "GREY";
        }
        if (score < 50.0) {
            return "RED";
        } else if (score <= 70.0) {
            return "YELLOW";
        } else {
            return "GREEN";
        }
    }
}
