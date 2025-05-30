package com.boanni_back.project.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "employee_number")
public class EmployeeNumber {
    @Id
    @Column(length = 50, nullable = false)
    private String employeeNum;

    @Column(length = 100, nullable = false)
    private String username;

    @Column(length = 4, nullable = false)
    private String departmentCode;

    @Column(nullable = false)
    private boolean used;

}