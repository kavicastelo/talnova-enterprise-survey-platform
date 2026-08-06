package com.talnova.tesp.configservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.domain.ProjectStatus;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.exception.DuplicateProjectIdException;
import com.talnova.tesp.configservice.exception.ProjectNotFoundException;
import com.talnova.tesp.configservice.exception.ProjectValidationException;
import com.talnova.tesp.configservice.mapper.ProjectMapper;
import com.talnova.tesp.configservice.repository.OutboxEventRepository;
import com.talnova.tesp.configservice.repository.ProjectRepository;
import com.talnova.tesp.configservice.service.AuditLoggerService;
import com.talnova.tesp.configservice.service.ProjectConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectConfigServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private AuditLoggerService auditLoggerService;

    private ProjectMapper projectMapper;
    private ObjectMapper objectMapper;
    private ProjectConfigServiceImpl projectConfigService;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();
        objectMapper = new ObjectMapper();
        projectConfigService = new ProjectConfigServiceImpl(projectRepository, outboxEventRepository, auditLoggerService, projectMapper, objectMapper);
    }

    @Test
    @DisplayName("Duplicate projectId throws DuplicateProjectIdException")
    void testDuplicateProjectIdThrowsException() {
        ProjectCreateDTO dto = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Existing Workspace")
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        when(projectRepository.existsByProjectIdAndIsDeletedFalse("PRJ-99201")).thenReturn(true);

        assertThrows(DuplicateProjectIdException.class, () -> projectConfigService.createProject(dto));
    }

    @Test
    @DisplayName("Default locale missing from supportedLocales throws ProjectValidationException")
    void testMissingDefaultLocaleThrowsException() {
        ProjectCreateDTO dto = ProjectCreateDTO.builder()
                .projectId("PRJ-99202")
                .name("Invalid Locale Workspace")
                .supportedLocales(List.of("si-LK", "ta-LK"))
                .defaultLocale("en-US")
                .build();

        when(projectRepository.existsByProjectIdAndIsDeletedFalse("PRJ-99202")).thenReturn(false);

        assertThrows(ProjectValidationException.class, () -> projectConfigService.createProject(dto));
    }

    @Test
    @DisplayName("Fetching non-existent projectId throws ProjectNotFoundException")
    void testFetchNonExistentProjectThrowsException() {
        when(projectRepository.findActiveByProjectId("PRJ-99999")).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectConfigService.getProjectByProjectId("PRJ-99999"));
    }

    @Test
    @DisplayName("Soft delete marks isDeleted as true")
    void testSoftDeleteProject() {
        ProjectDocument doc = ProjectDocument.builder()
                .projectId("PRJ-99201")
                .name("Active Project")
                .status(ProjectStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(projectRepository.findActiveByProjectId("PRJ-99201")).thenReturn(Optional.of(doc));

        projectConfigService.deleteProject("PRJ-99201");

        assertTrue(doc.isDeleted());
        verify(projectRepository, times(1)).save(doc);
    }
}
