package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.MessageTemplateRequestDTO;

import java.util.List;

public interface DistributionChannelRouter {

    ChannelMessageDispatchDTO buildAndHydrateMessage(MessageTemplateRequestDTO request);

    List<ChannelMessageDispatchDTO> prepareBatchDispatch(String projectId, String campaignId, String campaignTitle, List<GeneratedTokenDTO> tokens, List<DistributionChannel> channels, String locale);
}
