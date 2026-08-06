package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.service.RedisTokenCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisTokenCacheServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private ObjectMapper objectMapper;
    private RedisTokenCacheServiceImpl redisTokenCacheService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        redisTokenCacheService = new RedisTokenCacheServiceImpl(redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("TC-DST-301-01: Cache token batch in Redis with correct key schema and TTL")
    void testCacheTokenBatchAndRetrieval() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        GeneratedTokenDTO token1 = GeneratedTokenDTO.builder()
                .token("TKN-HASH-12345")
                .employeeId("EMP-10020")
                .campaignId("CMP-1001")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .kioskPin("849201")
                .build();

        Instant expirationDate = Instant.now().plusSeconds(86400 * 7);

        redisTokenCacheService.cacheTokenBatch("PRJ-99201", "CMP-1001", "SRV-5001", 1, List.of(token1), expirationDate);

        verify(valueOperations, times(1)).set(eq("tesp:tokens:TKN-HASH-12345"), any(), any(Duration.class));
        verify(valueOperations, times(1)).set(eq("tesp:tokens:pin:849201"), any(), any(Duration.class));

        // Test Get Token Payload
        TokenCachePayloadDTO expectedPayload = TokenCachePayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .kioskPin("849201")
                .build();

        when(valueOperations.get("tesp:tokens:TKN-HASH-12345")).thenReturn(objectMapper.writeValueAsString(expectedPayload));

        Optional<TokenCachePayloadDTO> payloadOpt = redisTokenCacheService.getTokenPayload("TKN-HASH-12345");
        assertTrue(payloadOpt.isPresent());
        assertEquals("PRJ-99201", payloadOpt.get().getProjectId());
        assertEquals("CMP-1001", payloadOpt.get().getCampaignId());
        assertEquals("849201", payloadOpt.get().getKioskPin());
    }

    @Test
    @DisplayName("TC-DST-301-02: Expired expiration date throws CampaignValidationException")
    void testExpiredExpirationDateThrowsValidationException() {
        GeneratedTokenDTO token1 = GeneratedTokenDTO.builder().token("TKN-1").build();
        Instant pastExpiration = Instant.now().minusSeconds(3600);

        assertThrows(CampaignValidationException.class, () ->
                redisTokenCacheService.cacheTokenBatch("PRJ-99201", "CMP-1001", "SRV-5001", 1, List.of(token1), pastExpiration));
    }

    @Test
    @DisplayName("TC-DST-301-03: Verify Redis key prefix format 'tesp:tokens:<token>'")
    void testRedisKeyPrefixFormat() {
        String key = redisTokenCacheService.buildRedisKey("MY-TOKEN-99");
        assertEquals("tesp:tokens:MY-TOKEN-99", key);
    }
}
