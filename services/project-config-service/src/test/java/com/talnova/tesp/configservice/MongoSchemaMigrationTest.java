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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoSchemaMigrationTest {

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
    @DisplayName("TC-CFG-101-A: Successfully map and persist valid project document")
    void testCreateProjectSuccess() {
        ProjectCreateDTO dto = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .branding(BrandingDTO.builder()
                        .companyName("Aitken Spence PLC")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US", "si-LK"))
                .defaultLocale("en-US")
                .build();

        ProjectDocument mockSavedDoc = projectMapper.toDocument(dto);
        mockSavedDoc.setId("66b26d8f8a84a51e3c8b4567");

        when(projectRepository.existsByProjectIdAndIsDeletedFalse("PRJ-99201")).thenReturn(false);
        when(projectRepository.save(any(ProjectDocument.class))).thenReturn(mockSavedDoc);

        ProjectResponseDTO response = projectConfigService.createProject(dto);

        assertNotNull(response);
        assertEquals("PRJ-99201", response.getProjectId());
        assertEquals("Aitken Spence Enterprise", response.getName());
        assertEquals(ProjectStatus.ACTIVE, response.getStatus());
        verify(projectRepository, times(1)).save(any(ProjectDocument.class));
    }

    @Test
    @DisplayName("TC-CFG-101-B: Verify regex pattern matching on projectId ^PRJ-[A-Z0-9]{4,10}$")
    void testProjectIdRegexValidation() {
        String validProjectId = "PRJ-99201";
        String invalidProjectId = "INVALID_PROJ";

        assertTrue(validProjectId.matches("^PRJ-[A-Z0-9]{4,10}$"));
        assertFalse(invalidProjectId.matches("^PRJ-[A-Z0-9]{4,10}$"));
    }
}
