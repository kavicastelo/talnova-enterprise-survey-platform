package com.talnova.tesp.orgservice.security;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class SubTreeScopeInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SubTreeScopeInterceptor.class);
    private static final String X_NODE_SCOPE_HEADER = "X-Node-Scope";

    private final OrgNodeRepository repository;

    public SubTreeScopeInterceptor(OrgNodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String nodeScope = request.getHeader(X_NODE_SCOPE_HEADER);
        if (nodeScope == null || nodeScope.trim().isEmpty()) {
            return true;
        }

        String requestURI = request.getRequestURI();
        String projectId = request.getParameter("projectId");

        // Parse target nodeId from URI e.g. /api/v1/nodes/N-201/subtree -> N-201
        String nodeId = extractNodeIdFromPath(requestURI);

        if (nodeId != null && projectId != null) {
            Optional<OrgNodeDocument> nodeOpt = repository.findActiveNodeByNodeId(projectId, nodeId);
            if (nodeOpt.isPresent()) {
                OrgNodeDocument node = nodeOpt.get();
                String scopePrefix = nodeScope.trim();
                if (!node.getPath().startsWith(scopePrefix) && !node.getPath().contains("," + scopePrefix + ",")) {
                    log.warn("ABAC Scope Violation: User scope '{}' attempted unauthorized access to node '{}' (path '{}')",
                            scopePrefix, nodeId, node.getPath());
                    throw new AccessDeniedException("Access Denied: Requested node '" + nodeId + "' is outside authorized sub-tree scope '" + scopePrefix + "'.");
                }
            }
        }

        return true;
    }

    private String extractNodeIdFromPath(String uri) {
        if (uri == null || !uri.contains("/nodes/")) {
            return null;
        }
        String[] parts = uri.substring(uri.indexOf("/nodes/") + 7).split("/");
        if (parts.length > 0 && !parts[0].isEmpty() && !"anomalies".equalsIgnoreCase(parts[0])) {
            return parts[0];
        }
        return null;
    }
}
