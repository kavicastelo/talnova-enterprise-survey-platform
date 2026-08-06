package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.repository.VaultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VaultServiceImpl implements VaultService {

    private static final Logger log = LoggerFactory.getLogger(VaultServiceImpl.class);

    private final VaultRepository vaultRepository;

    public VaultServiceImpl(VaultRepository vaultRepository) {
        this.vaultRepository = vaultRepository;
    }

    @Override
    @Transactional
    public void persistTokenBatchToVault(String projectId, String campaignId, List<GeneratedTokenDTO> tokens) {
        log.info("Persisting batch of {} tokens to isolated Vault DB for campaignId: {}", tokens != null ? tokens.size() : 0, campaignId);

        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        List<IdentityTokenVaultDocument> documents = new ArrayList<>();
        for (GeneratedTokenDTO dto : tokens) {
            IdentityTokenVaultDocument doc = IdentityTokenVaultDocument.builder()
                    .projectId(projectId)
                    .campaignId(campaignId)
                    .employeeId(dto.getEmployeeId())
                    .token(dto.getToken())
                    .kioskPin(dto.getKioskPin())
                    .anonymityLevel(dto.getAnonymityLevel())
                    .isBurned(false)
                    .build();
            documents.add(doc);
        }

        vaultRepository.saveAll(documents);
        log.info("Successfully vaulted {} tokens in tesp_vault_db for campaign '{}'", documents.size(), campaignId);
    }

    @Override
    public Optional<IdentityTokenVaultDocument> getVaultRecordByToken(String projectId, String campaignId, String token) {
        return vaultRepository.findByProjectIdAndCampaignIdAndToken(projectId, campaignId, token);
    }

    @Override
    public List<IdentityTokenVaultDocument> getUnburnedVaultRecordsForCampaign(String campaignId) {
        log.info("Polling unburned tokens from vault for campaignId: {}", campaignId);
        return vaultRepository.findByCampaignIdAndIsBurnedFalse(campaignId);
    }

    @Override
    @Transactional
    public void markTokenAsBurned(String projectId, String campaignId, String token) {
        log.info("Burning token in vault for campaignId: {}, token: {}", campaignId, token);
        IdentityTokenVaultDocument doc = vaultRepository.findByProjectIdAndCampaignIdAndToken(projectId, campaignId, token)
                .orElseThrow(() -> new CampaignValidationException("Token record not found in vault: " + token));

        doc.setBurned(true);
        doc.setBurnedAt(Instant.now());
        vaultRepository.save(doc);
        log.info("Token successfully burned in vault for campaign '{}'", campaignId);
    }
}
