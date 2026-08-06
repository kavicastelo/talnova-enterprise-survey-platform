package com.talnova.tesp.orgservice.repository;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrgNodeRepositoryCustomImpl implements OrgNodeRepositoryCustom {

    private final MongoOperations mongoOperations;

    public OrgNodeRepositoryCustomImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<OrgNodeDocument> findActiveNodeByNodeId(String projectId, String nodeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId)
                .and("nodeId").is(nodeId)
                .and("isDeleted").is(false));
        OrgNodeDocument doc = mongoOperations.findOne(query, OrgNodeDocument.class);
        return Optional.ofNullable(doc);
    }

    @Override
    public List<OrgNodeDocument> findActiveSubTreeByPathPrefix(String projectId, String pathPrefix) {
        Query query = new Query();
        String regexPattern = "^" + pathPrefix;
        query.addCriteria(Criteria.where("projectId").is(projectId)
                .and("path").regex(regexPattern)
                .and("isDeleted").is(false));
        return mongoOperations.find(query, OrgNodeDocument.class);
    }

    @Override
    public List<OrgNodeDocument> findAllActiveByNodeIds(String projectId, List<String> nodeIds) {
        if (nodeIds == null || nodeIds.isEmpty()) {
            return List.of();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId)
                .and("nodeId").in(nodeIds)
                .and("isDeleted").is(false));
        return mongoOperations.find(query, OrgNodeDocument.class);
    }
}
