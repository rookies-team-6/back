package com.boanni_back.project.gpt.controller;

import com.boanni_back.project.gpt.controller.dto.QuestionDto;
import com.boanni_back.project.gpt.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // 보안 문제 전체 조회
    @GetMapping
    public ResponseEntity<List<QuestionDto.Response>> getAllQuestions() {
        List<QuestionDto.Response> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // 보안 문제 생성 (관리자만 허용)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // <-- 중요: 관리자만 허용
    public ResponseEntity<QuestionDto.Response> createQuestion(@RequestBody QuestionDto.Request request) {
        QuestionDto.Response createQuestion = questionService.createQuestion(request);
        return ResponseEntity.ok(createQuestion);
    }
}
