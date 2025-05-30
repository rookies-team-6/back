package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.service.AdminScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users/scores")
public class AdminScoreController {
    private final AdminScoreService adminScoreService;


    public AdminScoreController(AdminScoreService adminScoreService) {
        this.adminScoreService = adminScoreService;
    }
//    모든 회원의 점수(score) 조회
//    @GetMapping
//    public ResponseEntity<List<AdminScoreDto>> getAllScores() {
//        return ResponseEntity.ok(adminScoreService.getAllUserScores());
//    }
//    해당 id 회원의 점수(score) 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<AdminScoreDto> getUserScore(@PathVariable Long id) {
//        return ResponseEntity.ok(adminScoreService.getUserScoreById(id));
//    }
//    모든 회원의 점수(score) 내림차순 조회
//    @GetMapping("/sorted")
//    public ResponseEntity<List<AdminScoreDto>> getScoresSortedDesc() {
//        return ResponseEntity.ok(adminScoreService.getScoresSortedDesc());
//    }
}
