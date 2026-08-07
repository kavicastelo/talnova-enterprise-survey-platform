package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.branding.BrandingHydratorServiceImpl;
import com.talnova.tesp.reportingservice.dto.ProjectBrandingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrandingHydratorTest {

    private BrandingHydratorServiceImpl brandingService;

    @BeforeEach
    void setUp() {
        brandingService = new BrandingHydratorServiceImpl();
    }

    @Test
    @DisplayName("TC-RPT-301-01: Hydrate custom white-label corporate profile for project PRJ-99201")
    void testHydrateCustomBranding() {
        ProjectBrandingDTO branding = brandingService.hydrateBranding("PRJ-99201");

        assertNotNull(branding);
        assertEquals("Acme Global Enterprise", branding.getCompanyName());
        assertEquals("#0F172A", branding.getPrimaryColorHex());
        assertEquals("#2563EB", branding.getSecondaryColorHex());
        assertTrue(branding.getLogoUrl().contains("acme-logo.png"));
    }

    @Test
    @DisplayName("TC-RPT-301-02: Fallback to default Talnova branding when project profile is unconfigured")
    void testHydrateFallbackBranding() {
        ProjectBrandingDTO branding = brandingService.hydrateBranding("PRJ-UNKNOWN");

        assertNotNull(branding);
        assertEquals("Talnova Enterprise", branding.getCompanyName());
        assertEquals("#1E3A8A", branding.getPrimaryColorHex());
    }
}
