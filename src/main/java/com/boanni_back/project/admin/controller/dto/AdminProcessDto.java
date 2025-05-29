package com.boanni_back.project.admin.controller.dto;

public class AdminProcessDto {
    private Long userId;
    private String username;
    private double progress;

    public AdminProcessDto(Long userId, String username, double progress) {
        this.userId = userId;
        this.username = username;
        this.progress = progress;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public double getProgress() { return progress; }
}