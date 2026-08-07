package com.talnova.tesp.actionservice.recommendation;

import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;

import java.util.List;

public interface ActionTemplateRecommenderService {

    /**
     * Recommends AI-matched remedial action templates based on Question Group and keyword search per FR-ACT-003.
     */
    List<ActionTemplateDocument> getRecommendations(String groupId, String keyword);
}
