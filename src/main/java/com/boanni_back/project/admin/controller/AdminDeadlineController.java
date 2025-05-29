package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.boanni_back.project.admin.service.AdminDeadlineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users/deadline")
public class AdminDeadlineController {

    private final AdminDeadlineService adminDeadlineService;

    public AdminDeadlineController(AdminDeadlineService adminDeadlineService) {
        this.adminDeadlineService = adminDeadlineService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDeadlineDto> getDeadline(@PathVariable Long id) {
        return ResponseEntity.ok(adminDeadlineService.getDeadline(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateDeadline(@PathVariable Long id,
                                               @RequestBody AdminDeadlineDto dto) {
        adminDeadlineService.setDeadline(id, dto);
        return ResponseEntity.ok().build();
    }
}

