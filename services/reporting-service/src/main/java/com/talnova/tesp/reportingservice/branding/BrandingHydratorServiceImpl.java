package com.talnova.tesp.reportingservice.branding;

import com.talnova.tesp.reportingservice.dto.ProjectBrandingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BrandingHydratorServiceImpl implements BrandingHydratorService {

    private static final Logger log = LoggerFactory.getLogger(BrandingHydratorServiceImpl.class);

    private final Map<String, ProjectBrandingDTO> brandingStore = new ConcurrentHashMap<>();

    public BrandingHydratorServiceImpl() {
        // Pre-seed sample corporate white-label profiles
        brandingStore.put("PRJ-99201", ProjectBrandingDTO.builder()
                .projectId("PRJ-99201")
                .companyName("Acme Global Enterprise")
                .logoUrl("https://cdn.talnova.com/branding/acme-logo.png")
                .primaryColorHex("#0F172A")
                .secondaryColorHex("#2563EB")
                .fontFamily("Inter, sans-serif")
                .footerText("Confidential & Proprietary - Acme Global Enterprise Executive Board Briefing")
                .build());
    }

    @Override
    public ProjectBrandingDTO hydrateBranding(String projectId) {
        log.info("Hydrating project branding metadata for projectId '{}' per FR-RPT-004", projectId);

        ProjectBrandingDTO branding = brandingStore.get(projectId);
        if (branding != null) {
            log.info("Loaded custom white-label profile for project '{}': companyName='{}'", projectId, branding.getCompanyName());
            return branding;
        }

        log.info("No custom profile found for project '{}'. Falling back to default Talnova branding", projectId);
        return ProjectBrandingDTO.builder()
                .projectId(projectId)
                .companyName("Talnova Enterprise")
                .logoUrl("https://cdn.talnova.com/assets/talnova-logo.png")
                .primaryColorHex("#1E3A8A")
                .secondaryColorHex("#3B82F6")
                .fontFamily("Roboto, sans-serif")
                .footerText("Confidential - Talnova Survey Platform Report")
                .build();
    }
}
