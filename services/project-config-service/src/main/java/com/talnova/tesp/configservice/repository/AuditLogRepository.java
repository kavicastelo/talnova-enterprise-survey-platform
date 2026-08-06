package com.talnova.tesp.configservice.repository;

import com.talnova.tesp.configservice.domain.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByProjectIdOrderByTimestampDesc(String projectId);
}
