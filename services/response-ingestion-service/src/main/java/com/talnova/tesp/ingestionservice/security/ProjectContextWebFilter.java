package com.talnova.tesp.ingestionservice.security;

import com.talnova.tesp.common.context.ProjectContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(-100)
public class ProjectContextWebFilter implements WebFilter {

    private static final String HEADER_PROJECT_ID = "X-Project-ID";
    private static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    private static final String HEADER_USER_ID = "X-User-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        String projectId = headers.getFirst(HEADER_PROJECT_ID);
        String correlationId = headers.getFirst(HEADER_CORRELATION_ID);
        String userId = headers.getFirst(HEADER_USER_ID);

        if (projectId != null && !projectId.isBlank()) {
            ProjectContextHolder.setProjectId(projectId);
        }
        if (correlationId != null && !correlationId.isBlank()) {
            ProjectContextHolder.setCorrelationId(correlationId);
        }
        if (userId != null && !userId.isBlank()) {
            ProjectContextHolder.setUserId(userId);
        }

        return chain.filter(exchange)
                .doFinally(signalType -> ProjectContextHolder.clear());
    }
}
