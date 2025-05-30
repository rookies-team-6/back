package com.boanni_back.project.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDeadlineDto {
    private LocalDate startDate;
    private LocalDate endDate;
}