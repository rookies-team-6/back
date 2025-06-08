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

        Question mockQuestion = mock(Question.class);
        given(mockQuestion.getId()).willReturn(100L);
        given(mockQuestion.getQuestion()).willReturn("질문 내용");

        Group group = Group.builder()
                .id(2L)
                .title("TEST2")
                .summary("그룹 요약입니다")
                .question(mockQuestion)
                .groupNum(1L)
                .build();

        // 리스트로 감싸야 함
        given(groupRepository.findByGroupNumWithQuestion(groupNum)).willReturn(List.of(group));

        List<GroupDto.Response> responseList = groupService.getGroupInfoByGroupNum(groupNum);

        assertThat(responseList).isNotNull();
        assertThat(responseList).isNotEmpty();

        GroupDto.Response response = responseList.get(0);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTitle()).isEqualTo("TEST2");
        assertThat(response.getSummary()).isEqualTo("그룹 요약입니다");
        assertThat(response.getGroupNum()).isEqualTo(1L);
        assertThat(response.getQuestionId()).isEqualTo(100L);
        assertThat(response.getQuestionName()).isEqualTo("질문 내용");
    }

    @Test
    void getGroupInfoByGroupNum_fail_exception() {
        Long groupNum = 1L;

        // 빈 리스트로 반환
        given(groupRepository.findByGroupNumWithQuestion(groupNum)).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> groupService.getGroupInfoByGroupNum(groupNum))
                .isInstanceOf(BusinessException.class);
    }
}
