package com.talnova.tesp.surveyservice.service;

import com.talnova.tesp.surveyservice.domain.model.LogicRule;
import com.talnova.tesp.surveyservice.dto.SurveySaveDraftDTO;

/**
 * Service component for compiling and validating declarative branching skip logic AST rules.
 */
public interface LogicASTCompiler {

    /**
     * Validates logic rules in survey draft payload to ensure all target page IDs exist.
     *
     * @param dto Survey draft DTO payload
     */
    void validateLogicRules(SurveySaveDraftDTO dto);

    /**
     * Evaluates a branching skip logic rule against a respondent answer value.
     *
     * @param rule        Logic rule AST node
     * @param answerValue Respondent answer value string
     * @return true if logic rule condition matches and skip should trigger
     */
    boolean evaluateRule(LogicRule rule, String answerValue);
}
