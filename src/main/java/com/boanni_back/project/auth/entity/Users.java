package com.boanni_back.project.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false)
    private EmployeeType employeeType;

    @Column(nullable = false)
    @Builder.Default
    private int score=0;

    @Builder.Default
    @Column(nullable = false)
    private Long currentQuestionIndex=-1L;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;
}

