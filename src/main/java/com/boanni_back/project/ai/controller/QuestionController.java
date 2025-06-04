package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.service.QuestionService;
import com.boanni_back.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
  
    // 보안 문제 개별 조회 - JSON API 유지
    @GetMapping("/me")
    public ResponseEntity<QuestionDto.Response> getQuestionByIndex(Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        return ResponseEntity.ok(questionService.getQuestionByIndex(userId));
    }
}
