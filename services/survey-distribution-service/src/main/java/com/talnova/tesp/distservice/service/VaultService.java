package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;

import java.util.List;
import java.util.Optional;

public interface VaultService {

    void persistTokenBatchToVault(String projectId, String campaignId, List<GeneratedTokenDTO> tokens);

    Optional<IdentityTokenVaultDocument> getVaultRecordByToken(String projectId, String campaignId, String token);

    List<IdentityTokenVaultDocument> getUnburnedVaultRecordsForCampaign(String campaignId);

    void markTokenAsBurned(String projectId, String campaignId, String token);
}
