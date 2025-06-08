# Thymeleaf 화면 설계

## 1. 템플릿 레이아웃

### 1.1 템플릿 구조
```
templates/
├── fragments/
│   ├── header-main.html            # 메인 페이지 상단 헤더 프래그먼트
│   └── header-detail.html            # 상세 페이지 상단 헤더 프래그먼트
└── admin/
    ├── users/                 # 사용자 관련 템플릿
    │   ├── list.html            # 메인 페이지(회원 목록 및 학습 마감일 달력)
    │   ├── detail.html          # 회원 상세 정보 페이지
    │   ├── list_by_type.html    # 회원 유형 필터링 검색 페이지
    │   └── name.html            # 회원 이름 검색 페이지
    ├── questions/             # 질문 관련 템플릿
    │   ├── list.html            # 질문 목록 페이지
    │   └── create.html          # 질문 추가 페이지
    ├── progress/              # 학습률 관련 템플릿
    │   ├── list.html            # 회원 학습률 목록 페이지
    │   └── detail.html          # 회원 개인 학습률 페이지
    ├── scores/                # 회원 점수 관련 템플릿
    │   ├── list.html            # 회원 점수 목록 페이지
    │   └── sorted.html          # 회원 점수 순위 페이지
    └── deadline/              # 사용자 학습 마감일 관련 템플릿
        ├── edit.html            # 회원 학습 마감일 수정 페이지
        └── expired.html         # 학습 마감일 만료된 회원 목록 페이지
```

### 2.1 레이아웃 설계 구조

**Base Layout (기본 골격)**
```
┌────────────────────────────────────────────┐
│                  Header                    │
│        (로고, 페이지 제목, 네비게이션)       │
├────────────────────────────────────────────┤
│               Main Content                 │
│      (타이틀, 검색 필터, 버튼 등 포함)       │
│                                            │
│          ┌────────────────────┐            │
│          │    Page Content    │            │
│          │  (표, 달력, 차트 등)             │
│          └────────────────────┘            │
│                                            │
└────────────────────────────────────────────┘
```

## 2. 프래그먼트
**templates/fragments/...**
```
- header-main.html: 메인 페이지 프래그먼트
- header-detail.html: 상세 페이지 프래그먼트
```

### 2.1 메인 페이지
```html
<div th:fragment="adminHeader">
  <nav class="navbar navbar-expand-lg" style="background-color: #fdb82c; height: 30px;"></nav>

  <div class="container-fluid d-flex align-items-center justify-content-between" style="background-color: #f8f7f3;">
    <a class="navbar-brand d-flex align-items-center" href="/" style="margin-left: 16px;">
      <img src="/images/logo.png" alt="제대로 보안니" width="110" height="50" class="me-2">
      <strong style="font-size: 18px;">관리자 페이지</strong>
    </a>

    <div class="me-4 d-flex gap-3">
      <a href="/admin/questions/all"
         style="font-size: 15px; font-weight: 500; color: #333; text-decoration: none;">
        전체 보안 문제 조회
      </a>
      <a href="/admin/users/scores"
         style="font-size: 15px; font-weight: 500; color: #333; text-decoration: none;">
        회원 점수 조회
      </a>
      <a href="/admin/users/progress"
         style="font-size: 15px; font-weight: 500; color: #333; text-decoration: none;">
        회원 학습률 조회
      </a>
    </div>
  </div>
</div>
```

### 2.2 상세 페이지
```html
<div th:fragment="adminHeader(subTitle)">
  <nav class="navbar navbar-expand-lg" style="background-color: #fdb82c; height: 30px;"></nav>

  <div class="container-fluid d-flex align-items-center justify-content-between" style="background-color: #f8f7f3;">
    <a class="navbar-brand d-flex align-items-center" href="/" style="margin-left: 16px;">
      <img src="/images/logo.png" alt="제대로 보안니" width="110" height="50" class="me-2">
      <strong style="font-size: 18px;">
        관리자 페이지 <span th:if="${subTitle}">- [[${subTitle}]]</span>
      </strong>
    </a>

    <div class="me-4 d-flex gap-3">
      <a href="/admin/users"
         style="font-size: 15px; font-weight: 500; color: #333; text-decoration: none;">
        관리자 메인 페이지로 돌아가기
      </a>
    </div>
  </div>
</div>
```
## 3. Spring Security 연동

### 3.1 인증 (Authentication)

- JWT 기반 로그인 인증 구현
- 로그인 성공 시 JWT 토큰 발급
- 클라이언트는 이후 모든 요청 시 `Authorization: Bearer <token>` 헤더 포함

---

### 3.2 인가 (Authorization)

#### 3.2.1 URL 접근 제어 (`SecurityFilterChain`)

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    // 생략
    .anyRequest().authenticated()
);
```

#### 3.2.2 어노테이션 접근 제어 (`Controller`)
```java
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // 모든 메서드는 ADMIN만 접근 가능
}
```

## 4. 폼 검증

### 4.1 구조 개요
- Thymeleaf 템플릿 내에서 `<form>` 직접 정의
- DTO 객체(`QuestionDto.Request`)를 `@ModelAttribute`로 바인딩
- 서버에서는 서비스 계층을 통해 문제 저장
- 예외 발생 시 `RedirectAttributes`를 통해 에러 메시지 전달

### 4.1.1 Controller 처리 흐름

```java
// GET: 문제 생성 폼 요청
@GetMapping("/create")
public String showCreateForm(Model model) {
    model.addAttribute("questionForm", new QuestionDto.Request());
    return "admin/questions/create";
}

// POST: 문제 생성 요청 처리
@PostMapping("/create")
public String createQuestion(@ModelAttribute("questionForm") QuestionDto.Request request,
                             RedirectAttributes redirectAttributes) {
    try {
        questionService.createQuestion(request);
        redirectAttributes.addFlashAttribute("message", "문제가 성공적으로 등록되었습니다.");
        return "redirect:/admin/questions/all";
    } catch (BusinessException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/admin/questions/create";
    }
}
```

### 4.1.2 HTML 템플릿 연동
```html
<form th:action="@{/admin/questions/create}" method="post" th:object="${questionForm}">
    <div class="mb-3">
      <label for="question" class="form-label fw-bold">문제 내용</label>
      <textarea id="question" th:field="*{question}" class="form-control" rows="5" placeholder="보안 문제 내용을 입력하세요."></textarea>
    </div>

    <div class="d-flex justify-content-between">
      <a th:href="@{/admin/questions/all}" class="btn btn-secondary">← 목록으로</a>
      <button type="submit" class="btn btn-primary">문제 등록</button>
    </div>
</form>
```