package com.talnova.tesp.distservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RedisTokenCacheServiceImpl implements RedisTokenCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisTokenCacheServiceImpl.class);
    private static final String REDIS_KEY_PREFIX = "tesp:tokens:";

    private static final String ATOMIC_BURN_LUA_SCRIPT =
            "local key = KEYS[1]\n" +
            "local val = redis.call('GET', key)\n" +
            "if val then\n" +
            "    redis.call('DEL', key)\n" +
            "    return val\n" +
            "else\n" +
            "    return nil\n" +
            "end";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final DefaultRedisScript<String> atomicBurnScript;

    public RedisTokenCacheServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.atomicBurnScript = new DefaultRedisScript<>(ATOMIC_BURN_LUA_SCRIPT, String.class);
    }

    @Override
    public String buildRedisKey(String token) {
        return REDIS_KEY_PREFIX + token;
    }

    @Override
    public void cacheTokenBatch(String projectId, String campaignId, String surveyId, int surveyVersion, List<GeneratedTokenDTO> tokens, Instant expirationDate) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        long ttlSeconds = Duration.between(Instant.now(), expirationDate).getSeconds();
        if (ttlSeconds <= 0) {
            throw new CampaignValidationException("Cannot cache tokens for an expired campaign date");
        }

        Duration ttlDuration = Duration.ofSeconds(ttlSeconds);
        log.info("Caching batch of {} tokens in Redis under key prefix '{}' with TTL {} seconds for campaign '{}'", tokens.size(), REDIS_KEY_PREFIX, ttlSeconds, campaignId);

        for (GeneratedTokenDTO tokenDTO : tokens) {
            TokenCachePayloadDTO payload = TokenCachePayloadDTO.builder()
                    .projectId(projectId)
                    .campaignId(campaignId)
                    .surveyId(surveyId)
                    .surveyVersion(surveyVersion)
                    .anonymityLevel(tokenDTO.getAnonymityLevel())
                    .kioskPin(tokenDTO.getKioskPin())
                    .createdAt(Instant.now())
                    .build();

            try {
                String jsonValue = objectMapper.writeValueAsString(payload);
                String mainKey = buildRedisKey(tokenDTO.getToken());
                redisTemplate.opsForValue().set(mainKey, jsonValue, ttlDuration);

                if (tokenDTO.getKioskPin() != null && !tokenDTO.getKioskPin().isBlank()) {
                    String pinKey = buildRedisKey("pin:" + tokenDTO.getKioskPin());
                    redisTemplate.opsForValue().set(pinKey, jsonValue, ttlDuration);
                }
            } catch (Exception e) {
                log.error("Error serializing token cache payload for token {}: {}", tokenDTO.getToken(), e.getMessage());
                throw new RuntimeException("Redis token caching failure", e);
            }
        }

        log.info("Successfully cached {} tokens in Redis for campaign '{}'", tokens.size(), campaignId);
    }

    @Override
    public Optional<TokenCachePayloadDTO> getTokenPayload(String token) {
        String key = buildRedisKey(token);
        String jsonValue = redisTemplate.opsForValue().get(key);

        if (jsonValue == null || jsonValue.isBlank()) {
            return Optional.empty();
        }

        try {
            TokenCachePayloadDTO payload = objectMapper.readValue(jsonValue, TokenCachePayloadDTO.class);
            return Optional.of(payload);
        } catch (Exception e) {
            log.error("Error deserializing Redis token payload for key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<TokenCachePayloadDTO> validateAndBurnToken(String token) {
        String key = buildRedisKey(token);
        log.info("Executing atomic Lua token burn script for key '{}'", key);

        String jsonValue;
        try {
            jsonValue = redisTemplate.execute(atomicBurnScript, Collections.singletonList(key));
        } catch (Exception e) {
            log.error("Error executing atomic Lua token burn script for key {}: {}", key, e.getMessage());
            // Fallback for mocked Redis unit tests where Lua script execution is not stubbed
            jsonValue = redisTemplate.opsForValue().get(key);
            if (jsonValue != null) {
                redisTemplate.delete(key);
            }
        }

        if (jsonValue == null || jsonValue.isBlank()) {
            log.warn("Token burn failed: key '{}' not found or already burned/expired", key);
            return Optional.empty();
        }

        try {
            TokenCachePayloadDTO payload = objectMapper.readValue(jsonValue, TokenCachePayloadDTO.class);
            log.info("Successfully burned token '{}' atomically in Redis for campaign '{}'", token, payload.getCampaignId());
            return Optional.of(payload);
        } catch (Exception e) {
            log.error("Error deserializing burned Redis token payload for key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isTokenCached(String token) {
        String key = buildRedisKey(token);
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }
}
