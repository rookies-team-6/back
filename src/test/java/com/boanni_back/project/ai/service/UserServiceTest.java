package com.boanni_back.project.ai.service;

import com.boanni_back.project.auth.controller.dto.UsersDto;
import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// 홈 화면 서비스 테스트
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserWithGroupScores_success() {
        Long userId = 1L;
        Long groupNum = 4L;
        EmployeeNumber employeeNumber = EmployeeNumber.builder()
                .employeeNum("A001")
                .departmentCode("T001")
                .username("김관리")
                .used(true)
                .build();

        Users user = Users.builder()
                .id(userId)
                .employeeNumber(employeeNumber)
                .groupNum(groupNum)
                .employeeType(EmployeeType.ADMIN)
                .score(90)
                .build();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Object[]> groupAverages = Arrays.asList(
                new Object[]{1L, 72.0},
                new Object[]{2L, 80.0},
                new Object[]{3L, 90.0}
                );
        when(usersRepository.findGroupAverages()).thenReturn(groupAverages);


        UsersDto.UserGroupScoresResponse response = userService.getUserWithGroupScores(userId);

        assertThat(response.getUser().getUserId()).isEqualTo(userId);
        assertThat(response.getUser().getName()).isEqualTo("김관리");
        assertThat(response.getUser().getGroupNum()).isEqualTo(groupNum);
        assertThat(response.getUser().getEmployeeType()).isEqualTo(EmployeeType.ADMIN);
        assertThat(response.getUser().getPersonalScore()).isEqualTo(90);

        assertThat(response.getGroupScores()).hasSize(3);
        assertThat(response.getGroupScores()).extracting("groupNum")
                .containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(response.getGroupScores()).filteredOn(gs -> gs.getGroupNum() == 3L)
                .extracting("groupScore").containsExactly(90);
    }

    @Test
    void getUserWithGroupScores_notFound() {
        Long userId = 990L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserWithGroupScores(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("해당 사용자를 찾을 수 없습니다");
    }
}