package com.talnova.tesp.configservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.common.context.ProjectContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TenantSecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantSecurityFilter.class);
    private static final Pattern PROJECT_PATH_PATTERN = Pattern.compile("^/api/v1/projects/([^/]+)(.*)$");

    public static final String HEADER_USER_ROLE = "X-User-Role";
    public static final String HEADER_PROJECT_ID = "X-Project-ID";
    public static final String HEADER_USER_ID = "X-User-ID";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.endsWith("/public-theme") ||
               path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/api/v1/projects/validate-theme");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String userRole = request.getHeader(HEADER_USER_ROLE);
        String headerProjectId = request.getHeader(HEADER_PROJECT_ID);

        Matcher matcher = PROJECT_PATH_PATTERN.matcher(path);
        String pathProjectId = matcher.matches() ? matcher.group(1) : null;
        String subPath = matcher.matches() ? matcher.group(2) : "";

        if (userRole != null && !userRole.isBlank()) {
            if ("PROJECT_ADMIN".equalsIgnoreCase(userRole) || "CONSULTANT_DAASH".equalsIgnoreCase(userRole)) {
                String assignedProject = headerProjectId != null ? headerProjectId : ProjectContextHolder.getProjectId();

                if (pathProjectId != null && assignedProject != null && !pathProjectId.equalsIgnoreCase(assignedProject)) {
                    log.warn("Tenant isolation violation attempt: User with role {} assigned to {} tried to access {}",
                            userRole, assignedProject, pathProjectId);
                    writeErrorResponse(response, HttpStatus.FORBIDDEN, "Access denied: Tenant scope mismatch", "https://tesp.talnova.com/errors/forbidden");
                    return;
                }

                if ("CONSULTANT_DAASH".equalsIgnoreCase(userRole) && "/features".equalsIgnoreCase(subPath) && "PATCH".equalsIgnoreCase(request.getMethod())) {
                    log.warn("Permission violation attempt: CONSULTANT_DAASH tried to update feature flags for {}", pathProjectId);
                    writeErrorResponse(response, HttpStatus.FORBIDDEN, "Consultants cannot modify subscription feature flags", "https://tesp.talnova.com/errors/forbidden");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String detail, String typeUri) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setType(URI.create(typeUri));
        problemDetail.setProperty("timestamp", Instant.now());

        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }
}
