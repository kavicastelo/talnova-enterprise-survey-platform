package com.talnova.tesp.orgservice.repository;

import com.talnova.tesp.orgservice.domain.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByProjectId(String projectId);
}
