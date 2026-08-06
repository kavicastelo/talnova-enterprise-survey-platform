package com.talnova.tesp.common.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class ProjectContextFilter implements Filter {

    public static final String HEADER_PROJECT_ID = "X-Project-ID";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_USER_ID = "X-User-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String projectId = httpRequest.getHeader(HEADER_PROJECT_ID);
            String correlationId = httpRequest.getHeader(HEADER_CORRELATION_ID);
            String userId = httpRequest.getHeader(HEADER_USER_ID);

            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            ProjectContextHolder.setProjectId(projectId);
            ProjectContextHolder.setCorrelationId(correlationId);
            ProjectContextHolder.setUserId(userId);

            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(HEADER_CORRELATION_ID, correlationId);
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            ProjectContextHolder.clear();
        }
    }
}
