package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.service.QuestionService;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
//@PreAuthorize("hasRole('ADMIN')")   // 모든 메소드가 관리자만 접근 가능
@RequestMapping("/admin/questions")
public class QuestionAdminController {

    private final QuestionService questionService;

    // 보안 문제 전체 조회
    @GetMapping("/all")
    public String getAllQuestions(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(required = false) String keyword,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, size);

        Page<QuestionDto.Response> questionPage =
                (keyword == null || keyword.isBlank())
                        ? questionService.getAllQuestions(pageable)
                        : questionService.searchQuestions(keyword, pageable);

        model.addAttribute("page", questionPage);
        model.addAttribute("questions", questionPage.getContent());
        model.addAttribute("keyword", keyword);
        return "admin/questions/list";
    }

    // 보안 문제 생성 폼
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("questionForm", new QuestionDto.Request());
        return "admin/questions/create";
    }

    // 보안 문제 생성
    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("questionForm") QuestionDto.Request request,
                                 RedirectAttributes redirectAttributes) {
        try {
            questionService.createQuestion(request);
            redirectAttributes.addFlashAttribute("message", "문제가 성공적으로 등록되었습니다.");
            return "redirect:/admin/questions/all";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/questions/create";
        }
    }
}
