package com.talnova.tesp.analyticsservice.ai;

import com.talnova.tesp.analyticsservice.dto.KeyDriverDTO;

import java.util.List;

public interface KeyDriverAnalyzerService {

    /**
     * Performs Multiple Linear Regression (OLS) to calculate relative importance weights of question group themes.
     *
     * @param yTargetScores Dependent variable array (Overall Engagement Scores per response)
     * @param xThemeScores Independent variable matrix (Theme scores per response)
     * @param themeNames List of theme names corresponding to columns of X
     * @return List of KeyDriverDTO objects ranked by normalized importance weight.
     */
    List<KeyDriverDTO> analyzeKeyDrivers(double[] yTargetScores, double[][] xThemeScores, List<String> themeNames);
}
