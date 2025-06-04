package com.boanni_back.project.board.entity;

import com.boanni_back.project.auth.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="board")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
//    내가 작성한 글 보려면 양방향 연관 관계 필요
    private Users users;

    @Column(nullable = false,length = 100)
    private String title;

    @Column(nullable = false,length = 500)
    private String contents;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
    }
}
