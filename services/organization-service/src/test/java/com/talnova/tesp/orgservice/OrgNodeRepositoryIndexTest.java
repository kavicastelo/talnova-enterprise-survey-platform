package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.repository.OrgNodeRepositoryCustomImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgNodeRepositoryIndexTest {

    @Mock
    private MongoOperations mongoOperations;

    private OrgNodeRepositoryCustomImpl repositoryCustom;

    @BeforeEach
    void setUp() {
        repositoryCustom = new OrgNodeRepositoryCustomImpl(mongoOperations);
    }

    @Test
    @DisplayName("TC-ORG-102-A: findActiveNodeByNodeId injects tenant & isDeleted = false filter")
    void testFindActiveNodeByNodeIdInjectsFilters() {
        OrgNodeDocument mockDoc = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-101")
                .name("Engineering Division")
                .type("DIVISION")
                .path(",N-001,N-101,")
                .depth(2)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(mongoOperations.findOne(any(Query.class), eq(OrgNodeDocument.class))).thenReturn(mockDoc);

        Optional<OrgNodeDocument> result = repositoryCustom.findActiveNodeByNodeId("PRJ-99201", "N-101");

        assertTrue(result.isPresent());
        assertEquals("N-101", result.get().getNodeId());

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoOperations).findOne(queryCaptor.capture(), eq(OrgNodeDocument.class));

        Query executedQuery = queryCaptor.getValue();
        String json = executedQuery.getQueryObject().toJson();
        assertTrue(json.contains("\"projectId\": \"PRJ-99201\""));
        assertTrue(json.contains("\"nodeId\": \"N-101\""));
        assertTrue(json.contains("\"isDeleted\": false"));
    }

    @Test
    @DisplayName("TC-ORG-102-B: findActiveSubTreeByPathPrefix executes indexed path prefix regex query")
    void testFindActiveSubTreeByPathPrefixExecutesRegexQuery() {
        OrgNodeDocument mockChild = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Software Department")
                .type("DEPARTMENT")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(mongoOperations.find(any(Query.class), eq(OrgNodeDocument.class))).thenReturn(List.of(mockChild));

        List<OrgNodeDocument> results = repositoryCustom.findActiveSubTreeByPathPrefix("PRJ-99201", ",N-001,N-101,");

        assertEquals(1, results.size());
        assertEquals("N-201", results.get(0).getNodeId());

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoOperations).find(queryCaptor.capture(), eq(OrgNodeDocument.class));

        Query executedQuery = queryCaptor.getValue();
        String json = executedQuery.getQueryObject().toJson();
        assertTrue(json.contains("\"projectId\": \"PRJ-99201\""));
        assertTrue(json.contains("\"isDeleted\": false"));
        assertTrue(json.contains("^,N-001,N-101,"));
    }
}
