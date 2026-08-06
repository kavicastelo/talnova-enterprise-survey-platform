package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;

/**
 * Service interface for Survey Builder operations including draft creation, updating, and AST retrieval.
 */
public interface SurveyBuilderService {

    /**
     * Creates or updates a survey AST draft document.
     *
     * @param surveyId Path survey ID
     * @param dto      Draft payload containing pages, sections, questions and logic rules
     * @return Saved SurveyResponseDTO
     */
    SurveyResponseDTO saveDraft(String surveyId, SurveySaveDraftDTO dto);

    /**
     * Retrieves the latest active or draft survey AST for a tenant.
     *
     * @param projectId Tenant project ID
     * @param surveyId  Survey ID
     * @return SurveyResponseDTO
     */
    SurveyResponseDTO getSurvey(String projectId, String surveyId);

    /**
     * Publishes a draft survey version, freezing structural mutations and transitioning status to PUBLISHED.
     *
     * @param projectId Tenant project ID
     * @param surveyId  Survey ID
     * @return Published SurveyResponseDTO
     */
    SurveyResponseDTO publishSurvey(String projectId, String surveyId);

    /**
     * Creates a new draft version (N+1) from an existing published survey.
     *
     * @param projectId Tenant project ID
     * @param surveyId  Survey ID
     * @return New draft SurveyResponseDTO
     */
    SurveyResponseDTO createDraftVersion(String projectId, String surveyId);
}
