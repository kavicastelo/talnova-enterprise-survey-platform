package com.talnova.tesp.distservice.repository;

import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends MongoRepository<SurveyCampaignDocument, String> {

    Optional<SurveyCampaignDocument> findByProjectIdAndCampaignIdAndIsDeletedFalse(String projectId, String campaignId);

    List<SurveyCampaignDocument> findByProjectIdAndStatusAndIsDeletedFalse(String projectId, CampaignStatus status);

    List<SurveyCampaignDocument> findByStatusAndIsDeletedFalse(CampaignStatus status);

    List<SurveyCampaignDocument> findByProjectIdAndIsDeletedFalse(String projectId);

    boolean existsByProjectIdAndCampaignIdAndIsDeletedFalse(String projectId, String campaignId);
}
