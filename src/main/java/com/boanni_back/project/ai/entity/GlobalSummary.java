package com.boanni_back.project.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "global_summary")
public class GlobalSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String summary;

    public void updateContent(String onlyTitle, String onlySummary) {
        this.title = onlyTitle;
        this.summary = onlySummary;
    }
}
