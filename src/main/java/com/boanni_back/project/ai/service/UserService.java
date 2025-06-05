package com.boanni_back.project.ai.service;

import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UsersRepository usersRepository;

    public UsersDto.UserGroupScoresResponse getUserWithGroupScores(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 모든 조별 점수 계산
        List<Users> allUsers = usersRepository.findAll();

        // 조별 점수 합계와 인원수 집계
        Map<Long, List<Integer>> groupScoresMap = new HashMap<>();
        for (Users u : allUsers) {
            groupScoresMap
                    .computeIfAbsent(u.getGroupNum(), k -> new ArrayList<>())   // 기존에 있는 경우
                    .add(u.getScore());
        }

        // 조별 점수 평균 계산
        List<UsersDto.GroupScoreDto> groupScores = groupScoresMap.entrySet().stream()
                .map(entry -> {
                    Long groupNum = entry.getKey();
                    List<Integer> scores = entry.getValue();
                    int avg = scores.stream().mapToInt(Integer::intValue).sum() / scores.size();
                    return new UsersDto.GroupScoreDto(groupNum, avg);
                })
                .collect(Collectors.toList());

        // 사용자 정보 DTO 생성
        UsersDto.UserHomeResponse userDto = new UsersDto.UserHomeResponse(
                user.getId(),
                user.getEmployeeNumber().getUsername(),
                user.getGroupNum(),
                user.getEmployeeType(),
                user.getScore()
        );

        // 최종 응답 DTO
        return new UsersDto.UserGroupScoresResponse(userDto, groupScores);
    }

}
