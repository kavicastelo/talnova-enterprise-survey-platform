package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyPage;
import com.talnova.tesp.surveyservice.domain.model.SurveyQuestion;
import com.talnova.tesp.surveyservice.domain.model.SurveySection;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.SurveyNotFoundException;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.mapper.SurveyMapper;
import com.talnova.tesp.surveyservice.repository.SurveyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.domain.model.OutboxEventDocument;
import com.talnova.tesp.surveyservice.repository.OutboxEventRepository;
import com.talnova.tesp.surveyservice.service.LocaleDictionaryResolverImpl;
import com.talnova.tesp.surveyservice.service.LogicASTCompilerImpl;
import com.talnova.tesp.surveyservice.service.SurveyBuilderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyBuilderServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    private SurveyMapper surveyMapper;
    private LogicASTCompilerImpl logicASTCompiler;
    private LocaleDictionaryResolverImpl localeDictionaryResolver;
    private ObjectMapper objectMapper;
    private SurveyBuilderServiceImpl surveyBuilderService;

    @BeforeEach
    void setUp() {
        surveyMapper = new SurveyMapper();
        logicASTCompiler = new LogicASTCompilerImpl();
        localeDictionaryResolver = new LocaleDictionaryResolverImpl();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        surveyBuilderService = new SurveyBuilderServiceImpl(
                surveyRepository, surveyMapper, logicASTCompiler, localeDictionaryResolver, outboxEventRepository, objectMapper
        );
    }

    @Test
    @DisplayName("TC-SRV-202-A: Successfully save new draft survey with valid question group")
    void testSaveDraftSuccess() {
        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .isMandatory(true)
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-LEADERSHIP")
                .questions(List.of(question))
                .build();

        SurveyPageDTO page = SurveyPageDTO.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(section))
                .build();

        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .title(Map.of("en-US", "2026 Annual Employee Engagement Survey"))
                .pages(List.of(page))
                .build();

        SurveyDocument mockSavedDoc = surveyMapper.toDocument(draftDTO);
        mockSavedDoc.setId("66b26d8f8a84a51e3c8b4567");
        mockSavedDoc.setVersion(1);
        mockSavedDoc.setStatus(SurveyStatus.DRAFT);

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse("PRJ-99201", "SRV-5001", SurveyStatus.DRAFT))
                .thenReturn(Optional.empty());
        when(surveyRepository.save(any(SurveyDocument.class))).thenReturn(mockSavedDoc);

        SurveyResponseDTO response = surveyBuilderService.saveDraft("SRV-5001", draftDTO);

        assertNotNull(response);
        assertEquals("SRV-5001", response.getSurveyId());
        assertEquals("PRJ-99201", response.getProjectId());
        assertEquals(1, response.getVersion());
        assertEquals(SurveyStatus.DRAFT, response.getStatus());
        verify(surveyRepository, times(1)).save(any(SurveyDocument.class));
    }

    @Test
    @DisplayName("TC-SRV-202-B: Throw SurveyValidationException when Likert question lacks mandatory groupId")
    void testSaveDraftMissingGroupIdThrowsValidationException() {
        SurveyQuestionDTO questionWithoutGroup = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .type(QuestionType.LIKERT)
                .groupId(null)
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-LEADERSHIP")
                .questions(List.of(questionWithoutGroup))
                .build();

        SurveyPageDTO page = SurveyPageDTO.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(section))
                .build();

        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .pages(List.of(page))
                .build();

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> surveyBuilderService.saveDraft("SRV-5001", draftDTO)
        );

        assertTrue(ex.getMessage().contains("Mandatory Question Group missing"));
        verify(surveyRepository, never()).save(any(SurveyDocument.class));
    }

    @Test
    @DisplayName("TC-SRV-202-C: Get survey successfully by projectId and surveyId")
    void testGetSurveySuccess() {
        SurveyDocument doc = SurveyDocument.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.DRAFT)
                .build();

        when(surveyRepository.findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc("PRJ-99201", "SRV-5001"))
                .thenReturn(Optional.of(doc));

        SurveyResponseDTO response = surveyBuilderService.getSurvey("PRJ-99201", "SRV-5001");

        assertNotNull(response);
        assertEquals("SRV-5001", response.getSurveyId());
    }

    @Test
    @DisplayName("TC-SRV-202-D: Get survey throws SurveyNotFoundException when survey does not exist")
    void testGetSurveyNotFoundThrowsException() {
        when(surveyRepository.findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc("PRJ-99201", "SRV-9999"))
                .thenReturn(Optional.empty());

        assertThrows(
                SurveyNotFoundException.class,
                () -> surveyBuilderService.getSurvey("PRJ-99201", "SRV-9999")
        );
    }

    @Test
    @DisplayName("TC-SRV-203-A: Publish survey version transitions status to PUBLISHED and sets publishedAt")
    void testPublishSurveySuccess() {
        SurveyQuestion question = SurveyQuestion.builder()
                .questionId("Q-101")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .build();

        SurveySection section = SurveySection.builder()
                .sectionId("SEC-1")
                .questions(List.of(question))
                .build();

        SurveyPage page = SurveyPage.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(section))
                .build();

        SurveyDocument draftDoc = SurveyDocument.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.DRAFT)
                .pages(List.of(page))
                .versionHistory(new ArrayList<>())
                .build();

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse("PRJ-99201", "SRV-5001", SurveyStatus.DRAFT))
                .thenReturn(Optional.of(draftDoc));
        when(surveyRepository.save(any(SurveyDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponseDTO response = surveyBuilderService.publishSurvey("PRJ-99201", "SRV-5001");

        assertNotNull(response);
        assertEquals(SurveyStatus.PUBLISHED, response.getStatus());
        assertNotNull(response.getPublishedAt());
        assertTrue(response.getVersionHistory().contains(1));
        verify(outboxEventRepository, times(1)).save(any(OutboxEventDocument.class));
    }

    @Test
    @DisplayName("TC-SRV-203-B: Attempting to publish an empty survey throws SurveyValidationException")
    void testPublishEmptySurveyThrowsException() {
        SurveyDocument emptyDraftDoc = SurveyDocument.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.DRAFT)
                .pages(new ArrayList<>()) // Empty pages!
                .build();

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse("PRJ-99201", "SRV-5001", SurveyStatus.DRAFT))
                .thenReturn(Optional.of(emptyDraftDoc));

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> surveyBuilderService.publishSurvey("PRJ-99201", "SRV-5001")
        );

        assertEquals("Cannot publish empty survey", ex.getMessage());
    }

    @Test
    @DisplayName("TC-SRV-203-C (TC-SRV-001): Saving draft on a published survey throws SurveyValidationException")
    void testSaveDraftOnPublishedSurveyThrowsException() {
        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .build();

        SurveyDocument publishedDoc = SurveyDocument.builder()
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.PUBLISHED)
                .build();

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse("PRJ-99201", "SRV-5001", SurveyStatus.DRAFT))
                .thenReturn(Optional.empty());
        when(surveyRepository.findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc("PRJ-99201", "SRV-5001"))
                .thenReturn(Optional.of(publishedDoc));

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> surveyBuilderService.saveDraft("SRV-5001", draftDTO)
        );

        assertEquals("Cannot modify structural design of a published survey version", ex.getMessage());
    }

    @Test
    @DisplayName("TC-SRV-203-D: Create new draft version (N+1) from published survey")
    void testCreateDraftVersionFromPublishedSurvey() {
        SurveyDocument publishedDoc = SurveyDocument.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .status(SurveyStatus.PUBLISHED)
                .versionHistory(List.of(1))
                .build();

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse("PRJ-99201", "SRV-5001", SurveyStatus.DRAFT))
                .thenReturn(Optional.empty());
        when(surveyRepository.findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc("PRJ-99201", "SRV-5001"))
                .thenReturn(Optional.of(publishedDoc));
        when(surveyRepository.save(any(SurveyDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponseDTO response = surveyBuilderService.createDraftVersion("PRJ-99201", "SRV-5001");

        assertNotNull(response);
        assertEquals(2, response.getVersion());
        assertEquals(SurveyStatus.DRAFT, response.getStatus());
        assertNull(response.getPublishedAt());
    }
}
