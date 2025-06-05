package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.repository.GroupRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    // group 반환값
    public List<GroupDto.Response> getGroupList() {
        return groupRepository.findAll()
                .stream()
                .map(GroupDto.Response::fromEntity)
                .toList();
    }

    //특정 groupNum 답변 조회
    public GroupDto.Response getGroupInfoByGroupNum(Long groupNum) {
        Group group = groupRepository.findByGroupNum(groupNum)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND, groupNum));
        return GroupDto.Response.fromEntity(group);
    }
}
