package com.talnova.tesp.configservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.configservice.domain.OutboxEventDocument;
import com.talnova.tesp.configservice.domain.OutboxStatus;
import com.talnova.tesp.configservice.domain.ProjectDocument;
import com.talnova.tesp.configservice.dto.BrandingDTO;
import com.talnova.tesp.configservice.dto.ProjectCreateDTO;
import com.talnova.tesp.configservice.mapper.ProjectMapper;
import com.talnova.tesp.configservice.messaging.OutboxEventPoller;
import com.talnova.tesp.configservice.repository.OutboxEventRepository;
import com.talnova.tesp.configservice.repository.ProjectRepository;
import com.talnova.tesp.configservice.service.AuditLoggerService;
import com.talnova.tesp.configservice.service.ProjectConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private AuditLoggerService auditLoggerService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ProjectMapper projectMapper;
    private ObjectMapper objectMapper;
    private ProjectConfigServiceImpl projectConfigService;
    private OutboxEventPoller outboxEventPoller;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();
        objectMapper = new ObjectMapper();
        projectConfigService = new ProjectConfigServiceImpl(projectRepository, outboxEventRepository, auditLoggerService, projectMapper, objectMapper);
        outboxEventPoller = new OutboxEventPoller(outboxEventRepository, kafkaTemplate);
    }

    @Test
    @DisplayName("TC-CFG-401-A: Creating a project saves a PENDING outbox event")
    void testCreateProjectSavesOutboxEvent() {
        ProjectCreateDTO dto = ProjectCreateDTO.builder()
                .projectId("PRJ-99201")
                .name("Aitken Spence Enterprise")
                .branding(BrandingDTO.builder()
                        .companyName("Aitken Spence PLC")
                        .primaryColor("#1E3A8A")
                        .secondaryColor("#3B82F6")
                        .build())
                .supportedLocales(List.of("en-US"))
                .defaultLocale("en-US")
                .build();

        ProjectDocument mockSavedDoc = projectMapper.toDocument(dto);
        mockSavedDoc.setId("66b26d8f8a84a51e3c8b4567");

        when(projectRepository.existsByProjectIdAndIsDeletedFalse("PRJ-99201")).thenReturn(false);
        when(projectRepository.save(any(ProjectDocument.class))).thenReturn(mockSavedDoc);

        projectConfigService.createProject(dto);

        ArgumentCaptor<OutboxEventDocument> outboxCaptor = ArgumentCaptor.forClass(OutboxEventDocument.class);
        verify(outboxEventRepository, times(1)).save(outboxCaptor.capture());

        OutboxEventDocument savedOutbox = outboxCaptor.getValue();
        assertEquals("PRJ-99201", savedOutbox.getProjectId());
        assertEquals("PROJECT_CREATED", savedOutbox.getEventType());
        assertEquals(OutboxStatus.PENDING, savedOutbox.getStatus());
        assertTrue(savedOutbox.getPayload().contains("PRJ-99201"));
    }

    @Test
    @DisplayName("TC-CFG-401-B: OutboxEventPoller dispatches PENDING event to Kafka topic tesp.project.events.v1")
    void testOutboxPollerDispatchesToKafka() {
        OutboxEventDocument pendingEvent = OutboxEventDocument.builder()
                .id("outbox-1")
                .eventId("EVT-12345678")
                .eventType("PROJECT_CREATED")
                .projectId("PRJ-99201")
                .payload("{\"projectId\":\"PRJ-99201\"}")
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .createdAt(Instant.now())
                .build();

        when(outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(pendingEvent));

        outboxEventPoller.processOutboxEvents();

        verify(kafkaTemplate, times(1)).send(OutboxEventPoller.TOPIC_PROJECT_EVENTS, "PRJ-99201", "{\"projectId\":\"PRJ-99201\"}");
        assertEquals(OutboxStatus.PROCESSED, pendingEvent.getStatus());
        assertNotNull(pendingEvent.getProcessedAt());
        verify(outboxEventRepository, times(1)).save(pendingEvent);
    }
}
