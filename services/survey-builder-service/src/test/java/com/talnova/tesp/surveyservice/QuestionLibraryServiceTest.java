package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.domain.model.QuestionLibraryDocument;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryCreateDTO;
import com.talnova.tesp.surveyservice.dto.QuestionLibraryResponseDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.mapper.QuestionLibraryMapper;
import com.talnova.tesp.surveyservice.repository.QuestionLibraryRepository;
import com.talnova.tesp.surveyservice.service.QuestionLibraryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionLibraryServiceTest {

    @Mock
    private QuestionLibraryRepository questionLibraryRepository;

    private QuestionLibraryMapper questionLibraryMapper;
    private QuestionLibraryServiceImpl questionLibraryService;

    @BeforeEach
    void setUp() {
        questionLibraryMapper = new QuestionLibraryMapper();
        questionLibraryService = new QuestionLibraryServiceImpl(questionLibraryRepository, questionLibraryMapper);
    }

    @Test
    @DisplayName("TC-SRV-401-A: Create question library template successfully")
    void testCreateQuestionTemplateSuccess() {
        QuestionLibraryCreateDTO createDTO = QuestionLibraryCreateDTO.builder()
                .libraryId("LIB-LEAD-001")
                .category("LEADERSHIP")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "My manager provides clear feedback."))
                .tags(List.of("feedback", "management"))
                .build();

        QuestionLibraryDocument mockDoc = questionLibraryMapper.toDocument(createDTO);
        mockDoc.setId("66b26d8f8a84a51e3c8b9999");

        when(questionLibraryRepository.existsByLibraryIdAndIsDeletedFalse("LIB-LEAD-001")).thenReturn(false);
        when(questionLibraryRepository.save(any(QuestionLibraryDocument.class))).thenReturn(mockDoc);

        QuestionLibraryResponseDTO response = questionLibraryService.createQuestionTemplate(createDTO);

        assertNotNull(response);
        assertEquals("LIB-LEAD-001", response.getLibraryId());
        assertEquals("LEADERSHIP", response.getCategory());
        assertEquals(QuestionType.LIKERT, response.getType());
        verify(questionLibraryRepository, times(1)).save(any(QuestionLibraryDocument.class));
    }

    @Test
    @DisplayName("TC-SRV-401-B: Creating duplicate libraryId throws SurveyValidationException")
    void testCreateDuplicateLibraryIdThrowsException() {
        QuestionLibraryCreateDTO createDTO = QuestionLibraryCreateDTO.builder()
                .libraryId("LIB-LEAD-001")
                .category("LEADERSHIP")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .build();

        when(questionLibraryRepository.existsByLibraryIdAndIsDeletedFalse("LIB-LEAD-001")).thenReturn(true);

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> questionLibraryService.createQuestionTemplate(createDTO)
        );

        assertTrue(ex.getMessage().contains("already exists"));
        verify(questionLibraryRepository, never()).save(any(QuestionLibraryDocument.class));
    }

    @Test
    @DisplayName("TC-SRV-401-C: Search question templates by category and search keyword")
    void testSearchQuestionsByCategoryAndKeyword() {
        QuestionLibraryDocument doc1 = QuestionLibraryDocument.builder()
                .libraryId("LIB-LEAD-001")
                .category("LEADERSHIP")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "Direct manager clear direction"))
                .tags(List.of("direction"))
                .build();

        QuestionLibraryDocument doc2 = QuestionLibraryDocument.builder()
                .libraryId("LIB-LEAD-002")
                .category("LEADERSHIP")
                .type(QuestionType.NPS)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "Recommend leadership team"))
                .tags(List.of("nps"))
                .build();

        when(questionLibraryRepository.findByCategoryIgnoreCaseAndIsDeletedFalse("LEADERSHIP"))
                .thenReturn(List.of(doc1, doc2));

        List<QuestionLibraryResponseDTO> results = questionLibraryService.searchQuestions("LEADERSHIP", "direction");

        assertEquals(1, results.size());
        assertEquals("LIB-LEAD-001", results.getFirst().getLibraryId());
    }
}
