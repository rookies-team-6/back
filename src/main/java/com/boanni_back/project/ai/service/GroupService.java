package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public List<GroupDto.Response> getGroupList() {
        return groupRepository.findAll()
                .stream()
                .map(GroupDto.Response::fromEntity)
                .toList();
    }
}
