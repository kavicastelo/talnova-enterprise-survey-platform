package com.talnova.tesp.orgservice.repository;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgNodeRepository extends MongoRepository<OrgNodeDocument, String>, OrgNodeRepositoryCustom {

    @Query("{ 'projectId': ?0, 'nodeId': ?1, 'isDeleted': false }")
    Optional<OrgNodeDocument> findActiveByProjectIdAndNodeId(String projectId, String nodeId);

    boolean existsByProjectIdAndNodeIdAndIsDeletedFalse(String projectId, String nodeId);

    @Query("{ 'projectId': ?0, 'path': { $regex: ?1 }, 'isDeleted': false }")
    List<OrgNodeDocument> findSubTreeByPathPrefix(String projectId, String pathRegex);
}
