package com.talnova.tesp.analyticsservice.cache;

import java.util.Map;
import java.util.Optional;

public interface AnalyticsCacheService {

    /**
     * Generates a deterministic SHA-256 hash representation of demographic filter parameters.
     */
    String generateFilterHash(Map<String, String> filterParams);

    /**
     * Retrieves cached analytical metrics JSON payload if present in Redis (1-hour TTL).
     */
    Optional<String> getCachedAnalytics(String projectId, String campaignId, String filterHash);

    /**
     * Caches analytical metrics JSON payload in Redis key tesp:analytics:<projectId>:<campaignId>:<filterHash> with 1-hour TTL.
     */
    void cacheAnalytics(String projectId, String campaignId, String filterHash, String jsonPayload);

    /**
     * Throttled cache invalidation for campaign analytics (max once per 30 seconds per OQ-ANL-002).
     */
    void invalidateCampaignCacheThrottled(String projectId, String campaignId);
}
