package com.boanni_back.project.ai.entity;

import com.boanni_back.project.auth.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Table(name = "user_ai_record")
public class UserAiRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userAnswer;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String aiAnswer;

    @Builder.Default
    @Setter
    @Column(nullable = false)
    private boolean isBookMarked = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Users users;
}