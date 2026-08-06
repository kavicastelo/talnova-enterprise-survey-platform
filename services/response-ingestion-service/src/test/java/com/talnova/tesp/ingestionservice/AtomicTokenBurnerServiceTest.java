package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.service.AtomicTokenBurnerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtomicTokenBurnerServiceTest {

    @Mock
    private ReactiveStringRedisTemplate reactiveRedisTemplate;

    private AtomicTokenBurnerServiceImpl tokenBurnerService;

    @BeforeEach
    void setUp() {
        tokenBurnerService = new AtomicTokenBurnerServiceImpl(reactiveRedisTemplate);
    }

    @Test
    @DisplayName("TC-INT-201-01: Burn valid single-use token atomically via Redis Lua script")
    void testBurnTokenSuccess() {
        String token = "TKN-VALID-101";
        String redisKey = AtomicTokenBurnerServiceImpl.TOKEN_KEY_PREFIX + token;
        String jsonPayload = "{\"projectId\":\"PRJ-99201\",\"campaignId\":\"CMP-1001\",\"nodeId\":\"N-301\"}";

        when(reactiveRedisTemplate.execute(any(RedisScript.class), eq(List.of(redisKey))))
                .thenReturn(Flux.just(jsonPayload));

        Mono<String> result = tokenBurnerService.burnToken(token);

        StepVerifier.create(result)
                .expectNext(jsonPayload)
                .verifyComplete();

        verify(reactiveRedisTemplate, times(1)).execute(any(RedisScript.class), eq(List.of(redisKey)));
    }

    @Test
    @DisplayName("TC-INT-201-02: Non-existent or already burned token returns empty Mono")
    void testBurnTokenNonExistent() {
        String token = "TKN-BURNED-999";
        String redisKey = AtomicTokenBurnerServiceImpl.TOKEN_KEY_PREFIX + token;

        when(reactiveRedisTemplate.execute(any(RedisScript.class), eq(List.of(redisKey))))
                .thenReturn(Flux.empty());

        Mono<String> result = tokenBurnerService.burnToken(token);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
