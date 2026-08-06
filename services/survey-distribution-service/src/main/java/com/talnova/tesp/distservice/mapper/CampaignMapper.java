package com.talnova.tesp.distservice.mapper;

import com.talnova.tesp.distservice.domain.model.CampaignMetrics;
import com.talnova.tesp.distservice.domain.model.CampaignSchedule;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.domain.model.TargetAudience;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignMetricsDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.dto.CampaignScheduleDTO;
import com.talnova.tesp.distservice.dto.TargetAudienceDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CampaignMapper {

    public SurveyCampaignDocument toDocument(CampaignCreateDTO dto) {
        if (dto == null) return null;

        TargetAudience targetAudience = dto.getTargetAudience() != null
                ? new TargetAudience(dto.getTargetAudience().getNodeIds(), dto.getTargetAudience().getDemographicFilters())
                : new TargetAudience();

        CampaignSchedule schedule = dto.getSchedule() != null
                ? new CampaignSchedule(dto.getSchedule().getReminderDates(), dto.getSchedule().getReminderFrequencyDays())
                : new CampaignSchedule();

        return SurveyCampaignDocument.builder()
                .projectId(dto.getProjectId())
                .campaignId(dto.getCampaignId())
                .surveyId(dto.getSurveyId())
                .surveyVersion(dto.getSurveyVersion() != null ? dto.getSurveyVersion() : 1)
                .title(dto.getTitle())
                .anonymityLevel(dto.getAnonymityLevel())
                .channels(dto.getChannels() != null ? dto.getChannels() : new ArrayList<>())
                .targetAudience(targetAudience)
                .schedule(schedule)
                .metrics(new CampaignMetrics())
                .startDate(dto.getStartDate())
                .expirationDate(dto.getExpirationDate())
                .isDeleted(false)
                .build();
    }

    public CampaignResponseDTO toResponseDTO(SurveyCampaignDocument doc) {
        if (doc == null) return null;

        TargetAudienceDTO targetAudienceDTO = doc.getTargetAudience() != null
                ? new TargetAudienceDTO(doc.getTargetAudience().getNodeIds(), doc.getTargetAudience().getDemographicFilters())
                : new TargetAudienceDTO();

        CampaignScheduleDTO scheduleDTO = doc.getSchedule() != null
                ? new CampaignScheduleDTO(doc.getSchedule().getReminderDates(), doc.getSchedule().getReminderFrequencyDays())
                : new CampaignScheduleDTO();

        CampaignMetricsDTO metricsDTO = doc.getMetrics() != null
                ? CampaignMetricsDTO.builder()
                .totalTargeted(doc.getMetrics().getTotalTargeted())
                .sent(doc.getMetrics().getSent())
                .delivered(doc.getMetrics().getDelivered())
                .opened(doc.getMetrics().getOpened())
                .started(doc.getMetrics().getStarted())
                .completed(doc.getMetrics().getCompleted())
                .bounced(doc.getMetrics().getBounced())
                .build()
                : new CampaignMetricsDTO();

        return CampaignResponseDTO.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .campaignId(doc.getCampaignId())
                .surveyId(doc.getSurveyId())
                .surveyVersion(doc.getSurveyVersion())
                .title(doc.getTitle())
                .anonymityLevel(doc.getAnonymityLevel())
                .channels(doc.getChannels())
                .targetAudience(targetAudienceDTO)
                .schedule(scheduleDTO)
                .metrics(metricsDTO)
                .status(doc.getStatus())
                .startDate(doc.getStartDate())
                .expirationDate(doc.getExpirationDate())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
