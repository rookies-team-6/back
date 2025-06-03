package com.boanni_back.project.admin.controller;

import com.boanni_back.project.admin.controller.dto.DepartmentCodeScoreDto;
import com.boanni_back.project.admin.service.AdminScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dept-scores")
public class DepartmentCodeScoreController {

    private final AdminScoreService adminScoreService;

    // 조별 평균 점수 조회
    @GetMapping("/average")
    public ResponseEntity<List<DepartmentCodeScoreDto>> getAverageScore() {
        Map<String, Integer> avgMap = adminScoreService.getAverageScoreByDepartment();

        List<DepartmentCodeScoreDto> result = avgMap.entrySet().stream()
                .map(entry -> new DepartmentCodeScoreDto(entry.getKey(), entry.getValue()))
                .toList();

        return ResponseEntity.ok(result);
    }
}
