package com.talnova.tesp.analyticsservice.scoring;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnpsCalculatorServiceImpl implements EnpsCalculatorService {

    @Override
    public EnpsResult calculateEnps(List<Double> npsScores) {
        if (npsScores == null || npsScores.isEmpty()) {
            return EnpsResult.builder()
                    .totalResponses(0)
                    .promoterCount(0)
                    .passiveCount(0)
                    .detractorCount(0)
                    .promoterPercentage(0.0)
                    .detractorPercentage(0.0)
                    .enpsScore(0.0)
                    .build();
        }

        int promoters = 0;
        int passives = 0;
        int detractors = 0;

        for (Double score : npsScores) {
            if (score == null) continue;

            if (score >= 9.0) {
                promoters++;
            } else if (score >= 7.0) {
                passives++;
            } else if (score >= 0.0) {
                detractors++;
            }
        }

        int total = promoters + passives + detractors;
        if (total == 0) {
            return EnpsResult.builder()
                    .totalResponses(0)
                    .promoterCount(0)
                    .passiveCount(0)
                    .detractorCount(0)
                    .promoterPercentage(0.0)
                    .detractorPercentage(0.0)
                    .enpsScore(0.0)
                    .build();
        }

        double promoterPct = ((double) promoters / total) * 100.0;
        double detractorPct = ((double) detractors / total) * 100.0;
        double enps = Math.round((promoterPct - detractorPct) * 10.0) / 10.0;

        return EnpsResult.builder()
                .totalResponses(total)
                .promoterCount(promoters)
                .passiveCount(passives)
                .detractorCount(detractors)
                .promoterPercentage(Math.round(promoterPct * 10.0) / 10.0)
                .detractorPercentage(Math.round(detractorPct * 10.0) / 10.0)
                .enpsScore(enps)
                .build();
    }
}
