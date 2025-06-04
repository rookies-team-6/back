package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.service.QuestionService;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    // 보안 문제 전체 조회
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllQuestions(Model model) {
        List<QuestionDto.Response> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "question/list";
    }

    // 보안 문제 개별 조회 - JSON API 유지
    @ResponseBody
    @GetMapping("/me")
    public ResponseEntity<QuestionDto.Response> getQuestionByIndex(Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        return ResponseEntity.ok(questionService.getQuestionByIndex(userId));
    }

    // 보안 문제 생성 폼
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("questionForm", new QuestionDto.Request());
        return "question/create";
    }

    // 보안 문제 생성
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createQuestion(@ModelAttribute("questionForm") QuestionDto.Request request,
                                 RedirectAttributes redirectAttributes) {
        try {
            questionService.createQuestion(request);
            redirectAttributes.addFlashAttribute("message", "문제가 성공적으로 등록되었습니다.");
            return "redirect:/questions/all";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/questions/create";
        }
    }
}
