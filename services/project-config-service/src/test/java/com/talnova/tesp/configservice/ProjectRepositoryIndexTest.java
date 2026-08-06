package com.talnova.tesp.configservice;

import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.domain.ProjectStatus;
import com.talnova.tesp.configservice.repository.ProjectRepositoryCustomImpl;
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
class ProjectRepositoryIndexTest {

    @Mock
    private MongoOperations mongoOperations;

    private ProjectRepositoryCustomImpl repositoryCustom;

    @BeforeEach
    void setUp() {
        repositoryCustom = new ProjectRepositoryCustomImpl(mongoOperations);
    }

    @Test
    @DisplayName("TC-CFG-102-A: Custom repository query injects isDeleted = false clause")
    void testFindActiveProjectByIdInjectsSoftDeleteFilter() {
        ProjectDocument mockDoc = ProjectDocument.builder()
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .status(ProjectStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(mongoOperations.findOne(any(Query.class), eq(ProjectDocument.class))).thenReturn(mockDoc);

        Optional<ProjectDocument> result = repositoryCustom.findActiveProjectById("PRJ-99201");

        assertTrue(result.isPresent());
        assertEquals("PRJ-99201", result.get().getProjectId());

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoOperations).findOne(queryCaptor.capture(), eq(ProjectDocument.class));

        Query executedQuery = queryCaptor.getValue();
        assertTrue(executedQuery.getQueryObject().toJson().contains("\"isDeleted\": false"));
        assertTrue(executedQuery.getQueryObject().toJson().contains("\"projectId\": \"PRJ-99201\""));
    }

    @Test
    @DisplayName("TC-CFG-102-B: findAllActiveProjects injects isDeleted = false clause")
    void testFindAllActiveProjectsInjectsSoftDeleteFilter() {
        ProjectDocument mockDoc = ProjectDocument.builder()
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .status(ProjectStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(mongoOperations.find(any(Query.class), eq(ProjectDocument.class))).thenReturn(List.of(mockDoc));

        List<ProjectDocument> results = repositoryCustom.findAllActiveProjects();

        assertEquals(1, results.size());
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(mongoOperations).find(queryCaptor.capture(), eq(ProjectDocument.class));

        Query executedQuery = queryCaptor.getValue();
        assertTrue(executedQuery.getQueryObject().toJson().contains("\"isDeleted\": false"));
    }
}
