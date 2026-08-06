package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.TokenCachePayloadDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RedisTokenCacheService {

    void cacheTokenBatch(String projectId, String campaignId, String surveyId, int surveyVersion, List<GeneratedTokenDTO> tokens, Instant expirationDate);

    Optional<TokenCachePayloadDTO> getTokenPayload(String token);

    Optional<TokenCachePayloadDTO> validateAndBurnToken(String token);

    boolean isTokenCached(String token);

    String buildRedisKey(String token);
}
