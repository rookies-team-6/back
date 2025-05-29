package com.boanni_back.project.admin.controller.dto;

public class AdminScoreDto {
    private Long id;
    private String username;
    private int score;

    public AdminScoreDto(Long id, String username, int score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
}
