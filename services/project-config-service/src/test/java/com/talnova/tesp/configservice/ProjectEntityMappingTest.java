package com.talnova.tesp.configservice;

import com.talnova.tesp.configservice.domain.BrandingConfig;
import com.talnova.tesp.configservice.domain.CustomAttributeDefinition;
import com.talnova.tesp.configservice.domain.DataType;
import com.talnova.tesp.configservice.domain.FeatureFlags;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.domain.ProjectStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityMappingTest {

    @Test
    @DisplayName("TC-CFG-201-A: Verify ProjectDocument domain entity construction and getter/setter integrity")
    void testProjectEntityScaffolding() {
        BrandingConfig branding = BrandingConfig.builder()
                .companyName("Aitken Spence PLC")
                .primaryColor("#1E3A8A")
                .secondaryColor("#3B82F6")
                .logoUrl("https://s3.amazonaws.com/logo.png")
                .build();

        FeatureFlags features = FeatureFlags.builder()
                .aiAnalyticsEnabled(true)
                .actionPlanningEnabled(true)
                .kioskModeEnabled(false)
                .smsDistributionEnabled(true)
                .build();

        CustomAttributeDefinition customAttr = CustomAttributeDefinition.builder()
                .key("department")
                .displayName("Department Name")
                .dataType(DataType.STRING)
                .allowedValues(List.of("Engineering", "HR", "Sales"))
                .build();

        ProjectDocument doc = ProjectDocument.builder()
                .id("66b26d8f8a84a51e3c8b4567")
                .projectId("PRJ-99201")
                .name("Aitken Spence Workspace")
                .status(ProjectStatus.ACTIVE)
                .branding(branding)
                .supportedLocales(List.of("en-US", "si-LK"))
                .defaultLocale("en-US")
                .features(features)
                .customAttributeDefinitions(List.of(customAttr))
                .version(1)
                .isDeleted(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        assertNotNull(doc);
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("Aitken Spence Workspace", doc.getName());
        assertEquals(ProjectStatus.ACTIVE, doc.getStatus());
        assertEquals("Aitken Spence PLC", doc.getBranding().getCompanyName());
        assertTrue(doc.getFeatures().isAiAnalyticsEnabled());
        assertFalse(doc.getFeatures().isKioskModeEnabled());
        assertEquals(1, doc.getCustomAttributeDefinitions().size());
        assertEquals("department", doc.getCustomAttributeDefinitions().get(0).getKey());
        assertFalse(doc.isDeleted());
    }
}
