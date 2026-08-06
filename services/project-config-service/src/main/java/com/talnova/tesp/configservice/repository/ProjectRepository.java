package com.talnova.tesp.configservice.repository;

import com.talnova.tesp.configservice.domain.ProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<ProjectDocument, String>, ProjectRepositoryCustom {

    @Query("{ 'projectId': ?0, 'isDeleted': false }")
    Optional<ProjectDocument> findActiveByProjectId(String projectId);

    boolean existsByProjectIdAndIsDeletedFalse(String projectId);
}
