package com.talnova.tesp.distservice.adapter;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;

public interface NotificationChannelAdapter {

    DistributionChannel getSupportedChannel();

    DispatchResultDTO dispatchMessage(ChannelMessageDispatchDTO message);
}
