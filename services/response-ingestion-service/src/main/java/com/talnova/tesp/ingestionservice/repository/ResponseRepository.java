package com.talnova.tesp.ingestionservice.repository;

import com.talnova.tesp.ingestionservice.domain.model.SurveyResponseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResponseRepository extends ReactiveMongoRepository<SurveyResponseDocument, String> {

    Flux<SurveyResponseDocument> findByProjectIdAndCampaignIdAndIsDeletedFalse(String projectId, String campaignId);

    Mono<Boolean> existsByResponseTokenAndIsDeletedFalse(String responseToken);

    Mono<Long> countByProjectIdAndCampaignIdAndIsDeletedFalse(String projectId, String campaignId);
}
