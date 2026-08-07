package com.talnova.tesp.analyticsservice.ai;

import com.talnova.tesp.analyticsservice.dto.KeyDriverDTO;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class KeyDriverAnalyzerServiceImpl implements KeyDriverAnalyzerService {

    private static final Logger log = LoggerFactory.getLogger(KeyDriverAnalyzerServiceImpl.class);

    @Override
    public List<KeyDriverDTO> analyzeKeyDrivers(double[] yTargetScores, double[][] xThemeScores, List<String> themeNames) {
        List<KeyDriverDTO> drivers = new ArrayList<>();

        if (yTargetScores == null || xThemeScores == null || themeNames == null || yTargetScores.length == 0 || xThemeScores.length == 0) {
            return drivers;
        }

        try {
            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            regression.newSampleData(yTargetScores, xThemeScores);

            double[] beta = regression.estimateRegressionParameters();
            double rSquared = regression.calculateRSquared();

            // beta[0] is intercept. beta[1..n] correspond to theme columns
            double absSum = 0.0;
            for (int i = 1; i < beta.length; i++) {
                absSum += Math.abs(beta[i]);
            }

            for (int i = 0; i < themeNames.size() && (i + 1) < beta.length; i++) {
                double rawBeta = beta[i + 1];
                double weight = absSum > 0 ? (Math.abs(rawBeta) / absSum) : 0.0;
                weight = Math.round(weight * 1000.0) / 1000.0;

                String category = categorizeImpact(weight);

                drivers.add(KeyDriverDTO.builder()
                        .themeGroupId("GRP-" + themeNames.get(i).toUpperCase().replaceAll("\\s+", "-"))
                        .themeName(themeNames.get(i))
                        .importanceWeight(weight)
                        .correlationScore(Math.round(rSquared * 100.0) / 100.0)
                        .impactCategory(category)
                        .build());
            }

            drivers.sort(Comparator.comparing(KeyDriverDTO::getImportanceWeight).reversed());
        } catch (Exception e) {
            log.error("Failed to estimate multiple linear regression key drivers", e);
        }

        return drivers;
    }

    private String categorizeImpact(double weight) {
        if (weight > 0.35) {
            return "HIGH_IMPACT";
        } else if (weight >= 0.15) {
            return "MEDIUM_IMPACT";
        } else {
            return "LOW_IMPACT";
        }
    }
}
