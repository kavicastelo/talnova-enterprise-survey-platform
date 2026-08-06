package com.talnova.tesp.configservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.talnova.tesp.common.event.DomainEvent;
import com.talnova.tesp.configservice.domain.OutboxEventDocument;
import com.talnova.tesp.configservice.domain.OutboxStatus;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.dto.FeatureFlagsDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.dto.ProjectResponseDTO;
import com.talnova.tesp.configservice.dto.PublicThemeDTO;
import com.talnova.tesp.configservice.exception.DuplicateProjectIdException;
import com.talnova.tesp.configservice.exception.ProjectNotFoundException;
import com.talnova.tesp.configservice.exception.ProjectValidationException;
import com.talnova.tesp.configservice.mapper.ProjectMapper;
import com.talnova.tesp.configservice.repository.OutboxEventRepository;
import com.talnova.tesp.configservice.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProjectConfigServiceImpl implements ProjectConfigService {

    private static final Logger log = LoggerFactory.getLogger(ProjectConfigServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final AuditLoggerService auditLoggerService;
    private final ProjectMapper projectMapper;
    private final ObjectMapper objectMapper;

    public ProjectConfigServiceImpl(ProjectRepository projectRepository,
                                     OutboxEventRepository outboxEventRepository,
                                     AuditLoggerService auditLoggerService,
                                     ProjectMapper projectMapper,
                                     ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.auditLoggerService = auditLoggerService;
        this.projectMapper = projectMapper;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @Transactional
    public ProjectResponseDTO createProject(ProjectCreateDTO createDTO) {
        log.info("Creating new project workspace with projectId: {}", createDTO.getProjectId());

        if (projectRepository.existsByProjectIdAndIsDeletedFalse(createDTO.getProjectId())) {
            throw new DuplicateProjectIdException(createDTO.getProjectId());
        }

        if (!createDTO.getSupportedLocales().contains(createDTO.getDefaultLocale())) {
            throw new ProjectValidationException("Default locale must be present in supported locales");
        }

        ProjectDocument document = projectMapper.toDocument(createDTO);
        ProjectDocument savedDocument = projectRepository.save(document);
        log.info("Successfully provisioned project workspace with ID: {}", savedDocument.getId());

        saveOutboxEvent(savedDocument.getProjectId(), "PROJECT_CREATED", projectMapper.toResponseDTO(savedDocument));
        auditLoggerService.logAuditEvent(savedDocument.getProjectId(), "CREATE_PROJECT", "Provisioned project workspace", "127.0.0.1");

        return projectMapper.toResponseDTO(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tesp:config", key = "#projectId")
    public ProjectResponseDTO getProjectByProjectId(String projectId) {
        log.info("Fetching project workspace for projectId: {}", projectId);
        ProjectDocument document = projectRepository.findActiveByProjectId(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        return projectMapper.toResponseDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tesp:theme", key = "#projectId")
    public PublicThemeDTO getPublicTheme(String projectId) {
        log.info("Fetching public theme for projectId: {}", projectId);
        ProjectDocument document = projectRepository.findActiveByProjectId(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        return projectMapper.toPublicThemeDTO(document);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"tesp:config", "tesp:theme"}, key = "#projectId")
    public ProjectResponseDTO updateProject(String projectId, ProjectCreateDTO updateDTO) {
        log.info("Updating project workspace for projectId: {}", projectId);
        ProjectDocument existingDocument = projectRepository.findActiveByProjectId(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!updateDTO.getSupportedLocales().contains(updateDTO.getDefaultLocale())) {
            throw new ProjectValidationException("Default locale must be present in supported locales");
        }

        existingDocument.setName(updateDTO.getName());
        existingDocument.setBranding(projectMapper.toBrandingConfig(updateDTO.getBranding()));
        existingDocument.setSupportedLocales(updateDTO.getSupportedLocales());
        existingDocument.setDefaultLocale(updateDTO.getDefaultLocale());
        existingDocument.setFeatures(projectMapper.toFeatureFlags(updateDTO.getFeatures()));
        existingDocument.setCustomAttributeDefinitions(projectMapper.toCustomAttributeDefinitions(updateDTO.getCustomAttributeDefinitions()));

        ProjectDocument updatedDocument = projectRepository.save(existingDocument);
        log.info("Successfully updated project workspace for projectId: {}", projectId);

        saveOutboxEvent(projectId, "PROJECT_UPDATED", projectMapper.toResponseDTO(updatedDocument));
        auditLoggerService.logAuditEvent(projectId, "UPDATE_PROJECT", "Updated project settings and branding", "127.0.0.1");

        return projectMapper.toResponseDTO(updatedDocument);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"tesp:config", "tesp:theme"}, key = "#projectId")
    public ProjectResponseDTO updateFeatureFlags(String projectId, FeatureFlagsDTO featureFlagsDTO) {
        log.info("Updating feature flags for projectId: {}", projectId);
        ProjectDocument existingDocument = projectRepository.findActiveByProjectId(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        existingDocument.setFeatures(projectMapper.toFeatureFlags(featureFlagsDTO));
        ProjectDocument updatedDocument = projectRepository.save(existingDocument);

        saveOutboxEvent(projectId, "PROJECT_UPDATED", projectMapper.toResponseDTO(updatedDocument));
        auditLoggerService.logAuditEvent(projectId, "TOGGLE_FEATURE_FLAGS", "Updated module feature activation flags", "127.0.0.1");

        return projectMapper.toResponseDTO(updatedDocument);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"tesp:config", "tesp:theme"}, key = "#projectId")
    public void deleteProject(String projectId) {
        log.info("Soft deleting project workspace for projectId: {}", projectId);
        ProjectDocument existingDocument = projectRepository.findActiveByProjectId(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        existingDocument.setDeleted(true);
        projectRepository.save(existingDocument);

        saveOutboxEvent(projectId, "PROJECT_DELETED", null);
        auditLoggerService.logAuditEvent(projectId, "DELETE_PROJECT", "Soft deleted project workspace", "127.0.0.1");
        log.info("Soft delete completed for projectId: {}", projectId);
    }

    private void saveOutboxEvent(String projectId, String eventType, Object payloadData) {
        try {
            String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8);
            DomainEvent<Object> domainEvent = new DomainEvent<>(eventId, eventType, projectId, UUID.randomUUID().toString(), payloadData);
            String jsonPayload = objectMapper.writeValueAsString(domainEvent);

            OutboxEventDocument outboxEvent = OutboxEventDocument.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .projectId(projectId)
                    .payload(jsonPayload)
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Outbox event {} ({}) created for projectId: {}", eventId, eventType, projectId);
        } catch (Exception ex) {
            log.error("Failed to write outbox event for projectId {}: {}", projectId, ex.getMessage());
        }
    }
}
