package com.talnova.tesp.configservice.mapper;

import com.talnova.tesp.configservice.domain.BrandingConfig;
import com.talnova.tesp.configservice.domain.CustomAttributeDefinition;
import com.talnova.tesp.configservice.domain.FeatureFlags;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.domain.ProjectStatus;
import com.talnova.tesp.configservice.dto.BrandingDTO;
import com.talnova.tesp.configservice.dto.CustomAttributeDefDTO;
import com.talnova.tesp.configservice.dto.FeatureFlagsDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.dto.ProjectResponseDTO;
import com.talnova.tesp.configservice.dto.PublicThemeDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    public ProjectDocument toDocument(ProjectCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return ProjectDocument.builder()
                .projectId(dto.getProjectId())
                .name(dto.getName())
                .status(ProjectStatus.ACTIVE)
                .branding(toBrandingConfig(dto.getBranding()))
                .supportedLocales(dto.getSupportedLocales())
                .defaultLocale(dto.getDefaultLocale())
                .features(toFeatureFlags(dto.getFeatures()))
                .customAttributeDefinitions(toCustomAttributeDefinitions(dto.getCustomAttributeDefinitions()))
                .version(1)
                .isDeleted(false)
                .build();
    }

    public ProjectResponseDTO toResponseDTO(ProjectDocument doc) {
        if (doc == null) {
            return null;
        }

        return ProjectResponseDTO.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .name(doc.getName())
                .status(doc.getStatus())
                .branding(toBrandingDTO(doc.getBranding()))
                .supportedLocales(doc.getSupportedLocales())
                .defaultLocale(doc.getDefaultLocale())
                .features(toFeatureFlagsDTO(doc.getFeatures()))
                .customAttributeDefinitions(toCustomAttributeDefDTOs(doc.getCustomAttributeDefinitions()))
                .version(doc.getVersion())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    public PublicThemeDTO toPublicThemeDTO(ProjectDocument doc) {
        if (doc == null || doc.getBranding() == null) {
            return null;
        }

        return PublicThemeDTO.builder()
                .projectId(doc.getProjectId())
                .companyName(doc.getBranding().getCompanyName())
                .logoUrl(doc.getBranding().getLogoUrl())
                .primaryColor(doc.getBranding().getPrimaryColor())
                .secondaryColor(doc.getBranding().getSecondaryColor())
                .customCssUrl(doc.getBranding().getCustomCssUrl())
                .defaultLocale(doc.getDefaultLocale())
                .build();
    }

    public BrandingConfig toBrandingConfig(BrandingDTO dto) {
        if (dto == null) return null;
        return BrandingConfig.builder()
                .companyName(dto.getCompanyName())
                .logoUrl(dto.getLogoUrl())
                .primaryColor(dto.getPrimaryColor())
                .secondaryColor(dto.getSecondaryColor())
                .customCssUrl(dto.getCustomCssUrl())
                .build();
    }

    public BrandingDTO toBrandingDTO(BrandingConfig config) {
        if (config == null) return null;
        return BrandingDTO.builder()
                .companyName(config.getCompanyName())
                .logoUrl(config.getLogoUrl())
                .primaryColor(config.getPrimaryColor())
                .secondaryColor(config.getSecondaryColor())
                .customCssUrl(config.getCustomCssUrl())
                .build();
    }

    public FeatureFlags toFeatureFlags(FeatureFlagsDTO dto) {
        if (dto == null) {
            return FeatureFlags.builder().build();
        }
        return FeatureFlags.builder()
                .aiAnalyticsEnabled(dto.isAiAnalyticsEnabled())
                .actionPlanningEnabled(dto.isActionPlanningEnabled())
                .kioskModeEnabled(dto.isKioskModeEnabled())
                .smsDistributionEnabled(dto.isSmsDistributionEnabled())
                .build();
    }

    public FeatureFlagsDTO toFeatureFlagsDTO(FeatureFlags flags) {
        if (flags == null) return null;
        return FeatureFlagsDTO.builder()
                .aiAnalyticsEnabled(flags.isAiAnalyticsEnabled())
                .actionPlanningEnabled(flags.isActionPlanningEnabled())
                .kioskModeEnabled(flags.isKioskModeEnabled())
                .smsDistributionEnabled(flags.isSmsDistributionEnabled())
                .build();
    }

    public List<CustomAttributeDefinition> toCustomAttributeDefinitions(List<CustomAttributeDefDTO> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream().map(dto -> CustomAttributeDefinition.builder()
                .key(dto.getKey())
                .displayName(dto.getDisplayName())
                .dataType(dto.getDataType())
                .allowedValues(dto.getAllowedValues())
                .build()).collect(Collectors.toList());
    }

    public List<CustomAttributeDefDTO> toCustomAttributeDefDTOs(List<CustomAttributeDefinition> definitions) {
        if (definitions == null) return Collections.emptyList();
        return definitions.stream().map(def -> CustomAttributeDefDTO.builder()
                .key(def.getKey())
                .displayName(def.getDisplayName())
                .dataType(def.getDataType())
                .allowedValues(def.getAllowedValues())
                .build()).collect(Collectors.toList());
    }
}
