package com.boanni_back.project.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "employee_auth")
public class EmployeeAuth {
    @Id
    @Column(name = "employee_num")
    private String employeeNum;

    @Column(nullable = false)
    private boolean used;
}