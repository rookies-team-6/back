package com.boanni_back.project.ai.service;

import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;

    // 보안 문제 전체 조회 페이지네이션 추가
    public Page<QuestionDto.Response> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(QuestionDto.Response::fromEntity);
    }

    // 보안 문제 개별 조회
    @Transactional
    public QuestionDto.Response getQuestionByIndex(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
        Long index = user.getCurrentQuestionIndex();

        Question question = questionRepository.findById(index)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, index));

        boolean canSolve = !user.getQuestionSolveDeadline().isBefore(LocalDate.now());

        if (!canSolve) {
            throw new BusinessException(ErrorCode.USER_DEADLINE_EXPIRED, userId);
        }

        if (index > questionRepository.count()) {
            throw new BusinessException(ErrorCode.NO_MORE_QUESTION, index - 1);
        }
        usersRepository.save(user);

        return QuestionDto.Response.fromEntity(question, canSolve);
    }

    // 보안 문제 생성
    @Transactional
    public void createQuestion(QuestionDto.Request request) {
        // 문제 비어있을 때
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.NO_QUESTION);
        }

        Question question = Question.builder()
                .question(request.getQuestion())
                .build();

        questionRepository.save(question);
    }

    //문제 키워드 검색
    public Page<QuestionDto.Response> searchQuestions(String keyword, Pageable pageable) {
        return questionRepository.findByQuestionContaining(keyword, pageable)
                .map(QuestionDto.Response::fromEntity);
    }

}