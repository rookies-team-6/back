package com.boanni_back.project.ai.controller;

import com.boanni_back.project.admin.service.AdminScoreService;
import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.service.GroupService;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<GroupDto.Response>> getGroupInfo(@PathVariable Long groupNum) {
        return ResponseEntity.ok(groupService.getGroupInfoByGroupNum(groupNum));
    }

    // 특정 groupNum과 questionId로 정보 디테일 조회
    @GetMapping("/{groupNum}/{questionId}")
    public ResponseEntity<GroupDto.Response> getGroupDetail(@PathVariable Long groupNum,@PathVariable Long questionId) {
        return ResponseEntity.ok(groupService.getGroupDetail(groupNum, questionId));
    }
}
