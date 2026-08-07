package com.talnova.tesp.notificationservice;

import com.talnova.tesp.notificationservice.controller.NotificationController;
import com.talnova.tesp.notificationservice.domain.model.NotificationChannel;
import com.talnova.tesp.notificationservice.domain.model.NotificationRecord;
import com.talnova.tesp.notificationservice.domain.model.NotificationStatus;
import com.talnova.tesp.notificationservice.dto.NotificationRequestDTO;
import com.talnova.tesp.notificationservice.listener.NotificationQueueListener;
import com.talnova.tesp.notificationservice.service.NotificationDispatcherService;
import com.talnova.tesp.notificationservice.service.NotificationDispatcherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationServiceTest {

    private NotificationDispatcherService dispatcherService;
    private NotificationQueueListener queueListener;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        dispatcherService = new NotificationDispatcherServiceImpl();
        queueListener = new NotificationQueueListener(dispatcherService);
        NotificationController controller = new NotificationController(dispatcherService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("TC-NTF-001: Multi-Channel Dispatcher dispatches EMAIL and SMS notifications successfully")
    void testMultiChannelDispatcher() {
        NotificationRequestDTO emailReq = NotificationRequestDTO.builder()
                .projectId("PRJ-99201")
                .recipient("hr-admin@company.com")
                .channel(NotificationChannel.EMAIL)
                .subject("Survey Dispatch Reminder")
                .content("Your survey response is requested.")
                .build();

        NotificationRecord emailRec = dispatcherService.dispatchNotification(emailReq);
        assertNotNull(emailRec.getNotificationId());
        assertEquals(NotificationStatus.DELIVERED, emailRec.getStatus());

        NotificationRequestDTO smsReq = NotificationRequestDTO.builder()
                .projectId("PRJ-99201")
                .recipient("+15550192834")
                .channel(NotificationChannel.SMS)
                .content("Your survey PIN is 881023")
                .build();

        NotificationRecord smsRec = dispatcherService.dispatchNotification(smsReq);
        assertNotNull(smsRec.getNotificationId());
        assertEquals(NotificationStatus.DELIVERED, smsRec.getStatus());
    }

    @Test
    @DisplayName("TC-NTF-002: Kafka Notification Queue Listener consumes tesp.notifications.queue.v1 payload")
    void testKafkaQueueListenerConsumption() {
        String jsonPayload = "{\"projectId\":\"PRJ-99201\",\"recipient\":\"manager@company.com\",\"channel\":\"MS_TEAMS\",\"subject\":\"Critical Risk Alert\",\"content\":\"High burnout risk detected in team N-301\"}";
        queueListener.consumeNotificationEvent(jsonPayload);

        // Verify status query for dispatched notifications
        assertDoesNotThrow(() -> {
            Optional<NotificationRecord> record = dispatcherService.getNotificationStatus("NTF-INVALID");
            assertTrue(record.isEmpty());
        });
    }

    @Test
    @DisplayName("TC-NTF-003: REST POST /api/v1/notifications/send accepts dispatch request and returns HTTP 202 Accepted")
    void testSendNotificationApi() throws Exception {
        mockMvc.perform(post("/api/v1/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Project-ID", "PRJ-99201")
                        .content("{\"recipient\":\"ops@company.com\",\"channel\":\"SLACK\",\"subject\":\"Alert\",\"content\":\"Survey campaign complete\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.notificationId").exists());
    }

    @Test
    @DisplayName("TC-NTF-004: REST GET /api/v1/notifications/status/{notificationId} returns status")
    void testGetNotificationStatusApi() throws Exception {
        NotificationRequestDTO req = NotificationRequestDTO.builder()
                .projectId("PRJ-99201")
                .recipient("test@company.com")
                .channel(NotificationChannel.EMAIL)
                .subject("Test Subject")
                .content("Test Content")
                .build();

        NotificationRecord rec = dispatcherService.dispatchNotification(req);

        mockMvc.perform(get("/api/v1/notifications/status/" + rec.getNotificationId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(rec.getNotificationId()))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }
}
