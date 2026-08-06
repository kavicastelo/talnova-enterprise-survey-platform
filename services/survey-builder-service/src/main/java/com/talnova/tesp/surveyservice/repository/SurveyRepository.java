package com.talnova.tesp.surveyservice.repository;

import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for SurveyDocument AST entities with tenant scoping and soft delete enforcement.
 */
@Repository
public interface SurveyRepository extends MongoRepository<SurveyDocument, String> {

    Optional<SurveyDocument> findByProjectIdAndSurveyIdAndVersionAndIsDeletedFalse(String projectId, String surveyId, Integer version);

    Optional<SurveyDocument> findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc(String projectId, String surveyId);

    Optional<SurveyDocument> findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(String projectId, String surveyId, SurveyStatus status);

    List<SurveyDocument> findByProjectIdAndIsDeletedFalse(String projectId);

    boolean existsByProjectIdAndSurveyIdAndIsDeletedFalse(String projectId, String surveyId);
}
