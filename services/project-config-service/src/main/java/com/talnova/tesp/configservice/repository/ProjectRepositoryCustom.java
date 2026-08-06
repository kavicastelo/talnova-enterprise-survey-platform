package com.talnova.tesp.configservice.repository;

import com.talnova.tesp.configservice.domain.ProjectDocument;
import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryCustom {

    Optional<ProjectDocument> findActiveProjectById(String projectId);

    List<ProjectDocument> findAllActiveProjects();
}
