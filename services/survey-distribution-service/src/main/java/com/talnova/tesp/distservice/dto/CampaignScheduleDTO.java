package com.talnova.tesp.distservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "DTO for campaign reminder schedule configuration")
public class CampaignScheduleDTO {

    @Schema(description = "List of scheduled reminder dates")
    private List<Instant> reminderDates = new ArrayList<>();

    @Schema(description = "Reminder nudge frequency in days", example = "4")
    private Integer reminderFrequencyDays;

    public CampaignScheduleDTO() {
    }

    public CampaignScheduleDTO(List<Instant> reminderDates, Integer reminderFrequencyDays) {
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

        public CampaignScheduleDTO build() {
            return new CampaignScheduleDTO(reminderDates, reminderFrequencyDays);
        }
    }
}
