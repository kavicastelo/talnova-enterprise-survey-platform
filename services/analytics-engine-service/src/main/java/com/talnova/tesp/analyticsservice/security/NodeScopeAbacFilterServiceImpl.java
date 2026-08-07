package com.talnova.tesp.analyticsservice.security;

import com.talnova.tesp.analyticsservice.exception.UnauthorizedNodeScopeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class NodeScopeAbacFilterServiceImpl implements NodeScopeAbacFilterService {

    private static final Logger log = LoggerFactory.getLogger(NodeScopeAbacFilterServiceImpl.class);

    @Override
    public void validateNodeScopeAccess(String authorizedNodeScopePath, String targetNodePath) {
        if (authorizedNodeScopePath == null || authorizedNodeScopePath.isBlank()) {
            return; // Global access role (e.g. EXECUTIVE)
        }

        if (targetNodePath == null || targetNodePath.isBlank()) {
            throw new UnauthorizedNodeScopeException("Unauthorized node scope: Target node path cannot be empty");
        }

        String normalizedAuth = normalizePath(authorizedNodeScopePath);
        String normalizedTarget = normalizePath(targetNodePath);

        if (!normalizedTarget.startsWith(normalizedAuth)) {
            log.warn("Blocked unauthorized node scope access attempt: user scope '{}', target scope '{}'",
                    normalizedAuth, normalizedTarget);
            throw new UnauthorizedNodeScopeException("Unauthorized node scope: Access denied to target node sub-tree path per BR-ANL-004");
        }
    }

    @Override
    public String compileAbacMaterializedPathRegex(String authorizedNodeScopePath) {
        if (authorizedNodeScopePath == null || authorizedNodeScopePath.isBlank()) {
            return "^,";
        }
        String normalized = normalizePath(authorizedNodeScopePath);
        return "^" + Pattern.quote(normalized);
    }

    private String normalizePath(String path) {
        String p = path.trim();
        if (!p.startsWith(",")) p = "," + p;
        if (!p.endsWith(",")) p = p + ",";
        return p;
    }
}
