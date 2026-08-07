package com.talnova.tesp.analyticsservice.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnalyticsCacheServiceImpl implements AnalyticsCacheService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsCacheServiceImpl.class);
    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private static final long THROTTLE_INTERVAL_SECONDS = 30;

    private final RedisOperations<String, String> redisTemplate;
    private final Map<String, Instant> lastInvalidationTimes = new ConcurrentHashMap<>();

    public AnalyticsCacheServiceImpl(RedisOperations<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateFilterHash(Map<String, String> filterParams) {
        if (filterParams == null || filterParams.isEmpty()) {
            return "DEFAULT";
        }

        TreeMap<String, String> sortedMap = new TreeMap<>(filterParams);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to compute SHA-256 filter hash", e);
            return "DEFAULT";
        }
    }

    @Override
    public Optional<String> getCachedAnalytics(String projectId, String campaignId, String filterHash) {
        String key = buildCacheKey(projectId, campaignId, filterHash);
        try {
            String value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Redis get failed for key {}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void cacheAnalytics(String projectId, String campaignId, String filterHash, String jsonPayload) {
        String key = buildCacheKey(projectId, campaignId, filterHash);
        try {
            redisTemplate.opsForValue().set(key, jsonPayload, CACHE_TTL);
            log.debug("Cached analytics in Redis for key {}", key);
        } catch (Exception e) {
            log.error("Redis set failed for key {}", key, e);
        }
    }

    @Override
    public void invalidateCampaignCacheThrottled(String projectId, String campaignId) {
        String cachePatternKey = projectId + ":" + campaignId;
        Instant now = Instant.now();
        Instant lastTime = lastInvalidationTimes.get(cachePatternKey);

        if (lastTime != null && Duration.between(lastTime, now).getSeconds() < THROTTLE_INTERVAL_SECONDS) {
            log.info("Suppressed cache invalidation for campaign '{}' due to 30s throttling window (OQ-ANL-002)", campaignId);
            return;
        }

        lastInvalidationTimes.put(cachePatternKey, now);

        String pattern = "tesp:analytics:" + projectId + ":" + campaignId + ":*";
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Invalidated {} analytics cache keys for campaign pattern '{}'", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Failed to invalidate campaign cache keys for pattern {}", pattern, e);
        }
    }

    private String buildCacheKey(String projectId, String campaignId, String filterHash) {
        return "tesp:analytics:" + projectId + ":" + campaignId + ":" + (filterHash != null ? filterHash : "DEFAULT");
    }
}
