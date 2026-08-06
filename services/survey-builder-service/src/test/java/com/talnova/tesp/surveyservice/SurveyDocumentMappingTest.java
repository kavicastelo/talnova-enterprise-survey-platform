package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.domain.model.LogicOperator;
import com.talnova.tesp.surveyservice.domain.model.LogicRule;
import com.talnova.tesp.surveyservice.domain.model.QuestionType;
import com.talnova.tesp.surveyservice.domain.model.SurveyDocument;
import com.talnova.tesp.surveyservice.domain.model.SurveyPage;
import com.talnova.tesp.surveyservice.domain.model.SurveyQuestion;
import com.talnova.tesp.surveyservice.domain.model.SurveySection;
import com.talnova.tesp.surveyservice.domain.model.SurveyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SurveyDocumentMappingTest {

    @Test
    @DisplayName("TC-SRV-201-A: Construct nested Survey AST hierarchy using Builder pattern")
    void testSurveyDocumentNestedBuilder() {
        LogicRule rule = LogicRule.builder()
                .ruleId("RULE-1")
                .operator(LogicOperator.LESS_THAN)
                .comparisonValue("7")
                .targetPageId("PAGE-3")
                .build();

        SurveyQuestion question = SurveyQuestion.builder()
                .questionId("Q-101")
                .type(QuestionType.NPS)
                .groupId("GRP-LEADERSHIP")
                .prompt(Map.of("en-US", "How likely are you to recommend?"))
                .isMandatory(true)
                .logicRules(List.of(rule))
                .build();

        SurveySection section = SurveySection.builder()
                .sectionId("SEC-LEADERSHIP")
                .title(Map.of("en-US", "Leadership Feedback"))
                .questions(List.of(question))
                .build();

        SurveyPage page = SurveyPage.builder()
                .pageId("PAGE-1")
                .pageOrder(1)
                .title(Map.of("en-US", "Page 1"))
                .sections(List.of(section))
                .build();

        SurveyDocument document = SurveyDocument.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .surveyId("SRV-5001")
                .version(1)
                .title(Map.of("en-US", "2026 Annual Employee Engagement Survey"))
                .status(SurveyStatus.DRAFT)
                .pages(List.of(page))
                .isDeleted(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        assertNotNull(document);
        assertEquals("PRJ-99201", document.getProjectId());
        assertEquals("SRV-5001", document.getSurveyId());
        assertEquals(1, document.getVersion());
        assertEquals(SurveyStatus.DRAFT, document.getStatus());
        assertEquals(1, document.getPages().size());

        SurveyPage retrievedPage = document.getPages().getFirst();
        assertEquals("PAGE-1", retrievedPage.getPageId());
        assertEquals(1, retrievedPage.getSections().size());

        SurveySection retrievedSection = retrievedPage.getSections().getFirst();
        assertEquals("SEC-LEADERSHIP", retrievedSection.getSectionId());
        assertEquals(1, retrievedSection.getQuestions().size());

        SurveyQuestion retrievedQuestion = retrievedSection.getQuestions().getFirst();
        assertEquals("Q-101", retrievedQuestion.getQuestionId());
        assertEquals(QuestionType.NPS, retrievedQuestion.getType());
        assertEquals("GRP-LEADERSHIP", retrievedQuestion.getGroupId());
        assertTrue(retrievedQuestion.isMandatory());

        assertEquals(1, retrievedQuestion.getLogicRules().size());
        LogicRule retrievedRule = retrievedQuestion.getLogicRules().getFirst();
        assertEquals(LogicOperator.LESS_THAN, retrievedRule.getOperator());
        assertEquals("PAGE-3", retrievedRule.getTargetPageId());
    }
}
