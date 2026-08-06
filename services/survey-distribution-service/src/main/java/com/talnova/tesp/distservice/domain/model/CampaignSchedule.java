package com.talnova.tesp.distservice.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CampaignSchedule {
    private List<Instant> reminderDates = new ArrayList<>();
    private Integer reminderFrequencyDays;

    public CampaignSchedule() {
    }

    public CampaignSchedule(List<Instant> reminderDates, Integer reminderFrequencyDays) {
        this.reminderDates = reminderDates != null ? reminderDates : new ArrayList<>();
        this.reminderFrequencyDays = reminderFrequencyDays;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Instant> getReminderDates() { return reminderDates; }
    public void setReminderDates(List<Instant> reminderDates) { this.reminderDates = reminderDates; }

    public Integer getReminderFrequencyDays() { return reminderFrequencyDays; }
    public void setReminderFrequencyDays(Integer reminderFrequencyDays) { this.reminderFrequencyDays = reminderFrequencyDays; }

    public static class Builder {
        private List<Instant> reminderDates = new ArrayList<>();
        private Integer reminderFrequencyDays;

        public Builder reminderDates(List<Instant> reminderDates) { this.reminderDates = reminderDates; return this; }
        public Builder reminderFrequencyDays(Integer reminderFrequencyDays) { this.reminderFrequencyDays = reminderFrequencyDays; return this; }

        public CampaignSchedule build() {
            return new CampaignSchedule(reminderDates, reminderFrequencyDays);
        }
    }
}
