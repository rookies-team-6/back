package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.service.AdminScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users/scores")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScoreController {
    private final AdminScoreService adminScoreService;

    //모든 회원의 점수(score) 조회
    @GetMapping
    public String getAllScores(Model model) {
        List<AdminScoreDto.Response> scores = adminScoreService.getAllUserScores();
        model.addAttribute("scores", scores);
        return "admin/scores/list";
    }

    //모든 회원의 점수(score) 내림차순 조회
    @GetMapping("/sorted")
    public String getScoresSortedDesc(Model model) {
        List<AdminScoreDto.Response> scores = adminScoreService.getScoresSortedDesc();
        model.addAttribute("scores", scores);
        return "admin/scores/sorted";
    }
}
