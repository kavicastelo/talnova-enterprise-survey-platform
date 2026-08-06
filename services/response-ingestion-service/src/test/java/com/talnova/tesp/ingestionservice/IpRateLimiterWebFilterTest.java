package com.talnova.tesp.ingestionservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.ingestionservice.security.IpRateLimiterWebFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IpRateLimiterWebFilterTest {

    private IpRateLimiterWebFilter filter;
    private WebFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new IpRateLimiterWebFilter(new ObjectMapper());
        filterChain = mock(WebFilterChain.class);
        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("TC-INT-801-04: Allow requests under 10 req/sec limit and return HTTP 429 when limit exceeded")
    void testRateLimiterExceeded() {
        String testIp = "192.168.1.100";

        // First 10 requests should be allowed (HTTP 200 via filter chain)
        for (int i = 0; i < 10; i++) {
            MockServerHttpRequest request = MockServerHttpRequest.post("/api/v1/responses")
                    .remoteAddress(new InetSocketAddress(testIp, 8080))
                    .build();
            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(filter.filter(exchange, filterChain))
                    .verifyComplete();
        }

        verify(filterChain, times(10)).filter(any());

        // 11th request should be blocked with HTTP 429 Too Many Requests
        MockServerHttpRequest blockedRequest = MockServerHttpRequest.post("/api/v1/responses")
                .remoteAddress(new InetSocketAddress(testIp, 8080))
                .build();
        MockServerWebExchange blockedExchange = MockServerWebExchange.from(blockedRequest);

        StepVerifier.create(filter.filter(blockedExchange, filterChain))
                .verifyComplete();

        assertEquals(429, blockedExchange.getResponse().getStatusCode().value());
        verify(filterChain, times(10)).filter(any()); // Should not have increased past 10
    }
}
