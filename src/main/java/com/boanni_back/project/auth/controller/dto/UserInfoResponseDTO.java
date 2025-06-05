package com.boanni_back.project.auth.controller.dto;

import com.boanni_back.project.auth.entity.EmployeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
public class UserInfoResponseDTO {

    private String username;
    private EmployeeType employeeType;
    private int score;
}
