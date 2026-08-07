package com.talnova.tesp.notificationservice.service;

import com.talnova.tesp.notificationservice.domain.model.NotificationRecord;
import com.talnova.tesp.notificationservice.dto.NotificationRequestDTO;

import java.util.Optional;

public interface NotificationDispatcherService {

    NotificationRecord dispatchNotification(NotificationRequestDTO request);

    Optional<NotificationRecord> getNotificationStatus(String notificationId);
}
