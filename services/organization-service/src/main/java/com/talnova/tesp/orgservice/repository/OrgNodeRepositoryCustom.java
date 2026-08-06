package com.talnova.tesp.orgservice.repository;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;

import java.util.List;
import java.util.Optional;

public interface OrgNodeRepositoryCustom {

    Optional<OrgNodeDocument> findActiveNodeByNodeId(String projectId, String nodeId);

    List<OrgNodeDocument> findActiveSubTreeByPathPrefix(String projectId, String pathPrefix);

    List<OrgNodeDocument> findAllActiveByNodeIds(String projectId, List<String> nodeIds);
}
