package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminProgressDto;
import com.boanni_back.project.admin.service.AdminProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users/progress")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProgressController {

    private final AdminProgressService adminProcessService;

    // 전체 회원의 학습 진행률 조회
    @GetMapping
    public String getAllUserProgress(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 5;
        List<AdminProgressDto.Response> allUsers = adminProcessService.getAllUserProgress();

        int totalUsers = allUsers.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalUsers);

        List<AdminProgressDto.Response> pagedList = allUsers.subList(fromIndex, toIndex);

        // 페이지용 데이터
        model.addAttribute("progressList", pagedList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // 전체 차트용 데이터
        model.addAttribute("allUsers", allUsers);

        return "admin/progress/list";
    }

    //해당 id 회원의 학습 진행률 매핑
    @GetMapping("/{id}")
    public String getUserProgress(@PathVariable Long id, Model model) {
        AdminProgressDto.Response response = adminProcessService.getUserProgress(id);
        model.addAttribute("progress", response);
        return "admin/progress/detail";
    }
}