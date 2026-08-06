package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.ReminderNudgeTargetDTO;
import com.talnova.tesp.distservice.exception.CampaignNotFoundException;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import com.talnova.tesp.distservice.repository.VaultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReminderTargetPollerServiceImpl implements ReminderTargetPollerService {

    private static final Logger log = LoggerFactory.getLogger(ReminderTargetPollerServiceImpl.class);

    private final CampaignRepository campaignRepository;
    private final VaultRepository vaultRepository;

    public ReminderTargetPollerServiceImpl(CampaignRepository campaignRepository, VaultRepository vaultRepository) {
        this.campaignRepository = campaignRepository;
        this.vaultRepository = vaultRepository;
    }

    @Override
    public List<ReminderNudgeTargetDTO> pollUncompletedReminderTargets(String projectId, String campaignId) {
        log.info("Polling uncompleted reminder targets for projectId: {}, campaignId: {}", projectId, campaignId);

        SurveyCampaignDocument campaign = campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found for campaignId: " + campaignId));

        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            log.warn("Campaign '{}' status is {}, skipping reminder polling", campaignId, campaign.getStatus());
            throw new CampaignValidationException("Reminders can only be polled for ACTIVE campaigns");
        }

        List<IdentityTokenVaultDocument> unburnedVaultDocs = vaultRepository.findByCampaignIdAndIsBurnedFalse(campaignId);
        log.info("Found {} unburned active token records in vault for campaign '{}'", unburnedVaultDocs.size(), campaignId);

        List<ReminderNudgeTargetDTO> targets = new ArrayList<>();
        for (IdentityTokenVaultDocument vaultDoc : unburnedVaultDocs) {
            ReminderNudgeTargetDTO target = ReminderNudgeTargetDTO.builder()
                    .projectId(projectId)
                    .campaignId(campaignId)
                    .employeeId(vaultDoc.getEmployeeId())
                    .token(vaultDoc.getToken())
                    .kioskPin(vaultDoc.getKioskPin())
                    .anonymityLevel(vaultDoc.getAnonymityLevel())
                    .channels(campaign.getChannels())
                    .build();
            targets.add(target);
        }

        log.info("Successfully filtered {} uncompleted nudge targets for campaign '{}'", targets.size(), campaignId);
        return targets;
    }
}
