package com.talnova.tesp.actionservice.controller;

import com.talnova.tesp.actionservice.domain.model.ActionPlanDocument;
import com.talnova.tesp.actionservice.domain.model.ActionStatus;
import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import com.talnova.tesp.actionservice.domain.model.ExternalSyncInfo;
import com.talnova.tesp.actionservice.dto.ActionPlanResponseDTO;
import com.talnova.tesp.actionservice.dto.ApprovalRequestDTO;
import com.talnova.tesp.actionservice.jira.JiraSyncAdapterService;
import com.talnova.tesp.actionservice.msplanner.MsPlannerSyncAdapterService;
import com.talnova.tesp.actionservice.recommendation.ActionTemplateRecommenderService;
import com.talnova.tesp.actionservice.repository.ActionPlanRepository;
import com.talnova.tesp.actionservice.statemachine.ActionStateMachineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/actions")
public class ActionPlanController {

    private final ActionStateMachineService stateMachineService;
    private final ActionPlanRepository actionPlanRepository;
    private final ActionTemplateRecommenderService recommenderService;
    private final JiraSyncAdapterService jiraSyncAdapterService;
    private final MsPlannerSyncAdapterService msPlannerSyncAdapterService;

    @Autowired
    public ActionPlanController(ActionStateMachineService stateMachineService, ActionPlanRepository actionPlanRepository, ActionTemplateRecommenderService recommenderService, JiraSyncAdapterService jiraSyncAdapterService, MsPlannerSyncAdapterService msPlannerSyncAdapterService) {
        this.stateMachineService = stateMachineService;
        this.actionPlanRepository = actionPlanRepository;
        this.recommenderService = recommenderService;
        this.jiraSyncAdapterService = jiraSyncAdapterService;
        this.msPlannerSyncAdapterService = msPlannerSyncAdapterService;
    }

    @PostMapping("/{actionPlanId}/approve")
    public ResponseEntity<ActionPlanResponseDTO> approveActionPlan(
            @PathVariable String actionPlanId,
            @Valid @RequestBody ApprovalRequestDTO request) {
        ActionPlanDocument approved = stateMachineService.transitionState(
                actionPlanId,
                ActionStatus.APPROVED,
                request.getActorId(),
                request.getUserRole(),
                request.getRationale() != null ? request.getRationale() : "Approved by HR Manager"
        );
        return ResponseEntity.ok(ActionPlanResponseDTO.fromDocument(approved));
    }

    @PostMapping("/{actionPlanId}/reject")
    public ResponseEntity<ActionPlanResponseDTO> rejectActionPlan(
            @PathVariable String actionPlanId,
            @Valid @RequestBody ApprovalRequestDTO request) {
        ActionPlanDocument rejected = stateMachineService.transitionState(
                actionPlanId,
                ActionStatus.REJECTED,
                request.getActorId(),
                request.getUserRole(),
                request.getRationale() != null ? request.getRationale() : "Rejected by HR Manager"
        );
        return ResponseEntity.ok(ActionPlanResponseDTO.fromDocument(rejected));
    }

    @PostMapping("/{actionPlanId}/sync-jira")
    public ResponseEntity<ExternalSyncInfo> syncToJira(
            @PathVariable String actionPlanId,
            @RequestParam(defaultValue = "ENG") String projectKey) {
        ExternalSyncInfo syncInfo = jiraSyncAdapterService.syncActionPlanToJira(actionPlanId, projectKey);
        return ResponseEntity.ok(syncInfo);
    }

    @PostMapping("/webhooks/jira")
    public ResponseEntity<Map<String, Object>> handleJiraWebhook(@RequestBody Map<String, String> payload) {
        String issueKey = payload.get("issueKey");
        String status = payload.get("status");
        boolean updated = jiraSyncAdapterService.handleJiraWebhook(issueKey, status);
        return ResponseEntity.ok(Map.of("processed", updated, "issueKey", issueKey != null ? issueKey : "UNKNOWN"));
    }

    @PostMapping("/{actionPlanId}/sync-ms-planner")
    public ResponseEntity<ExternalSyncInfo> syncToPlanner(
            @PathVariable String actionPlanId,
            @RequestParam(defaultValue = "PLN-MAIN") String planId) {
        ExternalSyncInfo syncInfo = msPlannerSyncAdapterService.syncActionPlanToPlanner(actionPlanId, planId);
        return ResponseEntity.ok(syncInfo);
    }

    @PostMapping("/webhooks/ms-planner")
    public ResponseEntity<Map<String, Object>> handlePlannerWebhook(@RequestBody Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");
        Integer percent = (Integer) payload.get("percentComplete");
        boolean updated = msPlannerSyncAdapterService.handlePlannerWebhook(taskId, percent != null ? percent : 0);
        return ResponseEntity.ok(Map.of("processed", updated, "taskId", taskId != null ? taskId : "UNKNOWN"));
    }

    @GetMapping("/templates/recommendations")
    public ResponseEntity<List<ActionTemplateDocument>> getTemplateRecommendations(
            @RequestParam String groupId,
            @RequestParam(required = false) String keyword) {
        List<ActionTemplateDocument> recommendations = recommenderService.getRecommendations(groupId, keyword);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/kanban")
    public ResponseEntity<List<ActionPlanResponseDTO>> getKanbanActionBoard(
            @RequestParam String projectId,
            @RequestParam(required = false) String nodeId) {
        List<ActionPlanDocument> docs;
        if (nodeId != null && !nodeId.isBlank()) {
            docs = actionPlanRepository.findByProjectIdAndNodeId(projectId, nodeId);
        } else {
            docs = actionPlanRepository.findAll().stream()
                    .filter(d -> projectId.equals(d.getProjectId()))
                    .collect(Collectors.toList());
        }
        List<ActionPlanResponseDTO> response = docs.stream()
                .map(ActionPlanResponseDTO::fromDocument)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{actionPlanId}")
    public ResponseEntity<ActionPlanResponseDTO> getActionPlanById(@PathVariable String actionPlanId) {
        ActionPlanDocument doc = actionPlanRepository.findByActionPlanId(actionPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Action plan not found with ID: " + actionPlanId));
        return ResponseEntity.ok(ActionPlanResponseDTO.fromDocument(doc));
    }
}
