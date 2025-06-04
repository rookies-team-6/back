package com.boanni_back.project.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
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

    public void markAsUsed() {
        this.used = true;
    }
}
