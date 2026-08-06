package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;

public interface AutomatedReminderSchedulerService {

    int executeScheduledReminderSequence();

    int processRemindersForCampaign(SurveyCampaignDocument campaign);
}
