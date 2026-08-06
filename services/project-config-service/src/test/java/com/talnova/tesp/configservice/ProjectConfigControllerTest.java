package com.talnova.tesp.configservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.configservice.controller.ProjectConfigController;
import com.talnova.tesp.configservice.domain.ProjectStatus;
import com.talnova.tesp.configservice.dto.BrandingDTO;
import com.talnova.tesp.configservice.dto.FeatureFlagsDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.dto.ProjectResponseDTO;
import com.talnova.tesp.configservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.configservice.exception.ProjectNotFoundException;
import com.talnova.tesp.configservice.service.ProjectConfigService;
import com.talnova.tesp.configservice.validation.WcagColorAccessibilityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProjectConfigController.class)
@ContextConfiguration(classes = {ProjectConfigController.class, GlobalExceptionHandler.class, WcagColorAccessibilityValidator.class})
@AutoConfigureMockMvc(addFilters = false)
class ProjectConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectConfigService projectConfigService;

    private ProjectCreateDTO createDTO;
    private ProjectResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .branding(BrandingDTO.builder()
                        .companyName("Aitken Spence PLC")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US", "si-LK"))
                .defaultLocale("en-US")
                .features(FeatureFlagsDTO.builder()
                        .aiAnalyticsEnabled(true)
                        .actionPlanningEnabled(true)
                        .build())
                .build();

        responseDTO = ProjectResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .status(ProjectStatus.ACTIVE)
                .branding(createDTO.getBranding())
                .supportedLocales(createDTO.getSupportedLocales())
                .defaultLocale(createDTO.getDefaultLocale())
                .features(createDTO.getFeatures())
                .version(1)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/projects returns 201 Created with Location header")
    void testCreateProjectApi() throws Exception {
        when(projectConfigService.createProject(any(ProjectCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/PRJ-99201"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value("PRJ-99201"))
                .andExpect(jsonPath("$.data.version").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/projects/{projectId} returns 200 OK")
    void testGetProjectApi() throws Exception {
        when(projectConfigService.getProjectByProjectId("PRJ-99201")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/projects/PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value("PRJ-99201"));
    }

    @Test
    @DisplayName("GET /api/v1/projects/{projectId} for non-existent/soft-deleted project returns 404 Not Found")
    void testGetSoftDeletedProjectReturns404() throws Exception {
        when(projectConfigService.getProjectByProjectId("PRJ-DELETED"))
                .thenThrow(new ProjectNotFoundException("PRJ-DELETED"));

        mockMvc.perform(get("/api/v1/projects/PRJ-DELETED"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Project Workspace Not Found"));
    }

    @Test
    @DisplayName("PUT /api/v1/projects/{projectId} updates project settings and increments version")
    void testUpdateProjectApi() throws Exception {
        ProjectResponseDTO updatedResponse = ProjectResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .name("Aitken Spence Updated")
                .status(ProjectStatus.ACTIVE)
                .version(2)
                .build();

        when(projectConfigService.updateProject(eq("PRJ-99201"), any(ProjectCreateDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/projects/PRJ-99201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.version").value(2))
                .andExpect(jsonPath("$.data.name").value("Aitken Spence Updated"));
    }

    @Test
    @DisplayName("PATCH /api/v1/projects/{projectId}/features updates feature flags")
    void testUpdateFeatureFlagsApi() throws Exception {
        FeatureFlagsDTO featureDTO = FeatureFlagsDTO.builder()
                .aiAnalyticsEnabled(true)
                .actionPlanningEnabled(false)
                .build();

        when(projectConfigService.updateFeatureFlags(eq("PRJ-99201"), any(FeatureFlagsDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/projects/PRJ-99201/features")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(featureDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/projects/{projectId} soft deletes project workspace")
    void testDeleteProjectApi() throws Exception {
        doNothing().when(projectConfigService).deleteProject("PRJ-99201");

        mockMvc.perform(delete("/api/v1/projects/PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project workspace soft deleted"));
    }
}
