package com.talnova.tesp.employeeservice.repository;

import com.talnova.tesp.employeeservice.domain.DemographicSnapshotDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemographicSnapshotRepository extends MongoRepository<DemographicSnapshotDocument, String> {

    List<DemographicSnapshotDocument> findByProjectIdAndSurveyId(String projectId, String surveyId);

    Optional<DemographicSnapshotDocument> findBySurveyIdAndEmployeeId(String surveyId, String employeeId);
}
