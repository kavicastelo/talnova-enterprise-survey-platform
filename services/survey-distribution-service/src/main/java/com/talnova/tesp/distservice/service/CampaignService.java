package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;

import java.util.List;

public interface CampaignService {

    CampaignResponseDTO createCampaign(CampaignCreateDTO dto);

    CampaignResponseDTO getCampaign(String projectId, String campaignId);

    CampaignResponseDTO updateCampaignStatus(String projectId, String campaignId, CampaignStatus newStatus);

    List<String> resolveTargetParticipantEmployeeIds(String projectId, List<String> nodeIds);
}
