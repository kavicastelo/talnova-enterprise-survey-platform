package com.talnova.tesp.configservice.repository;

import com.talnova.tesp.configservice.domain.OutboxEventDocument;
import com.talnova.tesp.configservice.domain.OutboxStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends MongoRepository<OutboxEventDocument, String> {

    List<OutboxEventDocument> findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
