package com.boanni_back.project.ai.service;

import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UsersRepository usersRepository;

    public UsersDto.UserGroupScoresResponse getUserWithGroupScores(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        List<UsersDto.GroupScoreDto> groupScores = calculateAllGroupScores();
        UsersDto.UserHomeResponse userDto = createUserDto(user);

        return new UsersDto.UserGroupScoresResponse(userDto, groupScores);
    }

    // 각 조별 평균 구하기
    private List<UsersDto.GroupScoreDto> calculateAllGroupScores() {
        return usersRepository.findGroupAverages().stream()
                .map(row -> new UsersDto.GroupScoreDto(
                        (Long) row[0],
                        ((Double) row[1]).intValue()
                ))
                .collect(Collectors.toList());
    }

    private UsersDto.UserHomeResponse createUserDto(Users user) {
        return new UsersDto.UserHomeResponse(
                user.getId(),
                user.getEmployeeNumber().getUsername(),
                user.getGroupNum(),
                user.getEmployeeType(),
                user.getScore()
        );
    }
}