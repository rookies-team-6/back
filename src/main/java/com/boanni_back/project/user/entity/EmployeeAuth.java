package com.boanni_back.project.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee_auth")
public class EmployeeAuth {
    @Id
    @Column(name = "employee_num")
    private String employeeNum;

    @Column(nullable = false)
    private boolean used;
}