package com.talnova.tesp.analyticsservice.pipeline;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class MongoAggregationPipelineBuilderImpl implements MongoAggregationPipelineBuilder {

    @Override
    public Aggregation buildHeatmapFacetPipeline(String projectId, String campaignId, String nodePathScope, Map<String, String> demographicFilters) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("projectId").is(projectId));
        criteriaList.add(Criteria.where("campaignId").is(campaignId));
        criteriaList.add(Criteria.where("isDeleted").ne(true));

        if (nodePathScope != null && !nodePathScope.isBlank()) {
            String regexPattern = "^" + Pattern.quote(nodePathScope);
            criteriaList.add(Criteria.where("demographicSnapshot.ancestorPaths").regex(regexPattern));
        }

        if (demographicFilters != null && !demographicFilters.isEmpty()) {
            int filterCount = 0;
            for (Map.Entry<String, String> entry : demographicFilters.entrySet()) {
                if (filterCount >= 5) break; // Limit to max 5 demographic filters per VR-ANL-004
                if (entry.getKey() != null && entry.getValue() != null) {
                    criteriaList.add(Criteria.where("demographicSnapshot." + entry.getKey()).is(entry.getValue()));
                    filterCount++;
                }
            }
        }

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        MatchOperation matchStage = Aggregation.match(combinedCriteria);

        FacetOperation facetStage = Aggregation.facet(
                Aggregation.group().count().as("totalResponses")
        ).as("overallMetrics")
        .and(
                Aggregation.group("nodeId").count().as("responseCount")
        ).as("nodeAggregates")
        .and(
                Aggregation.unwind("answers"),
                Aggregation.group("nodeId", "answers.groupId")
                        .count().as("count")
                        .avg("answers.numericValue").as("avgScore")
        ).as("themeAggregates");

        return Aggregation.newAggregation(matchStage, facetStage);
    }
}
