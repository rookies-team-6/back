package com.boanni_back.project.ai.controller;

import com.boanni_back.project.admin.service.AdminScoreService;
import com.boanni_back.project.ai.controller.dto.GroupAverageScoreDto;
import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/group")
public class GroupController {

    private final GroupService groupService;
    private final AdminScoreService adminScoreService;

    @GetMapping
    public ResponseEntity<List<GroupDto.Response>> getGroupList(){
        return ResponseEntity.ok(groupService.getGroupList());
    }

    // 특정 groupNum의 정보 조회
    @GetMapping("/{groupNum}")
    public ResponseEntity<GroupDto.Response> getGroupInfo(@PathVariable Long groupNum) {
        return ResponseEntity.ok(groupService.getGroupInfoByGroupNum(groupNum));
    }

    // 조별 평균 점수 모두 조회
    @GetMapping("/average-score")
    public ResponseEntity<List<GroupAverageScoreDto.Response>> getGroupAverageScores() {
        Map<Long, Integer> avgMap = groupService.getAverageScoreByGroupNum();

        List<GroupAverageScoreDto.Response> result = avgMap.entrySet().stream()
                .map(entry -> GroupAverageScoreDto.Response.builder()
                        .groupNum(entry.getKey())
                        .averageScore(entry.getValue())
                        .build())
                .toList();

        return ResponseEntity.ok(result);
    }

    // 조별 평균 점수 조회
    @GetMapping("/{groupNum}/average")
    public ResponseEntity<GroupAverageScoreDto.Response> getAverageScore(@PathVariable Long groupNum) {
        return ResponseEntity.ok(groupService.getGroupAverageScore(groupNum));
    }
}
