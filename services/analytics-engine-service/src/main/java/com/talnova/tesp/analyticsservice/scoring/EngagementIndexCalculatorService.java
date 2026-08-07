package com.talnova.tesp.analyticsservice.scoring;

import com.talnova.tesp.analyticsservice.domain.model.GroupScore;

import java.util.List;

public interface EngagementIndexCalculatorService {

    /**
     * Normalizes a single Likert score (1.0 to 5.0) to a 100-point scale.
     * Formula: Index = ((V - 1) / (5 - 1)) * 100
     */
    double calculateNormalizedScore(double rawLikertScore);

    /**
     * Calculates the overall mean 100-point engagement index for a list of Likert scores.
     */
    double calculateMeanEngagementIndex(List<Double> likertScores);

    /**
     * Calculates aggregate GroupScore for a specific Question Group theme.
     */
    GroupScore calculateGroupEngagementIndex(String groupId, List<Double> groupLikertScores);
}
