package com.talnova.tesp.reportingservice.repository;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportJobRepository extends MongoRepository<ReportJobDocument, String> {

    Optional<ReportJobDocument> findByProjectIdAndJobId(String projectId, String jobId);

    List<ReportJobDocument> findByProjectIdAndStatus(String projectId, ReportStatus status);

    List<ReportJobDocument> findByProjectIdAndCampaignId(String projectId, String campaignId);
}
