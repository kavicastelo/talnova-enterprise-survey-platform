package com.talnova.tesp.auditservice.repository;

import com.talnova.tesp.auditservice.domain.model.AuditLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogDocument, String> {

    Optional<AuditLogDocument> findByAuditId(String auditId);

    Page<AuditLogDocument> findByProjectId(String projectId, Pageable pageable);

    Page<AuditLogDocument> findByProjectIdAndActorId(String projectId, String actorId, Pageable pageable);

    Page<AuditLogDocument> findByProjectIdAndAction(String projectId, String action, Pageable pageable);
}
