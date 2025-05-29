package com.boanni_back.project.ai.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.entity.UserAiRecord;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.ai.repository.UserAiRecordRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAiRecordService {

    private final UserAiRecordRepository userAiRecordRepository;
    private final QuestionRepository questionRepository;
    private final AdminRepository adminRepository;

    public UserAiRecordDto.Response saveUserAnswer(UserAiRecordDto.Request request) {
        // 연관 엔티티 조회
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INDEX_NOT_FOUND, request.getQuestionId()));

        Users user = adminRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getUserId()));

        // 엔티티 생성 및 저장
        UserAiRecord record = UserAiRecord.builder()
                .question(question)
                .users(user)
                .userAnswer(request.getUserAnswer())
                .build();
        userAiRecordRepository.save(record);

        // Dto로 응답
        return UserAiRecordDto.Response.fromEntity(record);
    }
}

