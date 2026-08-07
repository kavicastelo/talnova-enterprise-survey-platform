package com.talnova.tesp.reportingservice.branding;

import com.talnova.tesp.reportingservice.dto.ProjectBrandingDTO;

public interface BrandingHydratorService {

    /**
     * Fetches white-label branding variables (logo URL, colors, font family) for the specified project per FR-RPT-004.
     */
    ProjectBrandingDTO hydrateBranding(String projectId);
}
