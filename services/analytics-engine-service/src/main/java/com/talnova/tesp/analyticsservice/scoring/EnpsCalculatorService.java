package com.talnova.tesp.analyticsservice.scoring;

import java.util.List;

public interface EnpsCalculatorService {

    /**
     * Calculates eNPS score = (% Promoters - % Detractors) * 100 for a collection of NPS numeric scores (0-10).
     *
     * @param npsScores List of numeric NPS scores (0.0 to 10.0)
     * @return EnpsResult containing counters and normalized eNPS score (-100.0 to +100.0).
     */
    EnpsResult calculateEnps(List<Double> npsScores);
}
