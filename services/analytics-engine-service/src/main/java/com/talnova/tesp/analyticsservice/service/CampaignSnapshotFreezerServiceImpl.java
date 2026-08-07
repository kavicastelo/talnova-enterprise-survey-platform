package com.talnova.tesp.analyticsservice.service;

import com.talnova.tesp.analyticsservice.cache.AnalyticsCacheService;
import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.event.CampaignClosedEvent;
import com.talnova.tesp.analyticsservice.repository.AnalyticalSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CampaignSnapshotFreezerServiceImpl implements CampaignSnapshotFreezerService {

    private static final Logger log = LoggerFactory.getLogger(CampaignSnapshotFreezerServiceImpl.class);

    private final AnalyticalSnapshotRepository snapshotRepository;
    private final AnalyticsCacheService cacheService;

    public CampaignSnapshotFreezerServiceImpl(AnalyticalSnapshotRepository snapshotRepository,
                                             AnalyticsCacheService cacheService) {
        this.snapshotRepository = snapshotRepository;
        this.cacheService = cacheService;
    }

    @KafkaListener(topics = "tesp.campaigns.events", groupId = "analytics-snapshot-freezer-group")
    public void handleCampaignClosedEvent(CampaignClosedEvent event) {
        if (event == null || event.getCampaignId() == null) return;
        log.info("Received campaign.closed Kafka event for campaignId '{}'", event.getCampaignId());
        freezeCampaignSnapshot(event);
    }

    @Override
    public AnalyticalSnapshotDocument freezeCampaignSnapshot(CampaignClosedEvent event) {
        Instant snapshotTime = event.getClosedAt() != null ? event.getClosedAt() : Instant.now();

        AnalyticalSnapshotDocument snapshot = AnalyticalSnapshotDocument.builder()
                .projectId(event.getProjectId())
                .campaignId(event.getCampaignId())
                .snapshotTimestamp(snapshotTime)
                .totalResponses(event.getTotalResponses())
                .isFrozen(true)
                .nodeAggregates(List.of())
                .build();

        AnalyticalSnapshotDocument savedSnapshot = snapshotRepository.save(snapshot);
        log.info("Persisted frozen AnalyticalSnapshotDocument id '{}' for campaignId '{}'", savedSnapshot.getId(), event.getCampaignId());

        cacheService.invalidateCampaignCacheThrottled(event.getProjectId(), event.getCampaignId());
        return savedSnapshot;
    }
}
