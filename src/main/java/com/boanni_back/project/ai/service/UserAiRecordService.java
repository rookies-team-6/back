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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor

public class UserAiRecordService {

    private final UserAiRecordRepository userAiRecordRepository;
    private final QuestionRepository questionRepository;
    private final AdminRepository adminRepository;

    // 사용자 답변 저장
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


        long nextIndex = user.getCurrentQuestionIndex() + 1;
        if (nextIndex > questionRepository.count()){    // 다음 문제 index가 등록된 문제 개수보다 클 경우
            throw new BusinessException(ErrorCode.NO_MORE_QUESTION, nextIndex - 1);
        }

        // 문제 인덱스 다음 문제로 넘어감
        user.setCurrentQuestionIndex(nextIndex);

        // Dto로 응답
        return UserAiRecordDto.Response.fromEntity(record);
    }

    public List<UserAiRecordDto.Response> getUserAnswer() {
        return userAiRecordRepository.findAll()
                .stream()
                .map(UserAiRecordDto.Response::fromEntity)
                .toList();
    }
}

