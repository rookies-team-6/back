package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.entity.EmployeeAuth;
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
        System.out.println(employeeNum);
        EmployeeAuth employee = employeeAuthRepository.findByEmployeeNum(employeeNum).orElseThrow(()-> new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR));
        System.out.println(employee.getEmployeeNum());
        System.out.println(employee.isUsed());
        if(employee.isUsed()) throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR);

        employee.setUsed(true);
        employeeAuthRepository.save(employee);
    }
}
