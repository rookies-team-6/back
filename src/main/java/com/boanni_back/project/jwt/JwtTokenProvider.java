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

    private Key key;

    @PostConstruct
    public void init() {
        // HS256용 키 생성
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Authentication 객체로부터 JWT 발급
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);
        Long id=userDetails.getId();


        return Jwts.builder()
                .setSubject(username)
                .claim("id",id)
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // (후속 요청용) 토큰에서 사용자명 꺼내기
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // (후속 요청용) 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 만료, 서명 불일치 등
            return false;
        }
    }
}
