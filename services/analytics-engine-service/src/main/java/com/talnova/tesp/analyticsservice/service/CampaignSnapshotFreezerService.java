package com.talnova.tesp.analyticsservice.service;

import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import com.talnova.tesp.analyticsservice.event.CampaignClosedEvent;

public interface CampaignSnapshotFreezerService {

    /**
     * Consumes campaign.closed Kafka event, calculates final aggregates,
     * freezes analytical snapshot document in MongoDB, and flushes Redis cache keys.
     *
     * @param event Campaign closure event details
     * @return Immutable frozen AnalyticalSnapshotDocument persisted to MongoDB.
     */
    AnalyticalSnapshotDocument freezeCampaignSnapshot(CampaignClosedEvent event);
}
