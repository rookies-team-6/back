package com.boanni_back.project.ai.service;

import com.boanni_back.project.ai.controller.dto.GroupAverageScoreDto;
import com.boanni_back.project.ai.controller.dto.GroupDto;
import com.boanni_back.project.ai.entity.Group;
import com.boanni_back.project.ai.entity.Question;
import com.boanni_back.project.ai.repository.GroupRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGroupList_success() {
        // given
        Group group = Group.builder()
                .id(1L)
                .title("안녕하세요")
                .summary("그룹 요약입니다")
                .question(mock(Question.class))
                .groupNum(1L)
                .build();
        given(groupRepository.findAll()).willReturn(List.of(group));

        // when
        List<GroupDto.Response> result = groupService.getGroupList();

        // then
        assertThat(result).hasSize(1);

        GroupDto.Response response = result.get(0);
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("안녕하세요");
        assertThat(response.getSummary()).isEqualTo("그룹 요약입니다");
        assertThat(response.getGroupNum()).isEqualTo(1L);
    }

    @Test
    void getGroupInfoByGroupNum_success() {
        Long groupNum = 1L;
        Group group = Group.builder()
                .id(2L)
                .title("TEST2")
                .summary("그룹 요약입니다")
                .question(mock(Question.class)) // 또는 실제 Question 객체
                .groupNum(1L)
                .build();
        given(groupRepository.findByGroupNum(groupNum)).willReturn(Optional.of(group));
        GroupDto.Response response = groupService.getGroupInfoByGroupNum(groupNum);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTitle()).isEqualTo("TEST2");
        assertThat(response.getSummary()).isEqualTo("그룹 요약입니다");
        assertThat(response.getGroupNum()).isEqualTo(1L);
    }

    @Test
    void getGroupInfoByGroupNum_fail_exception() {
        Long groupNum = 1L;
        given(groupRepository.findByGroupNum(groupNum)).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroupInfoByGroupNum(groupNum))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getAverageScoreByGroupNum_success() {
        given(questionRepository.count()).willReturn(5L);
        Users user1 = mock(Users.class);
        Users user2 = mock(Users.class);
        given(user1.getGroupNum()).willReturn(1L);
        given(user2.getGroupNum()).willReturn(1L);
        given(user1.getScore()).willReturn(400);
        given(user2.getScore()).willReturn(300);
        given(usersRepository.findAll()).willReturn(List.of(user1, user2));

        Map<Long, Integer> result = groupService.getAverageScoreByGroupNum();

        assertThat(result).containsKey(1L);
    }

    @Test
    void getGroupAverageScore_success() {
        Long groupNum = 2L;
        Group group = Group.builder()
                .id(groupNum)
                .title("안녕하세요")
                .summary("그룹 요약입니다")
                .question(mock(Question.class)) // 또는 실제 Question 객체
                .groupNum(2L)
                .build();
        given(groupRepository.findByGroupNum(groupNum)).willReturn(Optional.of(group));
        given(questionRepository.count()).willReturn(5L);

        Users user = mock(Users.class);
        given(user.getGroupNum()).willReturn(groupNum);
        given(user.getScore()).willReturn(400);
        given(usersRepository.findAll()).willReturn(List.of(user));

        GroupAverageScoreDto.Response response = groupService.getGroupAverageScore(groupNum);

        assertThat(response).isNotNull();
    }

    @Test
    void getGroupAverageScore_exception() {
        Long groupNum = 1L;
        given(groupRepository.findByGroupNum(groupNum)).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroupAverageScore(groupNum))
                .isInstanceOf(BusinessException.class);
    }
}
