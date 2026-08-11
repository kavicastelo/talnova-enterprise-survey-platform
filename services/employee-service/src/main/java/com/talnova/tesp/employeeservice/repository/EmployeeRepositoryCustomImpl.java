package com.talnova.tesp.employeeservice.repository;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    private final MongoOperations mongoOperations;

    public EmployeeRepositoryCustomImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<EmployeeDocument> findActiveEmployeeByEmployeeId(String projectId, String employeeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId)
                .and("employeeId").is(employeeId)
                .and("isDeleted").is(false));
        EmployeeDocument doc = mongoOperations.findOne(query, EmployeeDocument.class);
        return Optional.ofNullable(doc);
    }

    @Override
    public List<EmployeeDocument> findActiveByNodeIdAndStatus(String projectId, String nodeId, EmployeeStatus status) {
        Query query = new Query();
        Criteria criteria = Criteria.where("projectId").is(projectId)
                .and("nodeId").is(nodeId)
                .and("isDeleted").is(false);

        if (status != null) {
            criteria.and("status").is(status.name());
        }

        query.addCriteria(criteria);
        return mongoOperations.find(query, EmployeeDocument.class);
    }

    @Override
    public List<EmployeeDocument> findActiveByMatrixNodeId(String projectId, String matrixNodeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId)
                .and("matrixNodeIds").in(matrixNodeId)
                .and("isDeleted").is(false));
        return mongoOperations.find(query, EmployeeDocument.class);
    }

    @Override
    public List<EmployeeDocument> findAllActiveByProjectId(String projectId, String nodeId, EmployeeStatus status) {
        Query query = new Query();
        Criteria criteria = Criteria.where("projectId").is(projectId)
                .and("isDeleted").is(false);

        if (nodeId != null && !nodeId.isBlank()) {
            criteria.and("nodeId").is(nodeId);
        }
        if (status != null) {
            criteria.and("status").is(status.name());
        }

        query.addCriteria(criteria);
        return mongoOperations.find(query, EmployeeDocument.class);
    }
}
