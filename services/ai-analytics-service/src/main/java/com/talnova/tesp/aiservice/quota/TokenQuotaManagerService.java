package com.talnova.tesp.aiservice.quota;

public interface TokenQuotaManagerService {

    /**
     * Increments daily token usage counter for target project in Redis (24-hour TTL).
     */
    void consumeTokens(String projectId, int tokenCount);

    /**
     * Checks if project daily token consumption has reached 100% of daily limit per BR-AI-002.
     */
    boolean isQuotaExceeded(String projectId, int dailyLimitTokens);

    /**
     * Returns remaining tokens for target project for today's quota window.
     */
    long getRemainingTokens(String projectId, int dailyLimitTokens);
}
