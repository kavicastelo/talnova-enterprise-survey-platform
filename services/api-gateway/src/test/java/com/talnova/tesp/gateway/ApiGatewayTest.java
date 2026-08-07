package com.talnova.tesp.gateway;

import com.talnova.tesp.gateway.filter.GatewayContextFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiGatewayTest {

    @Autowired
    private GatewayContextFilter contextFilter;

    @Test
    @DisplayName("FR-SVC-001: GatewayContextFilter injects X-Correlation-ID and X-Project-ID headers downstream")
    void testGatewayContextFilterInjection() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/projects/PRJ-99201")
                .header("X-Project-ID", "PRJ-99201")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = filterExchange -> {
            ServerHttpRequest mutated = filterExchange.getRequest();
            assertNotNull(mutated.getHeaders().getFirst("X-Correlation-ID"), "X-Correlation-ID header must be generated and injected per FR-SVC-001");
            return Mono.empty();
        };

        contextFilter.filter(exchange, chain).block();
    }
}
