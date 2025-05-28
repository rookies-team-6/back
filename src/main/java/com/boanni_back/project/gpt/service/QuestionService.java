package com.boanni_back.project.gpt.service;

import com.boanni_back.project.gpt.controller.dto.QuestionDto;
import com.boanni_back.project.gpt.entity.Question;
import com.boanni_back.project.gpt.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    // 보안 문제 전체 조회
    public List<QuestionDto.Response> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(QuestionDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    // 보안 문제 생성
    public QuestionDto.Response createQuestion(QuestionDto.Request request) {
        Question question = new Question();
        question.setQuestion(request.getQuestion());
        return QuestionDto.Response.fromEntity(questionRepository.save(question));
    }


}