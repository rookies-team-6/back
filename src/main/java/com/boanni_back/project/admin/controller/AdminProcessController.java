package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminProcessDto;
import com.boanni_back.project.admin.service.AdminProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminProcessController {

    private final AdminProcessService adminProcessService;

    //해당 id 회원의 학습 진행률 매핑
    @GetMapping("/progress/{id}")
    public String getUserProgress(@PathVariable Long id, Model model) {
        AdminProcessDto.Response response = adminProcessService.getUserProgress(id);
        model.addAttribute("progress", response);
        return "admin/progress";
    }
}