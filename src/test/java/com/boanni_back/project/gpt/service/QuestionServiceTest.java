package com.boanni_back.project.gpt.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.gpt.controller.dto.QuestionDto;
import com.boanni_back.project.gpt.entity.Question;
import com.boanni_back.project.gpt.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AdminRepository adminRepository;

    private Users mockUser;
    private Question mockQuestion;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setCurrent_question_index(100L);

        mockQuestion = new Question();
        mockQuestion.setId(100L);
        mockQuestion.setQuestion("질문1");
    }

    // 모든 문제 조회 테스트
    @Test
    void getAllQuestions_returnsQuestionDtoList() {
        List<Question> questions = Arrays.asList(
                mockQuestion,
                new Question(101L, "질문2")
        );

        when(questionRepository.findAll()).thenReturn(questions);

        List<QuestionDto.Response> responses = questionService.getAllQuestions();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(1).getId()).isEqualTo(101L);
        assertThat(responses.get(1).getQuestion()).isEqualTo("질문2");
    }

    // index를 통한 조회 테스트
    @Test
    void getQuestionByIndex_validUserAndIndex_returnsQuestionDto() {
        // 유저, 질문 설정
        when(adminRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(questionRepository.findById(100L)).thenReturn(Optional.of(mockQuestion));
        when(adminRepository.save(any())).thenReturn(mockUser);

        QuestionDto.Response response = questionService.getQuestionByIndex(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getQuestion()).isEqualTo("질문1");
        assertThat(mockUser.getCurrent_question_index()).isEqualTo(101L);
        verify(adminRepository).save(mockUser);
    }

    // 에러 확인
    @Test
    void getQuestionByIndex_userNotFound_throwsBusinessException() {
        when(adminRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = catchThrowableOfType(
                () -> questionService.getQuestionByIndex(999L),
                BusinessException.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getQuestionByIndex_questionNotFound_throwsBusinessException() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(questionRepository.findById(100L)).thenReturn(Optional.empty());

        BusinessException exception = catchThrowableOfType(
                () -> questionService.getQuestionByIndex(1L),
                BusinessException.class);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INDEX_NOT_FOUND);
    }

    // 생성 테스트
    @Test
    void createQuestion_savesAndReturnsQuestionDto() {
        QuestionDto.Request request = new QuestionDto.Request();
        request.setQuestion("새 질문");

        Question savedQuestion = new Question();
        savedQuestion.setId(200L);
        savedQuestion.setQuestion("새 질문");

        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        QuestionDto.Response response = questionService.createQuestion(request);

        assertThat(response.getId()).isEqualTo(200L);
        assertThat(response.getQuestion()).isEqualTo("새 질문");
    }
}
