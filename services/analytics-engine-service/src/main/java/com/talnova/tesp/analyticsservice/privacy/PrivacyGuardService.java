package com.talnova.tesp.analyticsservice.privacy;

import com.talnova.tesp.analyticsservice.domain.model.NodeAggregate;
import com.talnova.tesp.analyticsservice.dto.HeatmapCellDTO;

public interface PrivacyGuardService {

    /**
     * Minimum sample size threshold for anonymity compliance (N >= 5 per BR-ANL-001).
     */
    int MIN_SAMPLE_SIZE_THRESHOLD = 5;

    /**
     * Inspects sample size N for a NodeAggregate. If N < 5, suppresses numeric scores and sets status = SUPPRESSED.
     */
    NodeAggregate sanitizeNodeAggregate(NodeAggregate rawAggregate);

    /**
     * Inspects sample size N for a HeatmapCellDTO. If N < 5, suppresses numeric score and sets colorIntensity = GREY.
     */
    HeatmapCellDTO sanitizeHeatmapCell(HeatmapCellDTO rawCell);
}
