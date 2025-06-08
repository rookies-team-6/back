package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.boanni_back.project.admin.service.AdminDeadlineService;
import com.boanni_back.project.admin.service.AdminService;
import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;
    private final AdminDeadlineService adminDeadlineService;

    //모든 회원 조회
    @GetMapping
    public String getAllUsers(@PageableDefault(size = 5) Pageable pageable, Model model) {
        Page<UsersDto.Response> page = adminService.getAllUsers(pageable);
        List<AdminDeadlineDto.Response> allUsers = adminDeadlineService.getAllUserDeadlines(); // 👈 모든 마감일

        model.addAttribute("users", page.getContent()); // 테이블용
        model.addAttribute("page", page);
        model.addAttribute("allUsers", allUsers);       // 달력용
        return "admin/users/list";
    }

    //해당 email로 회원 조회
    @GetMapping("/email/{email}")
    public String getUserByEmail(@PathVariable String email, Model model, RedirectAttributes redirectAttributes) {
        try {
            UsersDto.Response user = adminService.getUserByEmail(email);
            model.addAttribute("user", user);
            return "admin/users/detail";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    //employeeType으로 회원 조회
    @GetMapping("/type/{type}")
    public String getUsersByType(@PathVariable String type, Model model) {
        EmployeeType employeeType = EmployeeType.valueOf(type.toUpperCase());
        List<UsersDto.Response> users = adminService.getUsersByEmployeeType(employeeType);
        model.addAttribute("users", users);
        model.addAttribute("type", employeeType);
        return "admin/users/list_by_type";
    }

    //해당 username의 회원 조회
    @GetMapping("/username/{username}")
    public String getUserByUsername(@PathVariable String username, Model model, RedirectAttributes redirectAttributes) {
        try {
            UsersDto.Response user = adminService.getUserByUsername(username);
            model.addAttribute("user", user);
            return "admin/users/name";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    //해당 id 회원 삭제
    @PostMapping("/{id}/delete")
    public String deleteUserById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteUserById(id);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    //관리자로 권한 변경
    @PatchMapping("/{id}/role")
    public String promoteToAdmin(@PathVariable Long id) {
        adminService.promoteUserToAdmin(id);
        return "redirect:/admin/users";
    }
}