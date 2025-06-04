package com.boanni_back.project.auth.service;

import com.boanni_back.project.auth.entity.RefreshToken;
import com.boanni_back.project.auth.repository.RefreshTokenRepository;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 저장 또는 갱신
    public void saveOrUpdate(Long userId, String refreshToken, long expiresInDays) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken());

        token.setUserId(userId);
        token.setRefreshToken(refreshToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        refreshTokenRepository.save(token);
    }

    // DB에 저장된 토큰 검증
    public void validateRefreshToken(Long userId, String requestToken) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));

        if (!token.getRefreshToken().equals(requestToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    // 새 토큰 갱신
    public void updateRefreshToken(Long userId, String newRefreshToken, long expiresInDays) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));

        token.setRefreshToken(newRefreshToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        refreshTokenRepository.save(token);
    }

    // 로그아웃 등에서 삭제용
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);
    }
}
