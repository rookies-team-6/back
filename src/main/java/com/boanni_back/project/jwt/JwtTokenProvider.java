package com.boanni_back.project.jwt;

import com.boanni_back.project.auth.entity.CustomUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")         // application.properties에 설정
    private String secretKey;

    private final long expirationTime = 1000L * 60 * 60;

    private final long refreshTokenExpirationTime=7L*60*60*24;

    private Key key;

    @PostConstruct
    public void init() {
        // HS256용 키 생성
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Authentication 객체로부터 JWT 발급
    // AccessToken 발급 - CustomUserDetails 기준으로 설계
    public String generateAccessToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userDetails.getEmail())  // subject = email (username)
                .claim("id", userDetails.getId())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 발급 - roles는 굳이 안넣어도 됨
    public String generateRefreshToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(userDetails.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 username(email) 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
