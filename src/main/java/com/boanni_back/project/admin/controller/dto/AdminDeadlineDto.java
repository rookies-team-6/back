package com.boanni_back.project.admin.controller.dto;

import java.time.LocalDate;

public class AdminDeadlineDto {
    private LocalDate startDate;
    private LocalDate endDate;

    public AdminDeadlineDto() {}

    public AdminDeadlineDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
