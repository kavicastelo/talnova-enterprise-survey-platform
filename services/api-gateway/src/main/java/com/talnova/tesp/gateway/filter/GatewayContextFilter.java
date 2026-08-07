package com.talnova.tesp.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GatewayContextFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayContextFilter.class);

    public static final String HEADER_PROJECT_ID = "X-Project-ID";
    public static final String HEADER_USER_ID = "X-User-ID";
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String projectId = request.getHeaders().getFirst(HEADER_PROJECT_ID);
        String userId = request.getHeaders().getFirst(HEADER_USER_ID);
        String userRoles = request.getHeaders().getFirst(HEADER_USER_ROLES);
        String correlationId = request.getHeaders().getFirst(HEADER_CORRELATION_ID);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "CORR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (projectId == null || projectId.isBlank()) {
            projectId = "PRJ-DEFAULT";
        }

        log.info("API Gateway Context Injection [Path: {}] -> CorrelationID: '{}', ProjectID: '{}', UserID: '{}'",
                request.getURI().getPath(), correlationId, projectId, userId != null ? userId : "ANONYMOUS");

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER_CORRELATION_ID, correlationId)
                .header(HEADER_PROJECT_ID, projectId)
                .header(HEADER_USER_ID, userId != null ? userId : "")
                .header(HEADER_USER_ROLES, userRoles != null ? userRoles : "")
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
