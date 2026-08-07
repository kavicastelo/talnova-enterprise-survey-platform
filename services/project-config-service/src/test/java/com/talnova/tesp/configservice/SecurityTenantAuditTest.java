package com.talnova.tesp.configservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.configservice.dto.BrandingDTO;
import com.talnova.tesp.configservice.dto.FeatureFlagsDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTenantAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.talnova.tesp.configservice.repository.ProjectRepository projectRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.talnova.tesp.configservice.repository.OutboxEventRepository outboxEventRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.talnova.tesp.configservice.repository.AuditLogRepository auditLogRepository;

    @Test
    @DisplayName("VR-CFG-003 Guard: Non-HTTPS Logo URL raises 400 Bad Request")
    void testNonHttpsLogoUrlFailsValidation() throws Exception {
        ProjectCreateDTO payload = ProjectCreateDTO.builder()
                .projectId("PRJ-10001")
                .name("Test Project")
                .branding(BrandingDTO.builder()
                        .companyName("Test Corp")
                        .logoUrl("http://insecure-domain.com/logo.png")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['branding.logoUrl']").value("Logo URL must use HTTPS"));
    }

    @Test
    @DisplayName("BR-CFG-001 Guard: Attempting to alter projectId on PUT raises 400 Bad Request")
    void testAlteringProjectIdOnPutFails() throws Exception {
        com.talnova.tesp.configservice.domain.ProjectDocument existingDoc = com.talnova.tesp.configservice.domain.ProjectDocument.builder()
                .projectId("PRJ-99201")
                .name("Original Project")
                .status(com.talnova.tesp.configservice.domain.ProjectStatus.ACTIVE)
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .isDeleted(false)
                .build();
        org.mockito.Mockito.when(projectRepository.findActiveByProjectId("PRJ-99201")).thenReturn(java.util.Optional.of(existingDoc));

        ProjectCreateDTO updatePayload = ProjectCreateDTO.builder()
                .projectId("PRJ-DIFFERENT")
                .name("Updated Name")
                .branding(BrandingDTO.builder()
                        .companyName("Test Corp")
                        .logoUrl("https://s3.amazonaws.com/assets/logo.png")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        mockMvc.perform(put("/api/v1/projects/PRJ-99201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Cannot alter immutable projectId"));
    }

    @Test
    @DisplayName("BR-CFG-003 Guard: PROJECT_ADMIN accessing different tenant raises 403 Forbidden")
    void testTenantScopeMismatchReturns403() throws Exception {
        mockMvc.perform(put("/api/v1/projects/PRJ-TENANT-B")
                        .header("X-User-Role", "PROJECT_ADMIN")
                        .header("X-Project-ID", "PRJ-TENANT-A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Access denied: Tenant scope mismatch"));
    }

    @Test
    @DisplayName("PR-CFG-003 Guard: CONSULTANT_DAASH modifying feature flags raises 403 Forbidden")
    void testConsultantCannotModifyFeatureFlags() throws Exception {
        FeatureFlagsDTO featureFlags = new FeatureFlagsDTO(true, true, true, false);

        mockMvc.perform(patch("/api/v1/projects/PRJ-99201/features")
                        .header("X-User-Role", "CONSULTANT_DAASH")
                        .header("X-Project-ID", "PRJ-99201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(featureFlags)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Consultants cannot modify subscription feature flags"));
    }
}
