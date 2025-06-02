package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.controller.dto.VerifyRequestDTO;
import com.boanni_back.project.auth.controller.dto.VerifyResponseDTO;
import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.repository.EmployeeNumberRepository;
import com.boanni_back.project.auth.repository.UsersRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {
    private final EmployeeNumberRepository employeeNumberRepository;
    private final UsersRepository usersRepository;

    public EmployeeType getEmployeeType(String employeeNum){
//        사원 번호가 잘못 입력됐을 때,
        EmployeeNumber employeeRecord = employeeNumberRepository.findByEmployeeNum(employeeNum)
                .orElseThrow(()->new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR));
        String departmentCode = employeeRecord.getDepartmentCode();

//        사원 레코드 내부 department code가 공백일 때,
        if (departmentCode == null || departmentCode.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_NOT_FOUNT_TYPE);
        }

        char firstChar = departmentCode.charAt(0);
        return getType(firstChar);
    }

    private static EmployeeType getType(char firstChar) {
        switch (firstChar) {
            case 'T':
                return EmployeeType.TRAINEE;
            case 'E':
                return EmployeeType.EMPLOYEE;
            default:
                throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR);
        }
    }

    public VerifyResponseDTO verifyEmployeeAuth(VerifyRequestDTO request){


        EmployeeNumber employee = employeeNumberRepository
                .findByEmployeeNum(request.getEmployeeNum())
                .orElseThrow(()->new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR));

//        이미 Users 테이블에 등록이 돼어 있을 때,
        if(usersRepository.findByEmployeeNumber(employee).isPresent())
            throw new BusinessException(ErrorCode.AUTH_REGISTERED_EMPLOYEE_NUMBER_ERROR);


//        사용자 이름이 일치하지 않을 때,
        if(!employee.getUsername().equals(request.getUsername()))
            throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_NOT_EQUAL_USERNAME_EMPLOYEE_NUMBER)
        ;
        if(employee.isUsed()) throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR);

        return new VerifyResponseDTO(employee.getEmployeeNum());
    }
}
