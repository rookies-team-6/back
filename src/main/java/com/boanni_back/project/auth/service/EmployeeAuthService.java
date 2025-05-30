package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.controller.dto.VerifyRequestDTO;
import com.boanni_back.project.auth.controller.dto.VerifyResponseDTO;
import com.boanni_back.project.auth.entity.EmployeeNumber;
import com.boanni_back.project.auth.repository.EmployeeNumberRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {
    private final EmployeeNumberRepository employeeNumberRepository;

    public VerifyResponseDTO verifyEmployeeAuth(VerifyRequestDTO request){
        EmployeeNumber employee = employeeNumberRepository
                .findByEmployeeNum(request.getEmployeeNum()).orElseThrow(()-> new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR));

        if(!employee.getUsername().equals(request.getUsername())) throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_NOT_EQUAL_USERNAME_EMPLOYEE_NUM);
        if(employee.isUsed()) throw new BusinessException(ErrorCode.EMPLOYEE_AUTH_ERROR);

        return new VerifyResponseDTO(employee.getEmployeeNum(), employee.getUsername(), employee.getDepartmentCode());
    }
}
