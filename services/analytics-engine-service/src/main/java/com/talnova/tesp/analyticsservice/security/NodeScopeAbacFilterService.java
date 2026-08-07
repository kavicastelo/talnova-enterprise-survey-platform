package com.talnova.tesp.analyticsservice.security;

public interface NodeScopeAbacFilterService {

    /**
     * Validates that the requested target node path is within the user's authorized node scope path.
     * Throws UnauthorizedNodeScopeException (HTTP 403) if unauthorized per BR-ANL-004 & VR-ANL-002.
     *
     * @param authorizedNodeScopePath Materialized path authorized for user (e.g. ",N-100,N-201,")
     * @param targetNodePath Requested node path (e.g. ",N-100,N-201,N-305,")
     */
    void validateNodeScopeAccess(String authorizedNodeScopePath, String targetNodePath);

    /**
     * Compiles a regex string for MongoDB match stage filtering ancestor paths by authorized sub-tree prefix.
     */
    String compileAbacMaterializedPathRegex(String authorizedNodeScopePath);
}
