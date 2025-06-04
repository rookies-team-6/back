package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminScoreService {

    private final AdminRepository adminRepository;

    //모든 회원의 점수 조회
    public List<AdminScoreDto.Response> getAllUserScores() {
        return adminRepository.findAll().stream()
                .map(AdminScoreDto.Response::fromEntity)
                .toList();
    }

    //점수 내림차순 조회
    public List<AdminScoreDto.Response> getScoresSortedDesc() {
        return adminRepository.findAllByOrderByScoreDesc().stream()
                .map(AdminScoreDto.Response::fromEntity)
                .limit(10)
                .toList();
    }

    //부서 코드별 평균 점수 조회
    public Map<String, Integer> getAverageScoreByDepartment() {
        return adminRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        user -> user.getEmployeeNumber().getDepartmentCode(),
                        Collectors.collectingAndThen(
                                Collectors.averagingInt(Users::getScore),
                                avg -> (int) Math.round(avg) // 소수점 반올림 후 정수 반환
                        )
                ));
    }
}