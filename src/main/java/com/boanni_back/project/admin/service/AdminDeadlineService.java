package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor //생성자 어노테이션 주입
public class AdminDeadlineService {

    private final AdminRepository adminRepository;

    public AdminDeadlineDto getDeadline(Long id) {
        Users user = adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));

        if (user.getStartDate() == null || user.getEndDate() == null) {
            throw new BusinessException(ErrorCode.USER_DEADLINE_NOT_FOUND, id);
        }

        return new AdminDeadlineDto(user.getStartDate(), user.getEndDate());
    }

    public void setDeadline(Long userId, AdminDeadlineDto dto) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        ErrorCode.USER_NOT_FOUND.getHttpStatus(),
                        ErrorCode.USER_NOT_FOUND.formatMessage(userId)
                ));

        LocalDate now = LocalDate.now();
        if (dto.getStartDate().isBefore(now)) {
            throw new ResponseStatusException(
                    ErrorCode.USER_DEADLINE_BEFORE_TODAY.getHttpStatus(),
                    ErrorCode.USER_DEADLINE_BEFORE_TODAY.getMessage()
            );
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new ResponseStatusException(
                    ErrorCode.USER_DEADLINE_BEFORE_START.getHttpStatus(),
                    ErrorCode.USER_DEADLINE_BEFORE_START.getMessage()
            );
        }

        user.setStartDate(dto.getStartDate());
        user.setEndDate(dto.getEndDate());
        adminRepository.save(user);
    }
}
