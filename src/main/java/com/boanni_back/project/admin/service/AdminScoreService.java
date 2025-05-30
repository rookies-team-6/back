package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    //해당 id 회원의 점수 조회
    public AdminScoreDto.Response getUserScoreById(Long id) {
        return adminRepository.findById(id)
                .map(AdminScoreDto.Response::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
    }

    //점수 내림차순 조회
    public List<AdminScoreDto.Response> getScoresSortedDesc() {
        return adminRepository.findAllByOrderByScoreDesc().stream()
                .map(AdminScoreDto.Response::fromEntity)
                .toList();
    }
}