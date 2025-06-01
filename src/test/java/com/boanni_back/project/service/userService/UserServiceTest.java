//package com.boanni_back.project.service.userService;
//
//import com.boanni_back.project.auth.controller.dto.SignUpRequestDTO;
//import com.boanni_back.project.auth.entity.EmployeeType;
//import com.boanni_back.project.auth.entity.Users;
//import com.boanni_back.project.auth.repository.UsersRepository;
//import com.boanni_back.project.auth.service.UsersService;
//import com.boanni_back.project.exception.BusinessException;
//import com.boanni_back.project.exception.ErrorCode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UsersServiceTest {
//
//    @Mock
//    private UsersRepository usersRepository;
//
//    @InjectMocks
//    private UsersService usersService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private SignUpRequestDTO makeValidRequest() {
//        SignUpRequestDTO dto = new SignUpRequestDTO();
//        dto.setUsername("testuser");
//        dto.setEmail("test@domain.com");
//        dto.setPassword("1234");
//        dto.setPasswordCheck("1234");
//        dto.setEmployeeType(EmployeeType.TRAINEE);
//        return dto;
//    }
//
//    @Test
//    void 회원가입_성공() {
//        SignUpRequestDTO dto = makeValidRequest();
//
//        when(usersRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
//        when(usersRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
//
//        assertThatCode(() -> usersService.saveUser(dto))
//                .doesNotThrowAnyException();
//
//        verify(usersRepository).save(any(Users.class));
//    }
//
//    @Test
//    void 비밀번호불일치_예외() {
//        SignUpRequestDTO dto = makeValidRequest();
//        dto.setPasswordCheck("wrong");
//
//        assertThatThrownBy(() -> usersService.saveUser(dto))
//                .isInstanceOf(BusinessException.class)
//                .hasMessageContaining(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR.getMessage());
//    }
//
//    @Test
//    void 이메일중복_예외() {
//        SignUpRequestDTO dto = makeValidRequest();
//
//        when(usersRepository.findByEmail(dto.getEmail()))
//                .thenReturn(Optional.of(mock(Users.class)));
//
//        assertThatThrownBy(() -> usersService.saveUser(dto))
//                .isInstanceOf(BusinessException.class)
//                .hasMessageContaining(ErrorCode.AUTH_EMAIL_DUPLICATE_ERROR.getMessage());
//    }
//
//    @Test
//    void 유저네임중복_예외() {
//        SignUpRequestDTO dto = makeValidRequest();
//
//        when(usersRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
//        when(usersRepository.findByUsername(dto.getUsername()))
//                .thenReturn(Optional.of(mock(Users.class)));
//
//        assertThatThrownBy(() -> usersService.saveUser(dto))
//                .isInstanceOf(BusinessException.class)
//                .hasMessageContaining(ErrorCode.AUTH_USER_DUPLICATE_ERROR.getMessage());
//    }
//
//    @Test
//    void 허용되지않은_사원타입_예외() {
//        SignUpRequestDTO dto = makeValidRequest();
//        dto.setEmployeeType(EmployeeType.ADMIN); // 잘못된 타입
//
//        when(usersRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
//        when(usersRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> usersService.saveUser(dto))
//                .isInstanceOf(BusinessException.class)
//                .hasMessageContaining(ErrorCode.AUTH_NOT_INCLUDED_EMPLOYEE_NUMBER_ERROR.getMessage());
//    }
//}
