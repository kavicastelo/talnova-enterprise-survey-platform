package com.talnova.tesp.surveyservice.repository;

import com.talnova.tesp.surveyservice.domain.model.OutboxEventDocument;
import com.talnova.tesp.surveyservice.domain.model.OutboxStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for OutboxEventDocument entities.
 */
@Repository
public interface OutboxEventRepository extends MongoRepository<OutboxEventDocument, String> {

    List<OutboxEventDocument> findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
