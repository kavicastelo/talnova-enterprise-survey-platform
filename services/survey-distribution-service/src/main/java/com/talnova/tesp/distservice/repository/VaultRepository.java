package com.talnova.tesp.distservice.repository;

import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaultRepository extends MongoRepository<IdentityTokenVaultDocument, String> {

    Optional<IdentityTokenVaultDocument> findByProjectIdAndCampaignIdAndToken(String projectId, String campaignId, String token);

    Optional<IdentityTokenVaultDocument> findByCampaignIdAndEmployeeId(String campaignId, String employeeId);

    List<IdentityTokenVaultDocument> findByCampaignIdAndIsBurnedFalse(String campaignId);

    Optional<IdentityTokenVaultDocument> findByCampaignIdAndKioskPin(String campaignId, String kioskPin);
}
