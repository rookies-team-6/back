package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.service.AdminScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users/scores")
public class AdminScoreController {
    private final AdminScoreService adminScoreService;

    public AdminScoreController(AdminScoreService adminScoreService) {
        this.adminScoreService = adminScoreService;
    }

    @GetMapping
    public ResponseEntity<List<AdminScoreDto>> getAllScores() {
        return ResponseEntity.ok(adminScoreService.getAllUserScores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminScoreDto> getUserScore(@PathVariable Long id) {
        return ResponseEntity.ok(adminScoreService.getUserScoreById(id));
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<AdminScoreDto>> getScoresSortedDesc() {
        return ResponseEntity.ok(adminScoreService.getScoresSortedDesc());
    }
}
