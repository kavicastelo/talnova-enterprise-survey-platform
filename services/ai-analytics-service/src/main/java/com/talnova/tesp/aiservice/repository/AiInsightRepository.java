package com.talnova.tesp.aiservice.repository;

import com.talnova.tesp.aiservice.domain.model.AiInsightDocument;
import com.talnova.tesp.aiservice.domain.model.RiskSeverity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiInsightRepository extends MongoRepository<AiInsightDocument, String> {

    List<AiInsightDocument> findByProjectIdAndCampaignId(String projectId, String campaignId);

    Optional<AiInsightDocument> findByResponseIdAndQuestionId(String responseId, String questionId);

    List<AiInsightDocument> findByProjectIdAndRiskSeverity(String projectId, RiskSeverity riskSeverity);
}
