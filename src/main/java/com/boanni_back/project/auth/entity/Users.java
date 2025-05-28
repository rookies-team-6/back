package com.boanni_back.project.auth.entity;

import com.boanni_back.project.auth.entity.EmployeeType;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String pwd;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false)
    private EmployeeType employee_type;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private boolean quiz_completed;
}