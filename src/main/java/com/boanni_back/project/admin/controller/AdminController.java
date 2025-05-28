package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.service.AdminService;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Users> getUserByEmail(@PathVariable String email) {
        Users users = adminService.getUserByEmail(email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Users>> getUsersByType(@PathVariable String type) {
        EmployeeType employeeType = EmployeeType.valueOf(type.toUpperCase());
        List<Users> users = adminService.getUsersByEmployeeType(employeeType);
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
