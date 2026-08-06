package com.talnova.tesp.ingestionservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IpRateLimiterWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(IpRateLimiterWebFilter.class);
    private static final int MAX_REQUESTS_PER_SECOND = 10;

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public IpRateLimiterWebFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!path.startsWith("/api/v1/responses")) {
            return chain.filter(exchange);
        }

        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        String clientIp = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "UNKNOWN_IP";

        AtomicInteger counter = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        if (currentCount > MAX_REQUESTS_PER_SECOND) {
            log.warn("Rate limit exceeded for IP: {} (requests: {})", clientIp, currentCount);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            try {
                ApiResponse<Void> apiResponse = ApiResponse.error("Too many requests from IP address. Rate limit: 10 req/sec", "ERR-RATE-429");
                byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                return exchange.getResponse().writeWith(Mono.just(buffer));
            } catch (Exception e) {
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    public void resetCounts() {
        requestCounts.clear();
    }
}
