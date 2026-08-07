package com.talnova.tesp.aiservice.quota;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class TokenQuotaManagerServiceImpl implements TokenQuotaManagerService {

    private static final Logger log = LoggerFactory.getLogger(TokenQuotaManagerServiceImpl.class);
    private static final Duration QUOTA_KEY_TTL = Duration.ofDays(1);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RedisOperations<String, String> redisTemplate;

    public TokenQuotaManagerServiceImpl(RedisOperations<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void consumeTokens(String projectId, int tokenCount) {
        if (projectId == null || tokenCount <= 0) return;
        String key = buildQuotaKey(projectId);
        try {
            Long newTotal = redisTemplate.opsForValue().increment(key, tokenCount);
            if (newTotal != null && newTotal == tokenCount) {
                redisTemplate.expire(key, QUOTA_KEY_TTL);
            }
            log.debug("Consumed {} LLM tokens for project {}. New daily total: {}", tokenCount, projectId, newTotal);
        } catch (Exception e) {
            log.error("Failed to update Redis token quota counter for key {}", key, e);
        }
    }

    @Override
    public boolean isQuotaExceeded(String projectId, int dailyLimitTokens) {
        long currentUsage = getCurrentTokenUsage(projectId);
        boolean exceeded = currentUsage >= dailyLimitTokens;
        if (exceeded) {
            log.warn("Daily LLM token budget limit reached for project {} (Usage: {} / Limit: {}) per BR-AI-002",
                    projectId, currentUsage, dailyLimitTokens);
        }
        return exceeded;
    }

    @Override
    public long getRemainingTokens(String projectId, int dailyLimitTokens) {
        long currentUsage = getCurrentTokenUsage(projectId);
        return Math.max(0, dailyLimitTokens - currentUsage);
    }

    private long getCurrentTokenUsage(String projectId) {
        if (projectId == null) return 0;
        String key = buildQuotaKey(projectId);
        try {
            String val = redisTemplate.opsForValue().get(key);
            return val != null ? Long.parseLong(val) : 0;
        } catch (Exception e) {
            log.error("Failed to fetch Redis token quota for key {}", key, e);
            return 0;
        }
    }

    private String buildQuotaKey(String projectId) {
        String todayStr = LocalDate.now().format(DATE_FORMATTER);
        return "tesp:ai:quota:" + projectId + ":" + todayStr;
    }
}
