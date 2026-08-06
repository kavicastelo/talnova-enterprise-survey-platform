package com.talnova.tesp.configservice.validation;

import com.talnova.tesp.configservice.dto.ContrastValidationResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class WcagColorAccessibilityValidator {

    public ContrastValidationResponseDTO validateColorContrast(String hexColor1, String hexColor2) {
        double luminance1 = calculateRelativeLuminance(hexColor1);
        double luminance2 = calculateRelativeLuminance(hexColor2);

        double lighter = Math.max(luminance1, luminance2);
        double darker = Math.min(luminance1, luminance2);

        double ratio = (lighter + 0.05) / (darker + 0.05);
        double roundedRatio = BigDecimal.valueOf(ratio)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        boolean passed = roundedRatio >= 4.5;
        String wcagLevel;
        String recommendation;

        if (roundedRatio >= 7.0) {
            wcagLevel = "AAA";
            recommendation = "Color contrast ratio meets WCAG 2.1 AA and AAA standards (minimum 7:1 ratio achieved).";
        } else if (roundedRatio >= 4.5) {
            wcagLevel = "AA";
            recommendation = "Color contrast ratio meets WCAG 2.1 AA standards for normal text (minimum 4.5:1 ratio achieved).";
        } else {
            wcagLevel = "FAIL";
            recommendation = String.format("Color contrast ratio of %.2f:1 fails WCAG 2.1 AA requirement (minimum 4.5:1). Darken the text color or lighten the background color to improve legibility for visual impairments.", roundedRatio);
        }

        return ContrastValidationResponseDTO.builder()
                .passed(passed)
                .contrastRatio(roundedRatio)
                .wcagLevel(wcagLevel)
                .recommendation(recommendation)
                .build();
    }

    public double calculateRelativeLuminance(String hexColor) {
        String cleanHex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
        int r = Integer.parseInt(cleanHex.substring(0, 2), 16);
        int g = Integer.parseInt(cleanHex.substring(2, 4), 16);
        int b = Integer.parseInt(cleanHex.substring(4, 6), 16);

        double rLinear = linearizeSrgb(r / 255.0);
        double gLinear = linearizeSrgb(g / 255.0);
        double bLinear = linearizeSrgb(b / 255.0);

        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear;
    }

    private double linearizeSrgb(double c) {
        if (c <= 0.04045) {
            return c / 12.92;
        } else {
            return Math.pow((c + 0.055) / 1.055, 2.4);
        }
    }
}
