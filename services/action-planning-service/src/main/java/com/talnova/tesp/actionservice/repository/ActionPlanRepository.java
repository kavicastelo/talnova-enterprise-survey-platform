package com.talnova.tesp.actionservice.repository;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionPlanRepository extends MongoRepository<ActionPlanDocument, String> {

    Optional<ActionPlanDocument> findByActionPlanId(String actionPlanId);

    List<ActionPlanDocument> findByProjectIdAndNodeIdAndStatus(String projectId, String nodeId, ActionStatus status);

    List<ActionPlanDocument> findByProjectIdAndNodeId(String projectId, String nodeId);

    List<ActionPlanDocument> findByProjectIdAndCampaignId(String projectId, String campaignId);

    List<ActionPlanDocument> findByAssigneeIdAndStatus(String assigneeId, ActionStatus status);
}
