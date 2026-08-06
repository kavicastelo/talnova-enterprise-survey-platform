package com.talnova.tesp.configservice.repository;

import com.talnova.tesp.configservice.domain.ProjectDocument;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final MongoOperations mongoOperations;

    public ProjectRepositoryCustomImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<ProjectDocument> findActiveProjectById(String projectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("projectId").is(projectId).and("isDeleted").is(false));
        ProjectDocument document = mongoOperations.findOne(query, ProjectDocument.class);
        return Optional.ofNullable(document);
    }

    @Override
    public List<ProjectDocument> findAllActiveProjects() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        return mongoOperations.find(query, ProjectDocument.class);
    }
}
