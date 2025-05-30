package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.repository.EmployeeAuthRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {
    private final EmployeeAuthRepository employeeAuthRepository;

    public void verifyEmployeeAuth(String employeeNum){
        EmployeeNumber employee = employeeAuthRepository.getEmployeeAuthByEmployeeNum(employeeNum).orElseThrow(()-> new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR));
        if(employee.isUsed()) throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR);
    }
}
