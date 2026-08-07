package com.talnova.tesp.analyticsservice.scoring;

import com.talnova.tesp.analyticsservice.domain.model.GroupScore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngagementIndexCalculatorServiceImpl implements EngagementIndexCalculatorService {

    @Override
    public double calculateNormalizedScore(double rawLikertScore) {
        // Clamp raw score between 1.0 and 5.0
        double clamped = Math.max(1.0, Math.min(5.0, rawLikertScore));
        double normalized = ((clamped - 1.0) / 4.0) * 100.0;
        return Math.round(normalized * 10.0) / 10.0;
    }

    @Override
    public double calculateMeanEngagementIndex(List<Double> likertScores) {
        if (likertScores == null || likertScores.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;

        for (Double score : likertScores) {
            if (score != null) {
                sum += calculateNormalizedScore(score);
                count++;
            }
        }

        if (count == 0) return 0.0;
        return Math.round((sum / count) * 10.0) / 10.0;
    }

    @Override
    public GroupScore calculateGroupEngagementIndex(String groupId, List<Double> groupLikertScores) {
        double meanIndex = calculateMeanEngagementIndex(groupLikertScores);
        return GroupScore.builder()
                .groupId(groupId)
                .score(meanIndex)
                .build();
    }
}
