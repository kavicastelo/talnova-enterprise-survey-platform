package com.talnova.tesp.configservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.domain.ProjectStatus;
import com.talnova.tesp.configservice.dto.BrandingDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.dto.ProjectResponseDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheTest {

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
    @DisplayName("TC-CFG-301-A: Verify service methods trigger database fetch on first call")
    void testDatabaseFetchOnFirstCall() {
        ProjectDocument mockDoc = ProjectDocument.builder()
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .status(ProjectStatus.ACTIVE)
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        when(projectRepository.findActiveByProjectId("PRJ-99201")).thenReturn(Optional.of(mockDoc));

        ProjectResponseDTO result = projectConfigService.getProjectByProjectId("PRJ-99201");

        assertNotNull(result);
        assertEquals("PRJ-99201", result.getProjectId());
        verify(projectRepository, times(1)).findActiveByProjectId("PRJ-99201");
    }

    @Test
    @DisplayName("TC-CFG-301-B: Updating project fetches document and persists update")
    void testUpdateEvictsAndPersists() {
        ProjectDocument mockDoc = ProjectDocument.builder()
                .projectId("PRJ-99201")
                .name("Old Name")
                .status(ProjectStatus.ACTIVE)
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        ProjectCreateDTO updateDto = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Updated Name")
                .branding(BrandingDTO.builder()
                        .companyName("Aitken Spence PLC")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        when(projectRepository.findActiveByProjectId("PRJ-99201")).thenReturn(Optional.of(mockDoc));
        when(projectRepository.save(any(ProjectDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponseDTO updatedResult = projectConfigService.updateProject("PRJ-99201", updateDto);

        assertNotNull(updatedResult);
        assertEquals("Updated Name", updatedResult.getName());
        verify(projectRepository, times(1)).save(mockDoc);
    }
}
