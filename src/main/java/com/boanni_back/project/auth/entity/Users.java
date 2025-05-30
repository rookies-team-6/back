package com.boanni_back.project.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeType employeeType;

    @Column(nullable = false)
    @Builder.Default
    private int score = 0;

    @Builder.Default
    @Column(nullable = false)
    private Long currentQuestionIndex = 1L;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate questionSolveDeadline;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeNum", nullable = false)
    private EmployeeNumber employeeNumber;    //createdAt에 1년 더한 날짜를 questionSolveDeadline에 저장  @PrePersist    public void prePersist() {        this.createdAt = LocalDateTime.now();        this.questionSolveDeadline = LocalDate.now().plusYears(1);    }
}

