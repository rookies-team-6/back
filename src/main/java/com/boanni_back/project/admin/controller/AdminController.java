package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.service.AdminService;
import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UsersDto>> getAllUsers() {
        List<UsersDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UsersDto> getUserByEmail(@PathVariable String email) {
        UsersDto user = adminService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<UsersDto>> getUsersByType(@PathVariable String type) {
        EmployeeType employeeType = EmployeeType.valueOf(type.toUpperCase());
        List<UsersDto> users = adminService.getUsersByEmployeeType(employeeType);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable Long id) {
        adminService.promoteUserToAdmin(id);
        return ResponseEntity.ok().build();
    }
}