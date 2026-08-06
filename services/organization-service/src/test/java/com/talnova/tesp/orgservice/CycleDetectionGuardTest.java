package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.exception.CircularHierarchyException;
import com.talnova.tesp.orgservice.exception.NodeValidationException;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import com.talnova.tesp.orgservice.service.CycleDetectionGuard;
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
class CycleDetectionGuardTest {

    @Mock
    private OrgNodeRepository repository;

    private CycleDetectionGuard cycleGuard;

    @BeforeEach
    void setUp() {
        cycleGuard = new CycleDetectionGuard(repository);
    }

    @Test
    @DisplayName("TC-ORG-002-A: Moving node to a valid non-descendant parent passes validation")
    void testValidMovePasses() {
        OrgNodeDocument newParentDoc = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-102")
                .name("Marketing Division")
                .type("DIVISION")
                .path(",N-001,N-102,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-102")).thenReturn(Optional.of(newParentDoc));

        assertDoesNotThrow(() -> cycleGuard.validateMove("PRJ-99201", "N-201", "N-102"));
    }

    @Test
    @DisplayName("TC-ORG-002-B: Setting node parent to itself throws CircularHierarchyException")
    void testSelfMoveThrowsCircularHierarchyException() {
        CircularHierarchyException ex = assertThrows(CircularHierarchyException.class, () ->
                cycleGuard.validateMove("PRJ-99201", "N-101", "N-101")
        );

        assertTrue(ex.getMessage().contains("cannot be set as its own parent"));
    }

    @Test
    @DisplayName("TC-ORG-002-C: Attempting to move parent node N-101 under child N-201 throws CircularHierarchyException (TC-ORG-002)")
    void testCircularMoveThrowsCircularHierarchyException() {
        // Child N-201 has path ,N-001,N-101,N-201, which contains ,N-101,
        OrgNodeDocument childAsTargetParent = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Software Department")
                .type("DEPARTMENT")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-201")).thenReturn(Optional.of(childAsTargetParent));

        CircularHierarchyException ex = assertThrows(CircularHierarchyException.class, () ->
                cycleGuard.validateMove("PRJ-99201", "N-101", "N-201")
        );

        assertTrue(ex.getMessage().contains("Circular hierarchy move detected"));
    }

    @Test
    @DisplayName("TC-ORG-002-D: Moving under non-existent target parent throws NodeValidationException")
    void testNonExistentParentThrowsValidationException() {
        when(repository.findActiveNodeByNodeId("PRJ-99201", "N-999")).thenReturn(Optional.empty());

        NodeValidationException ex = assertThrows(NodeValidationException.class, () ->
                cycleGuard.validateMove("PRJ-99201", "N-101", "N-999")
        );

        assertTrue(ex.getMessage().contains("Target parent node 'N-999' does not exist"));
    }
}
