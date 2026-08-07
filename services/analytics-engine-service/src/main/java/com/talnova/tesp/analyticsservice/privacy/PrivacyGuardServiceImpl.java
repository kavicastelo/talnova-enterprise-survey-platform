package com.talnova.tesp.analyticsservice.privacy;

import com.talnova.tesp.analyticsservice.domain.model.NodeAggregate;
import com.talnova.tesp.analyticsservice.domain.model.NodeAggregateStatus;
import com.talnova.tesp.analyticsservice.dto.HeatmapCellDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PrivacyGuardServiceImpl implements PrivacyGuardService {

    private static final Logger log = LoggerFactory.getLogger(PrivacyGuardServiceImpl.class);

    @Override
    public NodeAggregate sanitizeNodeAggregate(NodeAggregate rawAggregate) {
        if (rawAggregate == null) return null;

        int responseCount = rawAggregate.getResponseCount() != null ? rawAggregate.getResponseCount() : 0;

        if (responseCount < MIN_SAMPLE_SIZE_THRESHOLD) {
            log.debug("Suppressing node aggregate for node '{}' due to small sample size (N = {} < 5)",
                    rawAggregate.getNodeId(), responseCount);
            return NodeAggregate.builder()
                    .nodeId(rawAggregate.getNodeId())
                    .nodePath(rawAggregate.getNodePath())
                    .responseCount(responseCount)
                    .status(NodeAggregateStatus.SUPPRESSED)
                    .enps(null)
                    .engagementIndex(null)
                    .build();
        }

        return NodeAggregate.builder()
                .nodeId(rawAggregate.getNodeId())
                .nodePath(rawAggregate.getNodePath())
                .responseCount(responseCount)
                .status(NodeAggregateStatus.VALID)
                .enps(rawAggregate.getEnps())
                .engagementIndex(rawAggregate.getEngagementIndex())
                .build();
    }

    @Override
    public HeatmapCellDTO sanitizeHeatmapCell(HeatmapCellDTO rawCell) {
        if (rawCell == null) return null;

        int sampleSize = rawCell.getSampleSize();

        if (sampleSize < MIN_SAMPLE_SIZE_THRESHOLD) {
            log.debug("Suppressing heatmap cell for node '{}' / group '{}' due to small sample size (N = {} < 5)",
                    rawCell.getNodeId(), rawCell.getGroupId(), sampleSize);
            return HeatmapCellDTO.builder()
                    .nodeId(rawCell.getNodeId())
                    .groupId(rawCell.getGroupId())
                    .sampleSize(sampleSize)
                    .score(null)
                    .colorIntensity("GREY")
                    .build();
        }

        return rawCell;
    }
}
