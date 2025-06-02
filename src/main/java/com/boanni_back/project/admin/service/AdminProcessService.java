package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminProcessDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.ai.repository.QuestionRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //생성자 어노테이션 주입
public class AdminProcessService {

    private final AdminRepository adminRepository;
    private final QuestionRepository questionRepository;

    public AdminProcessDto getUserProgress(Long userId) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        long index = user.getCurrentQuestionIndex();
        //Question 테이블의 question 갯수를 count하여 계산하는 방식입니다.
        long totalQuestions = questionRepository.count();

        double progress = (index < 0 || totalQuestions == 0) ? 0.0 : (index / (double) totalQuestions) * 100.0;
        String progressStr = String.format("%.0f%%", progress);

        return new AdminProcessDto(user.getId(), user.getUsername(), progressStr);
    }
}
