package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminProgressDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProgressService {

    private final AdminRepository adminRepository;
    private final QuestionRepository questionRepository;

    public List<AdminProgressDto.Response> getAllUserProgress() {
        long totalQuestions = questionRepository.count();

        return adminRepository.findAll().stream()
                .map(user -> AdminProgressDto.Response.fromEntity(user, totalQuestions))
                .collect(Collectors.toList());
    }

    public AdminProgressDto.Response getUserProgress(Long userId) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        long totalQuestions = questionRepository.count();

        return AdminProgressDto.Response.fromEntity(user, totalQuestions);
    }
}
