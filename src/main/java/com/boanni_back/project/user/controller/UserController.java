package com.boanni_back.project.user.controller;

import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.entity.User;
import com.boanni_back.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/rds-test")
    public ResponseEntity<String> testRdsInsert() {
        User user = User.builder()
                .email("test@boanni.com")
                .pwd("secure1234")
                .username("테스트유저")
                .employee_type(EmployeeType.EMPLOYEE)
                .score(0)
                .quiz_completed(false)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("✅ RDS insert 성공 (id=" + user.getId() + ")");
    }

    @GetMapping("/rds-test")
    public ResponseEntity<String> testRdsCount() {
        long count = userRepository.count();
        return ResponseEntity.ok("📦 RDS user 수: " + count);
    }

    @PostMapping("/redis-test")
    public ResponseEntity<String> redisTest() {
        redisTemplate.opsForValue().set("test-key", "테스트 값");
        return ResponseEntity.ok("✅ Redis set 성공");
    }

    @GetMapping("/redis-test")
    public ResponseEntity<String> getRedisTest() {
        String value = redisTemplate.opsForValue().get("test-key");
        return ResponseEntity.ok("📦 Redis 값: " + value);
    }

}
