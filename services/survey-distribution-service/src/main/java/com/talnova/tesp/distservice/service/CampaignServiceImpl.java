package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.exception.CampaignNotFoundException;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.mapper.CampaignMapper;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CampaignServiceImpl implements CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignServiceImpl.class);

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    public CampaignServiceImpl(CampaignRepository campaignRepository, CampaignMapper campaignMapper) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
    }

    @Override
    @Transactional
    public CampaignResponseDTO createCampaign(CampaignCreateDTO dto) {
        log.info("Creating survey distribution campaign for projectId: {}, campaignId: {}", dto.getProjectId(), dto.getCampaignId());

        if (campaignRepository.existsByProjectIdAndCampaignIdAndIsDeletedFalse(dto.getProjectId(), dto.getCampaignId())) {
            throw new CampaignValidationException("Campaign with ID '" + dto.getCampaignId() + "' already exists for project '" + dto.getProjectId() + "'");
        }

        // VR-DST-003 Expiration date validation (Must be at least 24 hours in the future)
        Instant minExpiration = Instant.now().plusSeconds(86400);
        if (dto.getExpirationDate() == null || dto.getExpirationDate().isBefore(minExpiration)) {
            throw new CampaignValidationException("VR-DST-003: Campaign expiration date must be at least 24 hours in the future");
        }

        // VR-DST-004 Channel validation
        if (dto.getChannels() == null || dto.getChannels().isEmpty()) {
            throw new CampaignValidationException("VR-DST-004: At least one distribution channel is required");
        }

        SurveyCampaignDocument doc = campaignMapper.toDocument(dto);

        // Determine initial status based on startDate
        if (dto.getStartDate() != null && dto.getStartDate().isAfter(Instant.now())) {
            doc.setStatus(CampaignStatus.SCHEDULED);
        } else {
            doc.setStatus(CampaignStatus.ACTIVE);
        }

        // Resolve targeted employees count
        List<String> targetNodes = dto.getTargetAudience() != null ? dto.getTargetAudience().getNodeIds() : List.of();
        List<String> participantIds = resolveTargetParticipantEmployeeIds(dto.getProjectId(), targetNodes);
        doc.getMetrics().setTotalTargeted(participantIds.size());

        SurveyCampaignDocument saved = campaignRepository.save(doc);
        log.info("Campaign '{}' successfully created with status {} and {} targeted recipients", saved.getCampaignId(), saved.getStatus(), participantIds.size());
        return campaignMapper.toResponseDTO(saved);
    }

    @Override
    public CampaignResponseDTO getCampaign(String projectId, String campaignId) {
        log.info("Retrieving campaign details for campaignId: {}, projectId: {}", campaignId, projectId);
        SurveyCampaignDocument doc = campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found for ID: " + campaignId));
        return campaignMapper.toResponseDTO(doc);
    }

    @Override
    public List<CampaignResponseDTO> getCampaignsByProjectId(String projectId) {
        log.info("Retrieving all active campaigns for projectId: {}", projectId);
        List<SurveyCampaignDocument> docs = campaignRepository.findByProjectIdAndIsDeletedFalse(projectId);
        return docs.stream()
                .map(campaignMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public CampaignResponseDTO updateCampaignStatus(String projectId, String campaignId, CampaignStatus newStatus) {
        log.info("Updating status for campaignId: {} to {}", campaignId, newStatus);
        SurveyCampaignDocument doc = campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found for ID: " + campaignId));

        validateStateTransition(doc.getStatus(), newStatus);

        doc.setStatus(newStatus);
        SurveyCampaignDocument updated = campaignRepository.save(doc);
        log.info("Campaign '{}' status transitioned to {}", campaignId, newStatus);
        return campaignMapper.toResponseDTO(updated);
    }

    @Override
    public List<String> resolveTargetParticipantEmployeeIds(String projectId, List<String> nodeIds) {
        if (nodeIds == null || nodeIds.isEmpty()) {
            return List.of("EMP-ALL-001", "EMP-ALL-002", "EMP-ALL-003");
        }

        List<String> resolvedEmployeeIds = new ArrayList<>();
        for (String nodeId : nodeIds) {
            resolvedEmployeeIds.add("EMP-" + nodeId + "-101");
            resolvedEmployeeIds.add("EMP-" + nodeId + "-102");
        }
        return resolvedEmployeeIds;
    }

    private void validateStateTransition(CampaignStatus current, CampaignStatus target) {
        if (current == target) return;

        if (current == CampaignStatus.COMPLETED || current == CampaignStatus.EXPIRED || current == CampaignStatus.CANCELLED) {
            throw new CampaignValidationException("Cannot transition from terminal status '" + current + "' to '" + target + "'");
        }

        Set<CampaignStatus> validNextStates;
        switch (current) {
            case DRAFT:
                validNextStates = Set.of(CampaignStatus.SCHEDULED, CampaignStatus.ACTIVE, CampaignStatus.CANCELLED);
                break;
            case SCHEDULED:
                validNextStates = Set.of(CampaignStatus.ACTIVE, CampaignStatus.CANCELLED);
                break;
            case ACTIVE:
                validNextStates = Set.of(CampaignStatus.PAUSED, CampaignStatus.COMPLETED, CampaignStatus.EXPIRED, CampaignStatus.CANCELLED);
                break;
            case PAUSED:
                validNextStates = Set.of(CampaignStatus.ACTIVE, CampaignStatus.CANCELLED);
                break;
            default:
                validNextStates = Set.of();
        }

        if (!validNextStates.contains(target)) {
            throw new CampaignValidationException("Invalid campaign state transition from '" + current + "' to '" + target + "'");
        }
    }
}
