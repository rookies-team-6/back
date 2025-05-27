package com.boanni_back.project.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_gpt_record")
public class UserGptRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userAnswer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String gptAnswer;

    @Column(nullable = false)
    private boolean isBookMarked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}