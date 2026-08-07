package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.cache.AnalyticsCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsCacheServiceTest {

    @Mock
    private RedisOperations<String, String> redisOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private AnalyticsCacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new AnalyticsCacheServiceImpl(redisOperations);
    }

    @Test
    @DisplayName("TC-ANL-102-01: Deterministic SHA-256 filter hash generator produces equal hashes regardless of query param insertion order")
    void testGenerateFilterHash() {
        Map<String, String> map1 = Map.of("Tenure", "1-3 Years", "Gender", "Female");
        Map<String, String> map2 = Map.of("Gender", "Female", "Tenure", "1-3 Years");

        String hash1 = cacheService.generateFilterHash(map1);
        String hash2 = cacheService.generateFilterHash(map2);

        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Filter parameters must produce identical SHA-256 hash regardless of order");
    }

    @Test
    @DisplayName("TC-ANL-102-02: Throttled cache invalidation suppresses subsequent invalidations within 30-second window per OQ-ANL-002")
    void testThrottledInvalidation() {
        when(redisOperations.keys(anyString())).thenReturn(Set.of("tesp:analytics:PRJ-99201:CMP-1001:DEFAULT"));

        // First call triggers invalidation
        cacheService.invalidateCampaignCacheThrottled("PRJ-99201", "CMP-1001");
        verify(redisOperations, times(1)).delete(anySet());

        // Immediate second call is suppressed due to 30-second throttle
        cacheService.invalidateCampaignCacheThrottled("PRJ-99201", "CMP-1001");
        verify(redisOperations, times(1)).delete(anySet()); // total invocations remains 1
    }
}
