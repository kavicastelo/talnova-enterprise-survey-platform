package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.dto.QuestionLibraryCreateDTO;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryResponseDTO;

import java.util.List;

/**
 * Service interface for managing reusable Question Library catalog items.
 */
public interface QuestionLibraryService {

    /**
     * Saves a new question template into the central Question Library.
     *
     * @param dto Create payload
     * @return Saved QuestionLibraryResponseDTO
     */
    QuestionLibraryResponseDTO createQuestionTemplate(QuestionLibraryCreateDTO dto);

    /**
     * Searches reusable question templates by category and query text.
     *
     * @param category Optional category filter
     * @param search   Optional text query filter
     * @return List of matching QuestionLibraryResponseDTO items
     */
    List<QuestionLibraryResponseDTO> searchQuestions(String category, String search);
}
