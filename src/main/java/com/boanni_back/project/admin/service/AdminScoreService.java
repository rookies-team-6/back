package com.boanni_back.project.admin.service;

import com.boanni_back.project.admin.controller.dto.AdminScoreDto;
import com.boanni_back.project.admin.repository.AdminRepository;
import com.boanni_back.project.auth.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminScoreService {

    private final AdminRepository adminRepository;

    public AdminScoreService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<AdminScoreDto> getAllUserScores() {
        return adminRepository.findAll().stream()
                .map(u -> new AdminScoreDto(u.getId(), u.getUsername(), u.getScore()))
                .collect(Collectors.toList());
    }

    public AdminScoreDto getUserScoreById(Long id) {
        Users users = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        return new AdminScoreDto(users.getId(), users.getUsername(), users.getScore());
    }

    public List<AdminScoreDto> getScoresSortedDesc() {
        return adminRepository.findAllByOrderByScoreDesc().stream()
                .map(u -> new AdminScoreDto(u.getId(), u.getUsername(), u.getScore()))
                .collect(Collectors.toList());
    }
}