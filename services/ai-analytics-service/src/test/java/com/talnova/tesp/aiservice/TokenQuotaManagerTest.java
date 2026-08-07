package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.quota.TokenQuotaManagerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenQuotaManagerTest {

    @Mock
    private RedisOperations<String, String> redisOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TokenQuotaManagerServiceImpl quotaManager;

    @BeforeEach
    void setUp() {
        when(redisOperations.opsForValue()).thenReturn(valueOperations);
        quotaManager = new TokenQuotaManagerServiceImpl(redisOperations);
    }

    @Test
    @DisplayName("TC-AI-102-01: Increment Redis token consumption counter")
    void testConsumeTokens() {
        when(valueOperations.increment(anyString(), eq(500L))).thenReturn(500L);

        quotaManager.consumeTokens("PRJ-99201", 500);

        verify(valueOperations, times(1)).increment(anyString(), eq(500L));
    }

    @Test
    @DisplayName("TC-AI-102-02: Check daily token quota threshold per BR-AI-002")
    void testIsQuotaExceeded() {
        when(valueOperations.get(anyString())).thenReturn("100000");

        boolean exceeded = quotaManager.isQuotaExceeded("PRJ-99201", 100_000);
        assertTrue(exceeded, "Quota must be flagged as exceeded when consumption equals daily budget limit");

        long remaining = quotaManager.getRemainingTokens("PRJ-99201", 100_000);
        assertEquals(0, remaining);
    }
}
