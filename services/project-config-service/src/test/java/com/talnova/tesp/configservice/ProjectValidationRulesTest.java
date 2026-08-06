package com.talnova.tesp.configservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.configservice.controller.ProjectConfigController;
import com.talnova.tesp.configservice.dto.BrandingDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.configservice.service.ProjectConfigService;
import com.talnova.tesp.configservice.validation.WcagColorAccessibilityValidator;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectConfigController.class)
@ContextConfiguration(classes = {ProjectConfigController.class, GlobalExceptionHandler.class, WcagColorAccessibilityValidator.class})
@AutoConfigureMockMvc(addFilters = false)
class ProjectValidationRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectConfigService projectConfigService;

    @Test
    @DisplayName("VR-CFG-002: Invalid HEX color format returns HTTP 400 Bad Request with RFC 7807 detail")
    void testInvalidHexColorValidation() throws Exception {
        ProjectCreateDTO invalidColorDto = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Test Project")
                .branding(BrandingDTO.builder()
                        .companyName("Test Company")
                        .primaryColor("blue") // Invalid HEX format
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidColorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Payload"))
                .andExpect(jsonPath("$.errors['branding.primaryColor']").value("Invalid HEX color format"));
    }

    @Test
    @DisplayName("VR-CFG-001: Invalid projectId regex pattern returns HTTP 400 Bad Request")
    void testInvalidProjectIdValidation() throws Exception {
        ProjectCreateDTO invalidProjectIdDto = ProjectCreateDTO.builder()
                .projectId("INVALID_ID") // Missing ^PRJ- prefix and uppercase match
                .name("Test Project")
                .branding(BrandingDTO.builder()
                        .companyName("Test Company")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProjectIdDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Payload"))
                .andExpect(jsonPath("$.errors['projectId']").value("projectId must match pattern ^PRJ-[A-Z0-9]{4,10}$"));
    }

    @Test
    @DisplayName("BR-CFG-002: Default locale missing from supportedLocales list returns HTTP 400")
    void testDefaultLocaleValidation() throws Exception {
        ProjectCreateDTO localeMismatchDto = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Test Project")
                .branding(BrandingDTO.builder()
                        .companyName("Test Company")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("si-LK", "ta-LK")) // en-US missing
                .defaultLocale("en-US")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localeMismatchDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Payload"));
    }
}
