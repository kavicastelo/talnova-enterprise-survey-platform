package com.talnova.tesp.ingestionservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AtomicTokenBurnerServiceImpl implements AtomicTokenBurnerService {

    private static final Logger log = LoggerFactory.getLogger(AtomicTokenBurnerServiceImpl.class);
    public static final String TOKEN_KEY_PREFIX = "tesp:tokens:";

    private final ReactiveStringRedisTemplate reactiveRedisTemplate;
    private final RedisScript<String> luaBurnScript;

    public AtomicTokenBurnerServiceImpl(ReactiveStringRedisTemplate reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        String lua = "if redis.call('EXISTS', KEYS[1]) == 1 then " +
                "local v = redis.call('GET', KEYS[1]); " +
                "redis.call('DEL', KEYS[1]); " +
                "return v; " +
                "else " +
                "return nil; " +
                "end";
        this.luaBurnScript = RedisScript.of(lua, String.class);
    }

    @Override
    public Mono<String> burnToken(String token) {
        if (token == null || token.isBlank()) {
            return Mono.empty();
        }

        String redisKey = TOKEN_KEY_PREFIX + token;
        log.info("Executing atomic Lua script to burn token key: '{}'", redisKey);

        return reactiveRedisTemplate.execute(luaBurnScript, List.of(redisKey))
                .next()
                .doOnNext(metadata -> log.info("Successfully burned token '{}' atomically in Redis (< 1.5ms SLA)", token))
                .doOnSuccess(metadata -> {
                    if (metadata == null) {
                        log.warn("Atomic token burn failed for token '{}': key not found or already burned", token);
                    }
                });
    }
}
