package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.exception.NodeValidationException;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import com.talnova.tesp.orgservice.service.PathCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgNodePathComputationTest {

    @Mock
    private OrgNodeRepository repository;

    private PathCalculatorService pathCalculatorService;

    @BeforeEach
    void setUp() {
        pathCalculatorService = new PathCalculatorService(repository);
    }

    @Test
    @DisplayName("TC-ORG-001-A: Root node creation calculates root path and depth = 1")
    void testRootNodePathComputation() {
        PathCalculatorService.ComputedPathResult result = pathCalculatorService.calculatePathAndDepth("PRJ-99201", "N-001", null);

        assertNotNull(result);
        assertEquals(",N-001,", result.getPath());
        assertEquals(1, result.getDepth());
    }

    @Test
    @DisplayName("TC-ORG-001-B: Child node creation under parent calculates path concatenation and depth increment")
    void testChildNodePathComputation() {
        OrgNodeDocument parentDoc = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-101")
                .name("Engineering Division")
                .type("DIVISION")
                .path(",N-001,N-101,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-101")).thenReturn(Optional.of(parentDoc));

        PathCalculatorService.ComputedPathResult result = pathCalculatorService.calculatePathAndDepth("PRJ-99201", "N-201", "N-101");

        assertNotNull(result);
        assertEquals(",N-001,N-101,N-201,", result.getPath());
        assertEquals(3, result.getDepth());
    }

    @Test
    @DisplayName("TC-ORG-001-C: Non-existent parent node throws NodeValidationException")
    void testNonExistentParentThrowsValidationException() {
        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-999")).thenReturn(Optional.empty());

        NodeValidationException ex = assertThrows(NodeValidationException.class, () ->
                pathCalculatorService.calculatePathAndDepth("PRJ-99201", "N-201", "N-999")
        );

        assertTrue(ex.getMessage().contains("Parent node 'N-999' does not exist"));
    }
}
