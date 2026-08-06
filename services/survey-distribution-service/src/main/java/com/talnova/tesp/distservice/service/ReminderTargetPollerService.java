package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.dto.ReminderNudgeTargetDTO;

import java.util.List;

public interface ReminderTargetPollerService {

    List<ReminderNudgeTargetDTO> pollUncompletedReminderTargets(String projectId, String campaignId);
}
