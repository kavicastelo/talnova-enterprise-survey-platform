package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.domain.DemographicSnapshotDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.CompileSnapshotRequestDTO;
import com.talnova.tesp.employeeservice.dto.DemographicSnapshotDTO;
import com.talnova.tesp.employeeservice.repository.DemographicSnapshotRepository;
import com.talnova.tesp.employeeservice.repository.EmployeeRepository;
import com.talnova.tesp.employeeservice.service.DemographicSnapshotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemographicSnapshotTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DemographicSnapshotRepository snapshotRepository;

    @Mock
    private MongoOperations mongoOperations;

    private DemographicSnapshotServiceImpl snapshotService;

    @BeforeEach
    void setUp() {
        snapshotService = new DemographicSnapshotServiceImpl(employeeRepository, snapshotRepository, mongoOperations);
    }

    @Test
    @DisplayName("TC-EMP-401-A: compileSurveySnapshot freezes employee demographics into immutable snapshot document FR-EMP-006")
    void testCompileSurveySnapshotImmutability() {
        HashMap<String, Object> initialAttrs = new HashMap<>();
        initialAttrs.put("TenureYears", 3);
        initialAttrs.put("Department", "Engineering");

        EmployeeDocument employee = EmployeeDocument.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .email("john.doe@aitkenspence.lk")
                .fullName("John Doe")
                .nodeId("N-201")
                .matrixNodeIds(List.of("N-301"))
                .status(EmployeeStatus.ACTIVE)
                .attributes(initialAttrs)
                .isDeleted(false)
                .build();

        when(mongoOperations.find(any(Query.class), eq(EmployeeDocument.class)))
                .thenReturn(List.of(employee));

        when(snapshotRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CompileSnapshotRequestDTO request = new CompileSnapshotRequestDTO("PRJ-99201", "SURV-5001", List.of("EMP-10020"));

        List<DemographicSnapshotDTO> snapshots = snapshotService.compileSurveySnapshot(request);

        assertNotNull(snapshots);
        assertEquals(1, snapshots.size());

        DemographicSnapshotDTO snapshot = snapshots.get(0);
        assertEquals("PRJ-99201", snapshot.getProjectId());
        assertEquals("SURV-5001", snapshot.getSurveyId());
        assertEquals("EMP-10020", snapshot.getEmployeeId());
        assertEquals("N-201", snapshot.getNodeId());

        // Verify captured frozen demographics map
        assertEquals(3, snapshot.getDemographics().get("TenureYears"));
        assertEquals("Engineering", snapshot.getDemographics().get("Department"));
        assertEquals("N-201", snapshot.getDemographics().get("nodeId"));

        // Mutate original employee profile attributes after snapshot creation
        employee.getAttributes().put("TenureYears", 10);
        employee.setNodeId("N-999");

        // Verify snapshot frozen demographics remain unchanged (Immutable per BR-EMP-005)
        assertEquals(3, snapshot.getDemographics().get("TenureYears"), "Snapshot demographics must remain frozen when profile is mutated");
        assertEquals("N-201", snapshot.getDemographics().get("nodeId"), "Snapshot nodeId must remain frozen");
    }
}
