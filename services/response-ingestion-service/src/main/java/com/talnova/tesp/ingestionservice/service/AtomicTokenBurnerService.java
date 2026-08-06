package com.talnova.tesp.ingestionservice.service;

import reactor.core.publisher.Mono;

public interface AtomicTokenBurnerService {

    /**
     * Atomically validates existence of token in Redis and burns (deletes) the key in a single network round-trip (< 1.5ms).
     *
     * @param token Single-use survey token string
     * @return Mono containing cached metadata JSON if valid and burned, or Mono.empty() if non-existent / already burned.
     */
    Mono<String> burnToken(String token);
}
