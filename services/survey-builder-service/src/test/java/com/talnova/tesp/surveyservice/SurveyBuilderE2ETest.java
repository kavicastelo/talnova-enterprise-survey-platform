package com.talnova.tesp.surveyservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.surveyservice.domain.model.LogicOperator;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import com.talnova.tesp.surveyservice.dto.LogicRuleDTO;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveyResponseDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.mapper.SurveyMapper;
import com.talnova.tesp.surveyservice.repository.OutboxEventRepository;
import com.talnova.tesp.surveyservice.repository.SurveyRepository;
import com.talnova.tesp.surveyservice.service.LocaleDictionaryResolverImpl;
import com.talnova.tesp.surveyservice.service.LogicASTCompilerImpl;
import com.talnova.tesp.surveyservice.service.SurveyBuilderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyBuilderE2ETest {

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
                surveyRepository,
                surveyMapper,
                logicASTCompiler,
                localeDictionaryResolver,
                outboxEventRepository,
                objectMapper
        );
    }

    @Test
    @DisplayName("TC-SRV-901-E2E: Comprehensive Survey Builder AST Lifecycle, Validation Rules, Lock & Publishing Flow")
    void testFullSurveyBuilderLifecycleAndValidationRules() {
        String projectId = "PRJ-99201";
        String surveyId = "SRV-5001";

        // Step 1: Valid initial draft
        SurveyQuestionDTO validQuestion = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .type(QuestionType.LIKERT)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "My manager provides clear direction."))
                .isMandatory(true)
                .build();

        SurveySectionDTO validSection = SurveySectionDTO.builder()
                .sectionId("SEC-1")
                .questions(List.of(validQuestion))
                .build();

        SurveyPageDTO validPage1 = SurveyPageDTO.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(validSection))
                .build();

        SurveySaveDraftDTO validDraftPayload = SurveySaveDraftDTO.builder()
                .projectId(projectId)
                .surveyId(surveyId)
                .title(Map.of("en-US", "2026 Annual Employee Engagement Survey"))
                .pages(List.of(validPage1))
                .build();

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(projectId, surveyId, SurveyStatus.DRAFT))
                .thenReturn(Optional.empty());
        when(surveyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SurveyResponseDTO savedDraftResponse = surveyBuilderService.saveDraft(surveyId, validDraftPayload);
        assertNotNull(savedDraftResponse);
        assertEquals(1, savedDraftResponse.getVersion());

        // Step 2: Validate BR-SRV-002 (Missing quantitative question groupId)
        SurveyQuestionDTO invalidQuestionNoGroup = SurveyQuestionDTO.builder()
                .questionId("Q-102")
                .type(QuestionType.LIKERT)
                .groupId(null) // Violation!
                .prompt(Map.of("en-US", "Invalid quantitative question"))
                .build();

        SurveyPageDTO invalidPageGroup = SurveyPageDTO.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(SurveySectionDTO.builder().sectionId("SEC-1").questions(List.of(invalidQuestionNoGroup)).build()))
                .build();

        SurveySaveDraftDTO invalidGroupDraft = SurveySaveDraftDTO.builder()
                .projectId(projectId)
                .surveyId(surveyId)
                .pages(List.of(invalidPageGroup))
                .build();

        assertThrows(SurveyValidationException.class, () -> surveyBuilderService.saveDraft(surveyId, invalidGroupDraft));

        // Step 3: Validate VR-SRV-004 (Missing default locale prompt)
        SurveyQuestionDTO invalidQuestionNoDefaultLocale = SurveyQuestionDTO.builder()
                .questionId("Q-103")
                .type(QuestionType.SHORT_TEXT)
                .prompt(Map.of("si-LK", "මගේ කළමනාකරු")) // Missing en-US!
                .build();

        SurveyPageDTO invalidPageLocale = SurveyPageDTO.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .sections(List.of(SurveySectionDTO.builder().sectionId("SEC-1").questions(List.of(invalidQuestionNoDefaultLocale)).build()))
                .build();

        SurveySaveDraftDTO invalidLocaleDraft = SurveySaveDraftDTO.builder()
                .projectId(projectId)
                .surveyId(surveyId)
                .pages(List.of(invalidPageLocale))
                .build();

        assertThrows(SurveyValidationException.class, () -> surveyBuilderService.saveDraft(surveyId, invalidLocaleDraft));

        // Step 4: Validate BR-SRV-004 / VR-SRV-003 (Backward skip logic cycle)
        LogicRuleDTO backwardRule = LogicRuleDTO.builder()
                .ruleId("RULE-1")
                .operator(LogicOperator.EQUALS)
                .comparisonValue("1")
                .targetPageId("PAGE-1") // Backward jump from Page 2 to Page 1!
                .build();

        SurveyQuestionDTO questionWithBackwardLogic = SurveyQuestionDTO.builder()
                .questionId("Q-201")
                .type(QuestionType.SHORT_TEXT)
                .prompt(Map.of("en-US", "Follow up comment"))
                .logicRules(List.of(backwardRule))
                .build();

        SurveyPageDTO page1 = SurveyPageDTO.builder().pageId("PAGE-1").pageOrder(1).sections(List.of()).build();
        SurveyPageDTO page2WithBackwardLogic = SurveyPageDTO.builder()
                .pageId("PAGE-2")
                .pageOrder(2)
                .sections(List.of(SurveySectionDTO.builder().sectionId("SEC-2").questions(List.of(questionWithBackwardLogic)).build()))
                .build();

        SurveySaveDraftDTO invalidCycleDraft = SurveySaveDraftDTO.builder()
                .projectId(projectId)
                .surveyId(surveyId)
                .pages(List.of(page1, page2WithBackwardLogic))
                .build();

        assertThrows(SurveyValidationException.class, () -> surveyBuilderService.saveDraft(surveyId, invalidCycleDraft));

        // Step 5: Publish Survey & Verify Outbox Event
        SurveyDocument draftDoc = surveyMapper.toDocument(validDraftPayload);
        draftDoc.setVersion(1);
        draftDoc.setStatus(SurveyStatus.DRAFT);

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(projectId, surveyId, SurveyStatus.DRAFT))
                .thenReturn(Optional.of(draftDoc));

        SurveyResponseDTO publishedResponse = surveyBuilderService.publishSurvey(projectId, surveyId);
        assertEquals(SurveyStatus.PUBLISHED, publishedResponse.getStatus());
        verify(outboxEventRepository, times(1)).save(any());

        // Step 6: Verify Structural Mutation Lock on Published Version (BR-SRV-001)
        SurveyDocument publishedDoc = surveyMapper.toDocument(validDraftPayload);
        publishedDoc.setVersion(1);
        publishedDoc.setStatus(SurveyStatus.PUBLISHED);

        when(surveyRepository.findByProjectIdAndSurveyIdAndStatusAndIsDeletedFalse(projectId, surveyId, SurveyStatus.DRAFT))
                .thenReturn(Optional.empty());
        when(surveyRepository.findFirstByProjectIdAndSurveyIdAndIsDeletedFalseOrderByVersionDesc(projectId, surveyId))
                .thenReturn(Optional.of(publishedDoc));

        assertThrows(SurveyValidationException.class, () -> surveyBuilderService.saveDraft(surveyId, validDraftPayload));

        // Step 7: Create New Draft Version (v2) from Published Survey
        SurveyResponseDTO newDraftVersion = surveyBuilderService.createDraftVersion(projectId, surveyId);
        assertEquals(2, newDraftVersion.getVersion());
        assertEquals(SurveyStatus.DRAFT, newDraftVersion.getStatus());
    }
}
