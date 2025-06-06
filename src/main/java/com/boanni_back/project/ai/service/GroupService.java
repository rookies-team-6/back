package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.GroupAverageScoreDto;
import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.repository.GroupRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;

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

    //부서 코드별 평균 점수 모두 조회
    public Map<Long, Integer> getAverageScoreByGroupNum() {
        long totalQuestions = questionRepository.count();
        int totalMaxScore = (int) (totalQuestions * 100);

        return usersRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Users::getGroupNum,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(user -> (double) user.getScore() / totalMaxScore * 100),
                                avg -> (int) Math.round(avg)
                        )
                ));
    }

    //부서 코드별 평균 점수 조회
    public GroupAverageScoreDto.Response getGroupAverageScore(Long groupNum) {
        Group group = groupRepository.findByGroupNum(groupNum)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND, groupNum));

        long totalQuestions = questionRepository.count();
        int totalMaxScore = (int) (totalQuestions * 100);

        List<Users> usersInGroup = usersRepository.findAll().stream()
                .filter(user -> Objects.equals(user.getGroupNum(), groupNum))
                .toList();

        double average = usersInGroup.stream()
                .mapToDouble(user -> (double) user.getScore() / totalMaxScore * 100)
                .average()
                .orElse(0.0);

        int roundedAverage = (int) Math.round(average);

        return GroupAverageScoreDto.Response.fromEntity(group, roundedAverage);
    }
}