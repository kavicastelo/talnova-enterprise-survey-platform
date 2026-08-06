package com.talnova.tesp.surveyservice;

import com.talnova.tesp.surveyservice.domain.model.LogicOperator;
import com.talnova.tesp.surveyservice.domain.model.LogicRule;
import com.talnova.tesp.surveyservice.dto.LogicRuleDTO;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import com.talnova.tesp.surveyservice.service.LogicASTCompilerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogicASTCompilerTest {

    private LogicASTCompilerImpl logicASTCompiler;

    @BeforeEach
    void setUp() {
        logicASTCompiler = new LogicASTCompilerImpl();
    }

    @Test
    @DisplayName("TC-SRV-301-A: Valid forward logic rule referencing existing target page ID passes validation")
    void testValidateLogicRulesSuccess() {
        LogicRuleDTO rule = LogicRuleDTO.builder()
                .ruleId("RULE-1")
                .operator(LogicOperator.LESS_THAN)
                .comparisonValue("7")
                .targetPageId("PAGE-2")
                .build();

        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .logicRules(List.of(rule))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-1")
                .questions(List.of(question))
                .build();

        SurveyPageDTO page1 = SurveyPageDTO.builder().pageId("PAGE-1").pageOrder(1).sections(List.of(section)).build();
        SurveyPageDTO page2 = SurveyPageDTO.builder().pageId("PAGE-2").pageOrder(2).sections(List.of()).build();

        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .pages(List.of(page1, page2))
                .build();

        assertDoesNotThrow(() -> logicASTCompiler.validateLogicRules(draftDTO));
    }

    @Test
    @DisplayName("TC-SRV-301-B: Logic rule referencing non-existent target page ID throws SurveyValidationException")
    void testValidateLogicRulesNonExistentTargetPageThrowsException() {
        LogicRuleDTO rule = LogicRuleDTO.builder()
                .ruleId("RULE-1")
                .operator(LogicOperator.EQUALS)
                .comparisonValue("Detractor")
                .targetPageId("PAGE-99")
                .build();

        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-101")
                .logicRules(List.of(rule))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-1")
                .questions(List.of(question))
                .build();

        SurveyPageDTO page1 = SurveyPageDTO.builder().pageId("PAGE-1").pageOrder(1).sections(List.of(section)).build();

        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .pages(List.of(page1))
                .build();

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> logicASTCompiler.validateLogicRules(draftDTO)
        );

        assertTrue(ex.getMessage().contains("target page ID 'PAGE-99'"));
    }

    @Test
    @DisplayName("TC-SRV-302-A: Backward skip logic rule (Page 3 to Page 1) throws SurveyValidationException")
    void testBackwardSkipLogicRuleThrowsException() {
        LogicRuleDTO backwardRule = LogicRuleDTO.builder()
                .ruleId("RULE-1")
                .operator(LogicOperator.LESS_THAN)
                .comparisonValue("5")
                .targetPageId("PAGE-1") // Backward skip to Page 1!
                .build();

        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-301")
                .logicRules(List.of(backwardRule))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-3")
                .questions(List.of(question))
                .build();

        SurveyPageDTO page1 = SurveyPageDTO.builder().pageId("PAGE-1").pageOrder(1).sections(List.of()).build();
        SurveyPageDTO page2 = SurveyPageDTO.builder().pageId("PAGE-2").pageOrder(2).sections(List.of()).build();
        SurveyPageDTO page3 = SurveyPageDTO.builder().pageId("PAGE-3").pageOrder(3).sections(List.of(section)).build();

        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .pages(List.of(page1, page2, page3))
                .build();

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> logicASTCompiler.validateLogicRules(draftDTO)
        );

        assertTrue(ex.getMessage().contains("Invalid forward skip target page"));
    }

    @Test
    @DisplayName("TC-SRV-302-B: Self-page skip logic rule (Page 2 to Page 2) throws SurveyValidationException")
    void testSelfPageSkipLogicRuleThrowsException() {
        LogicRuleDTO selfRule = LogicRuleDTO.builder()
                .ruleId("RULE-1")
                .operator(LogicOperator.EQUALS)
                .comparisonValue("Yes")
                .targetPageId("PAGE-2") // Self jump!
                .build();

        SurveyQuestionDTO question = SurveyQuestionDTO.builder()
                .questionId("Q-201")
                .logicRules(List.of(selfRule))
                .build();

        SurveySectionDTO section = SurveySectionDTO.builder()
                .sectionId("SEC-2")
                .questions(List.of(question))
                .build();

        SurveyPageDTO page1 = SurveyPageDTO.builder().pageId("PAGE-1").pageOrder(1).sections(List.of()).build();
        SurveyPageDTO page2 = SurveyPageDTO.builder().pageId("PAGE-2").pageOrder(2).sections(List.of(section)).build();

        SurveySaveDraftDTO draftDTO = SurveySaveDraftDTO.builder()
                .pages(List.of(page1, page2))
                .build();

        SurveyValidationException ex = assertThrows(
                SurveyValidationException.class,
                () -> logicASTCompiler.validateLogicRules(draftDTO)
        );

        assertTrue(ex.getMessage().contains("Invalid forward skip target page"));
    }

    @Test
    @DisplayName("TC-SRV-301-C: Evaluate rule operators (EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN)")
    void testEvaluateRuleOperators() {
        LogicRule ruleEquals = LogicRule.builder()
                .operator(LogicOperator.EQUALS)
                .comparisonValue("OptionA")
                .build();
        assertTrue(logicASTCompiler.evaluateRule(ruleEquals, "OptionA"));
        assertFalse(logicASTCompiler.evaluateRule(ruleEquals, "OptionB"));

        LogicRule ruleNotEquals = LogicRule.builder()
                .operator(LogicOperator.NOT_EQUALS)
                .comparisonValue("OptionA")
                .build();
        assertFalse(logicASTCompiler.evaluateRule(ruleNotEquals, "OptionA"));
        assertTrue(logicASTCompiler.evaluateRule(ruleNotEquals, "OptionB"));

        LogicRule ruleLessThan = LogicRule.builder()
                .operator(LogicOperator.LESS_THAN)
                .comparisonValue("7")
                .build();
        assertTrue(logicASTCompiler.evaluateRule(ruleLessThan, "5"));
        assertFalse(logicASTCompiler.evaluateRule(ruleLessThan, "8"));

        LogicRule ruleGreaterThan = LogicRule.builder()
                .operator(LogicOperator.GREATER_THAN)
                .comparisonValue("7")
                .build();
        assertTrue(logicASTCompiler.evaluateRule(ruleGreaterThan, "9"));
        assertFalse(logicASTCompiler.evaluateRule(ruleGreaterThan, "4"));
    }
}
