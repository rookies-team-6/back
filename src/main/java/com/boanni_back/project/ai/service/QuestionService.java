package com.boanni_back.project.ai.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AdminRepository adminRepository;

    // 보안 문제 전체 조회
    public List<QuestionDto.Response> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(QuestionDto.Response::fromEntity)
                .toList();
    }

    // 보안 문제 개별 조회
    @Transactional
    public QuestionDto.Response getQuestionByIndex(Long userId) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Long index = user.getCurrentQuestionIndex();

        Question question = questionRepository.findById(index)
                .orElseThrow(() -> new BusinessException(ErrorCode.INDEX_NOT_FOUND, index));

        // 다음 질문 index로 넘어감
        // 보류
        //user.setCurrentQuestionIndex(index + 1);
        adminRepository.save(user);

        return QuestionDto.Response.fromEntity(question);
    }

    // 보안 문제 생성
    public QuestionDto.Response createQuestion(QuestionDto.Request request) {
        Question question = new Question();
        question.setQuestion(request.getQuestion());
        return QuestionDto.Response.fromEntity(questionRepository.save(question));
    }


}