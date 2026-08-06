package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.domain.model.QuestionLibraryDocument;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryCreateDTO;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryResponseDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.mapper.QuestionLibraryMapper;
import com.talnova.tesp.surveyservice.repository.QuestionLibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of QuestionLibraryService handling question catalog template persistence and searches.
 */
@Service
public class QuestionLibraryServiceImpl implements QuestionLibraryService {

    private static final Logger log = LoggerFactory.getLogger(QuestionLibraryServiceImpl.class);

    private final QuestionLibraryRepository questionLibraryRepository;
    private final QuestionLibraryMapper questionLibraryMapper;

    public QuestionLibraryServiceImpl(QuestionLibraryRepository questionLibraryRepository,
                                      QuestionLibraryMapper questionLibraryMapper) {
        this.questionLibraryRepository = questionLibraryRepository;
        this.questionLibraryMapper = questionLibraryMapper;
    }

    @Override
    @Transactional
    public QuestionLibraryResponseDTO createQuestionTemplate(QuestionLibraryCreateDTO dto) {
        log.info("Creating Question Library entry with libraryId: {}, category: {}", dto.getLibraryId(), dto.getCategory());

        if (questionLibraryRepository.existsByLibraryIdAndIsDeletedFalse(dto.getLibraryId())) {
            throw new SurveyValidationException("Question Library entry with libraryId '" + dto.getLibraryId() + "' already exists");
        }

        QuestionLibraryDocument doc = questionLibraryMapper.toDocument(dto);
        doc.setDeleted(false);
        doc.setCreatedAt(Instant.now());
        doc.setUpdatedAt(Instant.now());

        QuestionLibraryDocument savedDoc = questionLibraryRepository.save(doc);
        log.info("Question Library entry saved successfully with ID: {}", savedDoc.getId());
        return questionLibraryMapper.toResponseDTO(savedDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionLibraryResponseDTO> searchQuestions(String category, String search) {
        log.info("Searching Question Library entries by category: {}, search: {}", category, search);

        List<QuestionLibraryDocument> docs;
        if (category != null && !category.isBlank()) {
            docs = questionLibraryRepository.findByCategoryIgnoreCaseAndIsDeletedFalse(category.trim());
        } else {
            docs = questionLibraryRepository.findByIsDeletedFalse();
        }

        if (search != null && !search.isBlank()) {
            String lowerSearch = search.trim().toLowerCase();
            docs = docs.stream().filter(doc -> {
                boolean matchesCategory = doc.getCategory() != null && doc.getCategory().toLowerCase().contains(lowerSearch);
                boolean matchesGroupId = doc.getGroupId() != null && doc.getGroupId().toLowerCase().contains(lowerSearch);
                boolean matchesTags = doc.getTags() != null && doc.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lowerSearch));
                boolean matchesPrompt = doc.getPrompt() != null && doc.getPrompt().values().stream().anyMatch(p -> p.toLowerCase().contains(lowerSearch));
                return matchesCategory || matchesGroupId || matchesTags || matchesPrompt;
            }).collect(Collectors.toList());
        }

        return docs.stream()
                .map(questionLibraryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
