package com.boanni_back.project.auth.controller;

import com.boanni_back.project.ai.service.UserService;
import com.boanni_back.project.auth.controller.dto.*;
import com.boanni_back.project.auth.entity.CustomUserDetails;
import com.boanni_back.project.auth.entity.EmployeeType;
import com.boanni_back.project.auth.service.CustomUserDetailsService;
import com.boanni_back.project.auth.service.EmployeeAuthService;
import com.boanni_back.project.auth.service.RefreshTokenService;
import com.boanni_back.project.auth.service.UsersService;
import com.boanni_back.project.exception.BusinessException;
import com.boanni_back.project.exception.ErrorCode;
import com.boanni_back.project.jwt.JwtTokenProvider;
import com.boanni_back.project.util.SecurityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController  {
    private final EmployeeAuthService employeeAuthService;
    private final UsersService usersService;
    private final RefreshTokenService refreshTokenService;


    //    사원 번호 확인 컨트롤러
    @GetMapping("/verify")
    public ResponseEntity<VerifyResponseDTO> verifyEmployeeNum(@ModelAttribute VerifyRequestDTO request){
        VerifyResponseDTO response=employeeAuthService.verifyEmployeeAuth(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO request){
        EmployeeType employeeType=employeeAuthService.getEmployeeType(request.getEmployeeNum());
        SignUpResponseDTO response=usersService.saveUser(request,employeeType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(userDetails.getUsername());
    }

    @PostMapping("/signin")
    public ResponseEntity<AccessTokenResponseDTO> signIn(@RequestBody SignInRequestDTO request, HttpServletResponse response) {
        SignInResponseDTO tokenResponse = usersService.signIn(request);

        // RefreshToken을 HttpOnly 쿠키로 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)  // 개발환경 http에서는 false, 운영에서는 true
                .sameSite("Lax") // 개발에서는 Lax (운영은 None)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", refreshTokenCookie.toString());


        return ResponseEntity.ok(new AccessTokenResponseDTO(tokenResponse.getAccessToken()));
    }

    @GetMapping("/email/check")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email){
        boolean isDuplicate = usersService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDTO> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        SignInResponseDTO newTokens = usersService.refreshToken(refreshToken);

        // 새로운 RefreshToken으로 쿠키 재설정
        Cookie refreshTokenCookie = getCookie(newTokens.getRefreshToken());

        response.addCookie(refreshTokenCookie);

        // AccessToken만 응답
        return ResponseEntity.ok(new AccessTokenResponseDTO(newTokens.getAccessToken()));

    }

    private static Cookie getCookie(String newTokens) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", newTokens);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        return refreshTokenCookie;
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = SecurityUtil.extractUserId(authentication);
        refreshTokenService.deleteRefreshToken(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserInfoResponseDTO> userInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = SecurityUtil.extractUserId(authentication);
        UserInfoResponseDTO response = usersService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }


}

