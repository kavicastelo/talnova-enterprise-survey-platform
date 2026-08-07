package com.talnova.tesp.actionservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.actionservice.controller.ActionPlanController;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;
import com.talnova.tesp.actionservice.dto.ApprovalRequestDTO;
import com.talnova.tesp.actionservice.exception.ActionPlanningExceptionHandler;
import com.talnova.tesp.actionservice.exception.InvalidStateTransitionException;
import com.talnova.tesp.actionservice.jira.JiraSyncAdapterService;
import com.talnova.tesp.actionservice.msplanner.MsPlannerSyncAdapterService;
import com.talnova.tesp.actionservice.recommendation.ActionTemplateRecommenderService;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ActionPlanControllerTest {

    @Mock
    private ActionStateMachineService stateMachineService;

    @Mock
    private ActionPlanRepository actionPlanRepository;

    @Mock
    private ActionTemplateRecommenderService recommenderService;

    @Mock
    private JiraSyncAdapterService jiraSyncAdapterService;

    @Mock
    private MsPlannerSyncAdapterService msPlannerSyncAdapterService;

    @InjectMocks
    private ActionPlanController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ActionPlanningExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("TC-ACT-302-01: POST /api/v1/actions/{actionPlanId}/approve returns 200 OK for HR_MANAGER per FR-ACT-002")
    void testApproveActionPlanSuccess() throws Exception {
        ActionPlanDocument approved = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .status(ActionStatus.APPROVED)
                .build();

        when(stateMachineService.transitionState(eq("ACT-901"), eq(ActionStatus.APPROVED), eq("USR-HR-DIR"), eq("HR_MANAGER"), anyString()))
                .thenReturn(approved);

        ApprovalRequestDTO request = ApprovalRequestDTO.builder()
                .actorId("USR-HR-DIR")
                .userRole("HR_MANAGER")
                .rationale("Approved action plan budget")
                .build();

        mockMvc.perform(post("/api/v1/actions/ACT-901/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionPlanId").value("ACT-901"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("TC-ACT-502-03: POST /api/v1/actions/{actionPlanId}/sync-ms-planner triggers MS Planner sync per FR-ACT-004")
    void testSyncToPlannerEndpoint() throws Exception {
        ExternalSyncInfo syncInfo = ExternalSyncInfo.builder()
                .system("MS_PLANNER")
                .externalKey("PLN-88102")
                .lastSyncedAt(Instant.now())
                .build();

        when(msPlannerSyncAdapterService.syncActionPlanToPlanner("ACT-901", "PLN-MAIN"))
                .thenReturn(syncInfo);

        mockMvc.perform(post("/api/v1/actions/ACT-901/sync-ms-planner")
                        .param("planId", "PLN-MAIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.system").value("MS_PLANNER"))
                .andExpect(jsonPath("$.externalKey").value("PLN-88102"));
    }
}
