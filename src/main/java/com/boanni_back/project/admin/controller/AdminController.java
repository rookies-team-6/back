package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.service.AdminService;
import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.EmployeeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

    //모든 회원 조회
    @GetMapping
    public String getAllUsers(Model model) {
        List<UsersDto.Response> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    //해당 email로 회원 조회
    @GetMapping("/{email}")
    public String getUserByEmail(@PathVariable String email, Model model) {
        UsersDto.Response user = adminService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "admin/users/detail";
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

    //해당 id 회원 삭제
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteUserById(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return "redirect:/admin/users";
    }

    //관리자로 권한 변경
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/role")
    public String promoteToAdmin(@PathVariable Long id) {
        adminService.promoteUserToAdmin(id);
        return "redirect:/admin/users";
    }
}