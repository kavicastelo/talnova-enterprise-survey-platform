package com.talnova.tesp.configservice.service;

import com.talnova.tesp.configservice.dto.FeatureFlagsDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.dto.ProjectResponseDTO;
import com.talnova.tesp.configservice.dto.PublicThemeDTO;

public interface ProjectConfigService {

    ProjectResponseDTO createProject(ProjectCreateDTO createDTO);

    ProjectResponseDTO getProjectByProjectId(String projectId);

    PublicThemeDTO getPublicTheme(String projectId);

    ProjectResponseDTO updateProject(String projectId, ProjectCreateDTO updateDTO);

    ProjectResponseDTO updateFeatureFlags(String projectId, FeatureFlagsDTO featureFlagsDTO);

    void deleteProject(String projectId);
}
