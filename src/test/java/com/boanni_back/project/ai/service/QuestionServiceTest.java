package com.boanni_back.project.ai.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.ai.controller.dto.QuestionDto;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.repository.QuestionRepository;
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
        mockUser = Users.builder()
                .id(1L)
                .email("test@user.com")
                .password("encodedPassword123")
                .employeeType(EmployeeType.EMPLOYEE)
                .score(0)
                .currentQuestionIndex(100L)
                .employeeNumber(mock(EmployeeNumber.class))  // 또는 직접 생성
                .build();

        mockQuestion = Question.builder()
                        .id(100L)
                        .question("question1~")
                        .build();
    }

    // 모든 문제 조회 테스트
    @Test
    void getAllQuestions_returnsQuestionDtoList() {
        List<Question> questions = Arrays.asList(
                mockQuestion,
                Question.builder().id(101L).question("question2 is ~~~").build()
        );

        when(questionRepository.findAll()).thenReturn(questions);

        List<QuestionDto.Response> responses = questionService.getAllQuestions();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(1).getId()).isEqualTo(101L);
        assertThat(responses.get(1).getQuestion()).isEqualTo("question2 is ~~~");
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
        assertThat(response.getQuestion()).isEqualTo("question1~");
        assertThat(mockUser.getCurrentQuestionIndex()).isEqualTo(100L);
        verify(adminRepository).save(mockUser);
    }

    // 에러 확인
    @Test
    void getQuestionByIndex_userNotFound_throwsBusinessException() {
        when(adminRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getQuestionByIndex(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getQuestionByIndex_questionNotFound_throwsBusinessException() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(questionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getQuestionByIndex(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INDEX_NOT_FOUND);
    }

    // 생성 테스트
    @Test
    void createQuestion_savesAndReturnsQuestionDto() {
        QuestionDto.Request request = new QuestionDto.Request();
        request.setQuestion("new question is ha");

        Question savedQuestion = Question.builder()
                                .id(200L)
                                .question("new question is ha")
                                .build();

        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        QuestionDto.Response response = questionService.createQuestion(request);

        assertThat(response.getId()).isEqualTo(200L);
        assertThat(response.getQuestion()).isEqualTo("new question is ha");
    }
}
