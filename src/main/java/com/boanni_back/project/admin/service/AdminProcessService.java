package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminProcessDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //생성자 어노테이션 주입
public class AdminProcessService {

    private final AdminRepository adminRepository;

    private final int TOTAL_QUESTIONS = 5;

    public AdminProcessDto getUserProgress(Long userId) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        long index = user.getCurrentQuestionIndex();
        double progress = (index < 0) ? 0.0 : (index / (double) TOTAL_QUESTIONS) * 100.0;

        return new AdminProcessDto(user.getId(), user.getUsername(), progress);
    }
}
