package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.service.RedisTokenCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SingleUseTokenBurnTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    private ObjectMapper objectMapper;
    private RedisTokenCacheServiceImpl redisTokenCacheService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        redisTokenCacheService = new RedisTokenCacheServiceImpl(redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("TC-DST-302-01: Atomic Lua script burns active single-use token in Redis (BR-DST-001)")
    void testAtomicTokenBurnSuccess() throws Exception {
        TokenCachePayloadDTO payload = TokenCachePayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .build();

        String jsonPayload = objectMapper.writeValueAsString(payload);

        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(List.of("tesp:tokens:TKN-ACTIVE-101"))))
                .thenReturn(jsonPayload);

        Optional<TokenCachePayloadDTO> burnedOpt = redisTokenCacheService.validateAndBurnToken("TKN-ACTIVE-101");

        assertTrue(burnedOpt.isPresent());
        assertEquals("PRJ-99201", burnedOpt.get().getProjectId());
        assertEquals("CMP-1001", burnedOpt.get().getCampaignId());
    }

    @Test
    @DisplayName("TC-DST-302-02: Re-burning an already burned or expired token returns empty Optional")
    void testReburningTokenReturnsEmpty() {
        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(List.of("tesp:tokens:TKN-BURNED-999"))))
                .thenReturn(null);

        Optional<TokenCachePayloadDTO> burnedOpt = redisTokenCacheService.validateAndBurnToken("TKN-BURNED-999");

        assertFalse(burnedOpt.isPresent(), "Second burn attempt must return empty Optional");
    }
}
