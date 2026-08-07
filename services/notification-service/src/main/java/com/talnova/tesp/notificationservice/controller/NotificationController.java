package com.talnova.tesp.notificationservice.controller;

import com.talnova.tesp.common.context.ProjectContextHolder;
import com.talnova.tesp.notificationservice.domain.model.NotificationRecord;
import com.talnova.tesp.notificationservice.dto.NotificationRequestDTO;
import com.talnova.tesp.notificationservice.dto.NotificationResponseDTO;
import com.talnova.tesp.notificationservice.service.NotificationDispatcherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationDispatcherService dispatcherService;

    @Autowired
    public NotificationController(NotificationDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send notification via multi-channel dispatcher", description = "Dispatches email, SMS, Teams, Slack, or Kiosk PIN notification")
    public ResponseEntity<NotificationResponseDTO> sendNotification(
            @Valid @RequestBody NotificationRequestDTO request,
            HttpServletRequest servletRequest) {
        String resolvedProjectId = resolveProjectId(request.getProjectId(), servletRequest);
        request.setProjectId(resolvedProjectId);

        log.info("REST Ingress: Requesting notification dispatch to recipient '{}' via channel '{}'",
                request.getRecipient(), request.getChannel());

        NotificationRecord record = dispatcherService.dispatchNotification(request);

        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .notificationId(record.getNotificationId())
                .projectId(record.getProjectId())
                .recipient(record.getRecipient())
                .channel(record.getChannel())
                .status(record.getStatus())
                .sentAt(record.getSentAt())
                .errorMessage(record.getErrorMessage())
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/status/{notificationId}")
    @Operation(summary = "Fetch notification delivery status", description = "Returns notification record status and dispatch timestamp")
    public ResponseEntity<NotificationResponseDTO> getNotificationStatus(@PathVariable String notificationId) {
        log.info("REST Ingress: Fetching status for notificationId '{}'", notificationId);

        NotificationRecord record = dispatcherService.getNotificationStatus(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification record not found with ID: " + notificationId));

        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .notificationId(record.getNotificationId())
                .projectId(record.getProjectId())
                .recipient(record.getRecipient())
                .channel(record.getChannel())
                .status(record.getStatus())
                .sentAt(record.getSentAt())
                .errorMessage(record.getErrorMessage())
                .build();

        return ResponseEntity.ok(response);
    }

    private String resolveProjectId(String paramProjectId, HttpServletRequest request) {
        String contextProjectId = ProjectContextHolder.getProjectId();
        if (contextProjectId != null && !contextProjectId.isBlank()) {
            return contextProjectId;
        }
        if (request != null) {
            String headerProjectId = request.getHeader("X-Project-ID");
            if (headerProjectId != null && !headerProjectId.isBlank()) {
                return headerProjectId;
            }
        }
        if (paramProjectId != null && !paramProjectId.isBlank()) {
            return paramProjectId;
        }
        return "PRJ-DEFAULT";
    }
}
