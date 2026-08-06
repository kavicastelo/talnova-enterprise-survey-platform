package com.talnova.tesp.orgservice.repository;

import com.talnova.tesp.orgservice.domain.OutboxEventDocument;
import com.talnova.tesp.orgservice.domain.OutboxStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends MongoRepository<OutboxEventDocument, String> {

    List<OutboxEventDocument> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
