package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminScoreService {

    private final AdminRepository adminRepository;
    private final QuestionRepository questionRepository;

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
}