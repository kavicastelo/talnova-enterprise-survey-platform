package com.talnova.tesp.analyticsservice;

import com.talnova.tesp.analyticsservice.exception.UnauthorizedNodeScopeException;
import com.talnova.tesp.analyticsservice.security.NodeScopeAbacFilterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeScopeAbacFilterServiceTest {

    private NodeScopeAbacFilterServiceImpl filterService;

    @BeforeEach
    void setUp() {
        filterService = new NodeScopeAbacFilterServiceImpl();
    }

    @Test
    @DisplayName("TC-ANL-401-01: Allow access to authorized node sub-tree path")
    void testValidateNodeScopeSuccess() {
        String authPath = ",N-100,N-201,";
        String targetPath = ",N-100,N-201,N-305,";

        assertDoesNotThrow(() -> filterService.validateNodeScopeAccess(authPath, targetPath));
    }

    @Test
    @DisplayName("TC-ANL-401-02: Block unauthorized access to sibling node scope and throw UnauthorizedNodeScopeException")
    void testValidateNodeScopeBlocked() {
        String authPath = ",N-100,N-201,";
        String siblingPath = ",N-100,N-202,N-306,";

        UnauthorizedNodeScopeException ex = assertThrows(UnauthorizedNodeScopeException.class,
                () -> filterService.validateNodeScopeAccess(authPath, siblingPath));

        assertTrue(ex.getMessage().contains("Unauthorized node scope"));
    }

    @Test
    @DisplayName("TC-ANL-401-03: Compile ABAC materialized path regex for MongoDB match stage")
    void testCompileRegex() {
        String regex = filterService.compileAbacMaterializedPathRegex(",N-100,N-201,");
        assertNotNull(regex);
        assertTrue(regex.startsWith("^"));
    }
}
