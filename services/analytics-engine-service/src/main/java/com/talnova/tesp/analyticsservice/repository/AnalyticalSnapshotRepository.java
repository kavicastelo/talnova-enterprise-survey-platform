package com.talnova.tesp.analyticsservice.repository;

import com.talnova.tesp.analyticsservice.domain.model.AnalyticalSnapshotDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticalSnapshotRepository extends MongoRepository<AnalyticalSnapshotDocument, String> {

    List<AnalyticalSnapshotDocument> findByProjectIdAndCampaignId(String projectId, String campaignId);

    Optional<AnalyticalSnapshotDocument> findFirstByProjectIdAndCampaignIdOrderBySnapshotDateDesc(String projectId, String campaignId);
}
