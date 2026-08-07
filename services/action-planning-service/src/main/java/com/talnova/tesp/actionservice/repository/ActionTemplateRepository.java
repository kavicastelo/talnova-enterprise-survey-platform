package com.talnova.tesp.actionservice.repository;

import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionTemplateRepository extends MongoRepository<ActionTemplateDocument, String> {

    Optional<ActionTemplateDocument> findByTemplateId(String templateId);

    List<ActionTemplateDocument> findByGroupId(String groupId);
}
