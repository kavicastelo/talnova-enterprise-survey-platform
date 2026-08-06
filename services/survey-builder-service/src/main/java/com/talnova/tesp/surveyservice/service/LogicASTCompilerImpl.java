package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.domain.model.LogicOperator;
import com.talnova.tesp.surveyservice.domain.model.LogicRule;
import com.talnova.tesp.surveyservice.dto.LogicRuleDTO;
import com.talnova.tesp.surveyservice.dto.SurveyPageDTO;
import com.talnova.tesp.surveyservice.dto.SurveyQuestionDTO;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;
import com.talnova.tesp.surveyservice.dto.SurveySectionDTO;
import com.talnova.tesp.surveyservice.exception.SurveyValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of LogicASTCompiler for validating and evaluating declarative skip logic AST rules,
 * enforcing forward-only skip logic to prevent circular branching loops.
 */
@Component
public class LogicASTCompilerImpl implements LogicASTCompiler {

    private static final Logger log = LoggerFactory.getLogger(LogicASTCompilerImpl.class);

    @Override
    public void validateLogicRules(SurveySaveDraftDTO dto) {
        if (dto.getPages() == null || dto.getPages().isEmpty()) {
            return;
        }

        Map<String, Integer> pageOrderMap = new HashMap<>();
        for (SurveyPageDTO page : dto.getPages()) {
            if (page.getPageId() != null && !page.getPageId().isBlank()) {
                pageOrderMap.put(page.getPageId(), page.getPageOrder());
            }
        }

        for (SurveyPageDTO page : dto.getPages()) {
            if (page.getSections() == null) continue;
            int sourcePageOrder = page.getPageOrder();
            String sourcePageId = page.getPageId();

            for (SurveySectionDTO section : page.getSections()) {
                if (section.getQuestions() == null) continue;
                for (SurveyQuestionDTO question : section.getQuestions()) {
                    if (question.getLogicRules() == null) continue;
                    for (LogicRuleDTO rule : question.getLogicRules()) {
                        validateRule(rule, pageOrderMap, sourcePageId, sourcePageOrder, question.getQuestionId());
                    }
                }
            }
        }
    }

    @Override
    public boolean evaluateRule(LogicRule rule, String answerValue) {
        if (rule == null || answerValue == null || rule.getOperator() == null) {
            return false;
        }

        String compValue = rule.getComparisonValue() != null ? rule.getComparisonValue() : "";
        LogicOperator operator = rule.getOperator();

        return switch (operator) {
            case EQUALS -> answerValue.trim().equalsIgnoreCase(compValue.trim());
            case NOT_EQUALS -> !answerValue.trim().equalsIgnoreCase(compValue.trim());
            case LESS_THAN -> compareNumeric(answerValue, compValue) < 0;
            case GREATER_THAN -> compareNumeric(answerValue, compValue) > 0;
        };
    }

    private void validateRule(LogicRuleDTO rule, Map<String, Integer> pageOrderMap, String sourcePageId, int sourcePageOrder, String questionId) {
        if (rule.getOperator() == null) {
            throw new SurveyValidationException("Invalid logic rule in question '" + questionId + "': operator is mandatory");
        }

        if (rule.getTargetPageId() == null || rule.getTargetPageId().isBlank()) {
            throw new SurveyValidationException("Invalid logic rule in question '" + questionId + "': targetPageId is mandatory");
        }

        String targetPageId = rule.getTargetPageId();
        if (!pageOrderMap.containsKey(targetPageId)) {
            throw new SurveyValidationException("Invalid logic rule target page: target page ID '" + targetPageId + "' in question '" + questionId + "' does not exist in survey");
        }

        int targetPageOrder = pageOrderMap.get(targetPageId);
        if (targetPageOrder <= sourcePageOrder) {
            throw new SurveyValidationException("Invalid forward skip target page: target page '" + targetPageId + "' (order " + targetPageOrder + ") must appear after source page '" + sourcePageId + "' (order " + sourcePageOrder + ")");
        }
    }

    private int compareNumeric(String val1, String val2) {
        try {
            double d1 = Double.parseDouble(val1.trim());
            double d2 = Double.parseDouble(val2.trim());
            return Double.compare(d1, d2);
        } catch (NumberFormatException e) {
            log.warn("Numeric logic evaluation fallback to string comparison for values: '{}', '{}'", val1, val2);
            return val1.trim().compareToIgnoreCase(val2.trim());
        }
    }
}
