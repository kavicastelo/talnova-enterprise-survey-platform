package com.talnova.tesp.distservice.messaging;

import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.NotificationDispatchEventDTO;

import java.util.List;

public interface NotificationKafkaProducer {

    void publishDispatchEvent(ChannelMessageDispatchDTO dispatch);

    void publishBatchDispatchEvents(List<ChannelMessageDispatchDTO> dispatches);

    NotificationDispatchEventDTO toEventDTO(ChannelMessageDispatchDTO dispatch);
}
