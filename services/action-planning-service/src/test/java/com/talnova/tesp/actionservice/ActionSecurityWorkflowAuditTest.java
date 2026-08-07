package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.controller.ActionPlanController;
import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.dto.ApprovalRequestDTO;
import com.talnova.tesp.actionservice.exception.ActionPlanningExceptionHandler;
import com.talnova.tesp.actionservice.jira.JiraSyncAdapterService;
import com.talnova.tesp.actionservice.msplanner.MsPlannerSyncAdapterService;
import com.talnova.tesp.actionservice.recommendation.ActionTemplateRecommenderService;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ActionPlanController.class)
@ContextConfiguration(classes = {ActionPlanController.class, ActionPlanningExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ActionSecurityWorkflowAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionStateMachineService stateMachineService;

    @MockBean
    private ActionPlanRepository actionPlanRepository;

    @MockBean
    private ActionTemplateRecommenderService recommenderService;

    @MockBean
    private JiraSyncAdapterService jiraSyncAdapterService;

    @MockBean
    private MsPlannerSyncAdapterService msPlannerSyncAdapterService;

    @Test
    @DisplayName("X-Project-ID Header Resolution: GET /api/v1/actions/kanban resolves projectId from gateway header")
    void testHeaderResolutionGetKanban() throws Exception {
        ActionPlanDocument mockPlan = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .title("Communication Huddle")
                .status(ActionStatus.APPROVED)
                .build();

        when(actionPlanRepository.findByProjectIdAndNodeId("PRJ-99201", "N-301")).thenReturn(List.of(mockPlan));

        mockMvc.perform(get("/api/v1/actions/kanban")
                        .header("X-Project-ID", "PRJ-99201")
                        .param("nodeId", "N-301"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actionPlanId").value("ACT-901"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    @DisplayName("FR-ACT-002 / BR-ACT-001: POST /api/v1/actions/{actionPlanId}/approve transitions state to APPROVED")
    void testApproveActionPlanEndpoint() throws Exception {
        ActionPlanDocument approvedDoc = ActionPlanDocument.builder()
                .actionPlanId("ACT-901")
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .title("Weekly Huddle")
                .status(ActionStatus.APPROVED)
                .assigneeId("EMP-9021")
                .createdBy("USR-HR-001")
                .build();

        when(stateMachineService.transitionState(eq("ACT-901"), eq(ActionStatus.APPROVED), eq("USR-HR-001"), eq("HR_MANAGER"), any()))
                .thenReturn(approvedDoc);

        mockMvc.perform(post("/api/v1/actions/ACT-901/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actorId\":\"USR-HR-001\",\"userRole\":\"HR_MANAGER\",\"rationale\":\"Approved for implementation\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionPlanId").value("ACT-901"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
