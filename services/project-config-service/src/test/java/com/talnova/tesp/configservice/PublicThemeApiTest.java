package com.talnova.tesp.configservice;

import com.talnova.tesp.configservice.controller.ProjectConfigController;
import com.talnova.tesp.configservice.dto.PublicThemeDTO;
import com.talnova.tesp.configservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.configservice.service.ProjectConfigService;
import com.talnova.tesp.configservice.validation.WcagColorAccessibilityValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProjectConfigController.class)
@ContextConfiguration(classes = {ProjectConfigController.class, GlobalExceptionHandler.class, WcagColorAccessibilityValidator.class})
@AutoConfigureMockMvc(addFilters = false)
class PublicThemeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectConfigService projectConfigService;

    @Test
    @DisplayName("TC-CFG-302-A: GET /api/v1/projects/{projectId}/public-theme returns 200 OK with Cache-Control header")
    void testGetPublicThemeSuccess() throws Exception {
        PublicThemeDTO themeDTO = PublicThemeDTO.builder()
                .projectId("PRJ-99201")
                .companyName("Aitken Spence PLC")
                .logoUrl("https://s3.amazonaws.com/logo.png")
                .primaryColor("#1E3A8A")
                .secondaryColor("#3B82F6")
                .defaultLocale("en-US")
                .build();

        when(projectConfigService.getPublicTheme("PRJ-99201")).thenReturn(themeDTO);

        mockMvc.perform(get("/api/v1/projects/PRJ-99201/public-theme"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "public, max-age=60"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.projectId").value("PRJ-99201"))
                .andExpect(jsonPath("$.data.companyName").value("Aitken Spence PLC"))
                .andExpect(jsonPath("$.data.primaryColor").value("#1E3A8A"))
                .andExpect(jsonPath("$.data.defaultLocale").value("en-US"));
    }
}
