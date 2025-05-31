package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminDeadlineDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor //생성자 어노테이션 주입
public class AdminDeadlineService {

    private final AdminRepository adminRepository;

    // 마감일 조회
    public AdminDeadlineDto.Response getDeadline(Long userId) {
        Users user = adminRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        return AdminDeadlineDto.Response.fromEntity(user, user.getQuestionSolveDeadline());
    }

    // 마감일 수정
    public Users updateDeadline(AdminDeadlineDto.Request request) {
        Users user = adminRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getUserId()));

        LocalDate oldDeadline = user.getQuestionSolveDeadline();
        LocalDate newDeadline = request.getNewDeadline();
        LocalDate now = LocalDate.now();

        if (newDeadline.isBefore(now)) {
            throw new BusinessException(ErrorCode.USER_DEADLINE_BEFORE_TODAY, request.getUserId());
        }

        if (newDeadline.equals(oldDeadline)) {
            throw new BusinessException(ErrorCode.USER_DEADLINE_SAME_AS_BEFORE, request.getUserId());
        }

        user.setQuestionSolveDeadline(newDeadline);
        return adminRepository.save(user);
    }

}
