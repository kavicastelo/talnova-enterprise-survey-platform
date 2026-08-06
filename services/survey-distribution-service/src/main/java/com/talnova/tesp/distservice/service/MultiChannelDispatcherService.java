package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;

import java.util.List;

public interface MultiChannelDispatcherService {

    DispatchResultDTO dispatchSingleMessage(ChannelMessageDispatchDTO message);

    List<DispatchResultDTO> dispatchBatchMessages(List<ChannelMessageDispatchDTO> messages);
}
