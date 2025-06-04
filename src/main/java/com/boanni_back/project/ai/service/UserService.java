package com.boanni_back.project.ai.service;

import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;

    public UsersDto.UserGroupScoresResponse getUserWithGroupScores(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 모든 조별 점수 계산
        List<Object[]> groupScoresRaw = usersRepository.findAllGroupScores();
        List<UsersDto.GroupScoreDto> groupScores = groupScoresRaw.stream()
                            .map(row -> new UsersDto.GroupScoreDto((Long)row[0], ((Number)row[1]).intValue()))
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
