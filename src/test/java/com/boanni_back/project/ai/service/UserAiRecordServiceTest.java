package com.boanni_back.project.ai.service;

import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.controller.dto.UserAiRecordDto;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.entity.UserAiRecord;
import com.boanni_back.project.ai.repository.UserAiRecordRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAiRecordServiceTest {

    @Mock
    private UserAiRecordRepository userAiRecordRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private UserAiRecordService userAiRecordService;
    private UserAiRecord record;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Users user = Users.builder().id(1L).currentQuestionIndex(3L).build();
        Question question = Question.builder().id(2L).build();
        record = UserAiRecord.builder()
                .id(1L)
                .question(question)
                .users(user)
                .userAnswer("My answer is umm")
                .isBookMarked(true)
                .build();
    }

    // 푼 문제 찾기 성공!
    @Test
    void getSolvedRecord_success() {
        // Id 1이면 index 3으로, 그 조건으로 푼 문제 찾기
        when(adminRepository.findCurrentQuestionIndexById(1L)).thenReturn(3L);
        when(userAiRecordRepository.findByUsersIdAndQuestionIdLessThanEqualOrderByQuestionIdAsc(1L, 3L))
                .thenReturn(Optional.of(List.of(record)));
        List<UserAiRecordDto.Response> result = userAiRecordService.getSolvedRecord(1L);

        assertEquals(1, result.size());
        assertEquals("My answer is umm", result.get(0).getUserAnswer());

        // 한 번 호출
        verify(userAiRecordRepository).findByUsersIdAndQuestionIdLessThanEqualOrderByQuestionIdAsc(1L, 3L);
    }

    // 푼 문제 찾기 실패!
    @Test
    void getSolvedRecord_notFound() {
        // index 1로
        when(adminRepository.findCurrentQuestionIndexById(1L)).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                userAiRecordService.getSolvedRecord(1L)
        );
        assertEquals(ErrorCode.SOLVED_RECORD_NOT_FOUND, ex.getErrorCode());
    }

    // 푼 문제 중 북마크한 문제만 찾기 성공!
    @Test
    void getBookMarkedRecord_success() {
        // user id 1, isBookMarked true일 때
        when(userAiRecordRepository.findByUsersIdAndIsBookMarkedTrue(1L))
                .thenReturn(Optional.of(List.of(record)));
        List<UserAiRecordDto.Response> result = userAiRecordService.getBookMarkedRecord(1L);

        // 확인
        assertEquals(1, result.size());
        assertTrue(result.get(0).isBookMarked());
        // 한 번 호출
        verify(userAiRecordRepository).findByUsersIdAndIsBookMarkedTrue(1L);
    }

    // 실패!
    @Test
    void getBookMarkedRecord_notFound() {
        // isBookMarked false이도록 만듦
        when(userAiRecordRepository.findByUsersIdAndIsBookMarkedTrue(1L))
                .thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                userAiRecordService.getBookMarkedRecord(1L)
        );
        assertEquals(ErrorCode.BOOKMARKED_RECORD_NOT_FOUND, ex.getErrorCode());
    }
}
