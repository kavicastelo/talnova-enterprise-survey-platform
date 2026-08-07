package com.talnova.tesp.actionservice.verification;

public interface ScoreDeltaEvaluatorService {

    /**
     * Calculates score improvement delta (Score_post - BaselineScore) for an Action Plan per FR-ACT-006 and BR-ACT-004.
     */
    double evaluateScoreDelta(String actionPlanId, double postActionScore);
}
