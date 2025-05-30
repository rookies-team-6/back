package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    // 보안 문제 전체 조회
    @GetMapping
    public ResponseEntity<List<QuestionDto.Response>> getAllQuestions() {
        List<QuestionDto.Response> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // 보안 문제 개별 조회 - Users의 current_question_index 참고
//    @GetMapping("/{userId}")
//    public ResponseEntity<QuestionDto.Response> getQuestionByIndex(@PathVariable Long userId){
//        return ResponseEntity.ok(questionService.getQuestionByIndex(userId));
//    }

    // 보안 문제 생성 (관리자만 허용)
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')") // <-- 중요: 관리자만 허용
//    public ResponseEntity<QuestionDto.Response> createQuestion(@RequestBody QuestionDto.Request request) {
//        QuestionDto.Response createQuestion = questionService.createQuestion(request);
//        return ResponseEntity.ok(createQuestion);
//    }
}
