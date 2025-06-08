package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.repository.GroupRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public List<GroupDto.Response> getGroupInfoByGroupNum(Long groupNum) {
        List<Group> groups = groupRepository.findByGroupNumWithQuestion(groupNum);
        if (groups == null || groups.isEmpty()) {
            throw new BusinessException(ErrorCode.GROUP_NOT_FOUND, groupNum);
        }
        return groups.stream()
                .map(GroupDto.Response::fromEntity)
                .toList();
    }

    // 특정 groupNum과 questionId로 정보 디테일 조회
    public GroupDto.Response getGroupDetail(Long groupNum, Long questionId) {
        Group group = groupRepository.findByGroupNumAndQuestion_Id(groupNum, questionId)
                .orElseThrow(()-> new BusinessException(ErrorCode.GROUP_NOT_FOUND, groupNum));
        return GroupDto.Response.fromEntity(group);

    }
}