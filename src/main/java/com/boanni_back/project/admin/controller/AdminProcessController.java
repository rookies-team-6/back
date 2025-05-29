package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.AdminProcessDto;
import com.boanni_back.project.admin.service.AdminProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminProcessController {

    private final AdminProcessService adminProcessService;

    //해당 id 회원의 학습 진행률 매핑
    @GetMapping("/progress/{id}")
    public ResponseEntity<AdminProcessDto> getProgress(@PathVariable Long id) {
        return ResponseEntity.ok(adminProcessService.getUserProgress(id));
    }
}