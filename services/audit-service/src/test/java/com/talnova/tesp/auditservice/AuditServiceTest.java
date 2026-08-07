package com.talnova.tesp.auditservice;

import com.talnova.tesp.auditservice.controller.AuditLogController;
import com.talnova.tesp.auditservice.domain.model.AuditLogDocument;
import com.talnova.tesp.auditservice.dto.AuditLogRequestDTO;
import com.talnova.tesp.auditservice.listener.AuditEventKafkaListener;
import com.talnova.tesp.auditservice.repository.AuditLogRepository;
import com.talnova.tesp.auditservice.service.AuditLogService;
import com.talnova.tesp.auditservice.service.AuditLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLogService auditLogService;
    private AuditEventKafkaListener kafkaListener;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditLogService = new AuditLogServiceImpl(auditLogRepository);
        kafkaListener = new AuditEventKafkaListener(auditLogService);
        AuditLogController controller = new AuditLogController(auditLogService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("TC-AUD-001: Append-only audit logger records audit entry per BR-SEC-012")
    void testRecordAuditLogSuccess() {
        when(auditLogRepository.save(any(AuditLogDocument.class))).thenAnswer(i -> i.getArgument(0));

        AuditLogRequestDTO req = AuditLogRequestDTO.builder()
                .projectId("PRJ-99201")
                .actorId("USR-HR-001")
                .userRole("HR_MANAGER")
                .action("ACTION_PLAN_APPROVED")
                .resourceId("ACT-901")
                .details("Approved action plan budget")
                .build();

        AuditLogDocument doc = auditLogService.recordAuditLog(req);
        assertNotNull(doc.getAuditId());
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("ACTION_PLAN_APPROVED", doc.getAction());
        verify(auditLogRepository, times(1)).save(any(AuditLogDocument.class));
    }

    @Test
    @DisplayName("TC-AUD-002: Kafka Audit Event Listener consumes tesp.audit.events.v1 payload")
    void testKafkaEventListenerConsumption() {
        when(auditLogRepository.save(any(AuditLogDocument.class))).thenAnswer(i -> i.getArgument(0));

        String jsonMessage = "{\"projectId\":\"PRJ-99201\",\"actorId\":\"USR-ADMIN\",\"userRole\":\"PROJECT_ADMIN\",\"action\":\"PROJECT_BRANDING_UPDATED\",\"resourceId\":\"PRJ-99201\",\"details\":\"Updated primary color to #1e3a8a\"}";
        kafkaListener.consumeAuditEvent(jsonMessage);

        verify(auditLogRepository, times(1)).save(any(AuditLogDocument.class));
    }

    @Test
    @DisplayName("TC-AUD-003: REST POST /api/v1/audit/logs records audit entry and returns HTTP 201 Created")
    void testPostAuditLogApi() throws Exception {
        when(auditLogRepository.save(any(AuditLogDocument.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/v1/audit/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Project-ID", "PRJ-99201")
                        .content("{\"actorId\":\"USR-HR-001\",\"userRole\":\"HR_MANAGER\",\"action\":\"SENTIMENT_OVERRIDE\",\"resourceId\":\"INS-402\",\"details\":\"Human overridden sentiment label to POSITIVE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auditId").exists())
                .andExpect(jsonPath("$.action").value("SENTIMENT_OVERRIDE"));
    }

    @Test
    @DisplayName("TC-AUD-004: REST GET /api/v1/audit/logs returns paginated project audit trail")
    void testGetAuditLogsApi() throws Exception {
        AuditLogDocument doc = AuditLogDocument.builder()
                .auditId("AUD-88102")
                .projectId("PRJ-99201")
                .actorId("USR-HR-001")
                .userRole("HR_MANAGER")
                .action("ACTION_PLAN_APPROVED")
                .resourceId("ACT-901")
                .timestamp(Instant.now())
                .build();

        when(auditLogRepository.findByProjectId(eq("PRJ-99201"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(doc)));

        mockMvc.perform(get("/api/v1/audit/logs")
                        .header("X-Project-ID", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].auditId").value("AUD-88102"))
                .andExpect(jsonPath("$[0].action").value("ACTION_PLAN_APPROVED"));
    }
}
