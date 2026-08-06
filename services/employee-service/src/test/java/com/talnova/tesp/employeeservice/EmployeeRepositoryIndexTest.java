package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.repository.EmployeeRepositoryCustomImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.query.Query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeRepositoryIndexTest {

    @Mock
    private MongoOperations mongoOperations;

    private EmployeeRepositoryCustomImpl repositoryCustom;

    @BeforeEach
    void setUp() {
        repositoryCustom = new EmployeeRepositoryCustomImpl(mongoOperations);
    }

    @Test
    @DisplayName("TC-EMP-102-A: Verify EmployeeDocument possesses required compound unique and multikey index annotations BR-EMP-001")
    void testEmployeeDocumentCompoundIndexesAnnotations() {
        CompoundIndexes indexesAnnotation = EmployeeDocument.class.getAnnotation(CompoundIndexes.class);
        assertNotNull(indexesAnnotation, "EmployeeDocument must be annotated with @CompoundIndexes");

        CompoundIndex[] indexes = indexesAnnotation.value();
        assertEquals(3, indexes.length, "EmployeeDocument must define 3 compound indexes");

        // Index 1: Unique Compound Index on (projectId, employeeId)
        CompoundIndex uniqProjEmp = indexes[0];
        assertEquals("uniq_proj_emp", uniqProjEmp.name());
        assertEquals("{'projectId': 1, 'employeeId': 1}", uniqProjEmp.def());
        assertTrue(uniqProjEmp.unique());

        // Index 2: Compound Search Index on (projectId, nodeId, status)
        CompoundIndex idxProjNodeStatus = indexes[1];
        assertEquals("idx_proj_node_status", idxProjNodeStatus.name());
        assertEquals("{'projectId': 1, 'nodeId': 1, 'status': 1}", idxProjNodeStatus.def());

        // Index 3: Multikey Index on (projectId, matrixNodeIds)
        CompoundIndex idxProjMatrixNodes = indexes[2];
        assertEquals("idx_proj_matrix_nodes", idxProjMatrixNodes.name());
        assertEquals("{'projectId': 1, 'matrixNodeIds': 1}", idxProjMatrixNodes.def());
    }

    @Test
    @DisplayName("TC-EMP-102-B: Verify EmployeeRepositoryCustomImpl builds correct Mongo criteria for nodeId and status queries SLA-EMP-02")
    void testCustomQueryConstructionForNodeAndStatus() {
        repositoryCustom.findActiveByNodeIdAndStatus("PRJ-99201", "N-101", EmployeeStatus.ACTIVE);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoOperations).find(queryCaptor.capture(), eq(EmployeeDocument.class));

        Query executedQuery = queryCaptor.getValue();
        String queryString = executedQuery.getQueryObject().toJson();

        assertTrue(queryString.contains("\"projectId\": \"PRJ-99201\""));
        assertTrue(queryString.contains("\"nodeId\": \"N-101\""));
        assertTrue(queryString.contains("\"status\": \"ACTIVE\""));
        assertTrue(queryString.contains("\"isDeleted\": false"));
    }

    @Test
    @DisplayName("TC-EMP-102-C: Verify EmployeeRepositoryCustomImpl builds multikey criteria for matrixNodeIds query")
    void testCustomQueryConstructionForMatrixNodes() {
        repositoryCustom.findActiveByMatrixNodeId("PRJ-99201", "N-301");

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoOperations).find(queryCaptor.capture(), eq(EmployeeDocument.class));

        Query executedQuery = queryCaptor.getValue();
        String queryString = executedQuery.getQueryObject().toJson();

        assertTrue(queryString.contains("\"projectId\": \"PRJ-99201\""));
        assertTrue(queryString.contains("matrixNodeIds"));
        assertTrue(queryString.contains("N-301"));
        assertTrue(queryString.contains("\"isDeleted\": false"));
    }
}
