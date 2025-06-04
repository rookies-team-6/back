package com.boanni_back.project.ai.controller;

import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/group")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDto.Response>> getGroupList(){
        return ResponseEntity.ok(groupService.getGroupList());
    }
}
