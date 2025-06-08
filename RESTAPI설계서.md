# REST API 설계서

##  문서 정보
- **프로젝트명**: [제대로 보안니]
- **작성자**: [육하원칙/최지은]
- **작성일**: [2025-06-08]
- **버전**: [v2.0]
- **검토자**: [박효영]
- **API 버전**: v1
- **Base URL**: https://server.boaniserver.kro.kr

---

## 1. API 설계 개요

### 1.1 설계 목적
> RESTful 원칙에 따라 클라이언트-서버 간 통신 규격을 정의하여 일관되고 확장 가능한 API를 제공

### 1.2 설계 원칙
- **RESTful 아키텍처**: HTTP 메서드와 상태 코드의 올바른 사용
- **일관성**: 모든 API 엔드포인트에서 동일한 규칙 적용
- **버전 관리**: URL 경로를 통한 버전 구분
- **보안**: JWT 기반 인증 또는 외부 기능을 이용한 인증증
- **성능**: 페이지네이션, 캐싱
- **문서화**: 명확한 요청/응답 스펙 제공

### 1.3 기술 스택
- **프레임워크**: Spring Boot 3.4.6
- **인증**: JWT (JSON Web Token)
- **직렬화**: JSON

---

## 2. API 공통 규칙

### 2.1 URL 설계 규칙
| 규칙 | 설명                | 좋은 예                          | 나쁜 예                    |
|------|-------------------|-------------------------------|-------------------------|
| **명사 사용** | 동사가 아닌 명사로 리소스 표현 | `/auth/verify`                | `/auth/getVerifies`     |
| **복수형 사용** | 컬렉션은 복수형으로 표현     | `/admin/users`                | `/admin/user`           |
| **계층 구조** | 리소스 간 관계를 URL로 표현 | `/admin/users/TRAINEE/type`   | `/admin/getUserTypes`   |
| **동작 표현** | HTTP 메서드로 동작 구분   | `POST /api/signin`            | `/api/createSignin`     |

### 2.2 HTTP 메서드 사용 규칙
| 메서드 | 용도 | 멱등성 | 안전성 | 예시                          |
|--------|------|--------|--------|-----------------------------|
| **GET** | 리소스 조회 | ✅ | ✅ | `GET /api/record`           |
| **POST** | 리소스 생성 | ❌ | ❌ | `POST /api/questions`       |
| **PUT** | 리소스 전체 수정 | ✅ | ❌ | `PUT /board/4`              |
| **PATCH** | 리소스 부분 수정 | ❌ | ❌ | `PATCH /admin/users/1/role` |
| **DELETE** | 리소스 삭제 | ✅ | ❌ | `DELETE /board/2`           |

### 2.3 HTTP 상태 코드 가이드
| 코드      | 상태                    | 설명          | 사용 예시 |
|---------|-----------------------|-------------|----------|
| **200** | OK                    | 성공 (데이터 포함) | GET 요청 성공 |
| **201** | Created               | 리소스 생성 성공   | POST 요청 성공 |
| **204** | No Content            | 리소스 삭제 성공   | POST 요청 성공 |
| **400** | Bad Request           | 잘못된 요청      | 검증 실패 |
| **401** | Unauthorized          | 인증 필요       | 토큰 없음/만료 |
| **403** | Forbidden             | 권한 없음       | 접근 거부 |
| **404** | Not Found             | 리소스 없음      | 존재하지 않는 ID |
| **500** | Internal Server Error | 서버 오류       | 예기치 못한 오류 |

### 2.4 공통 요청 헤더
```
Content-Type: application/json
Accept: application/json
X-Request-ID: {UUID}  // 요청 추적용
Accept-Language: ko-KR
User-Agent: LibraryApp/1.0.0
```

### 2.5 공통 응답 형식
#### 성공 응답 예시(단일 객체)
```json
{
  "success": true,
  "code": 200,
  "data": {
    // 실제 데이터
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:48:36.730049",
  "requestId": "3ef5874e-aa3e-45c2-8df9-6a2c484957b9"
}
```

#### 성공 응답 예시(목록/페이지네이션)
```json
{
  "success": true,
  "data": {
    "content": [
      // 데이터 배열
    ],
    "pageable": {
      "pageNumber": 20,
      "pageSize": 10,
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 200,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalElements": 203,
    "totalPages": 21,
    "first": false,
    "size": 10,
    "number": 20,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "numberOfElements": 3,
    "empty": false
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:47:09.564345",
  "requestId": "f03697c6-600f-41fa-9e45-e231135132ff"
}
```

#### 에러 응답 예시
```json
{
  "success": false,
  "code": 404,
  "error": {
    "code": "B003",
    "message": "해당 페이지에 게시글이 존재하지 않습니다.",
    "details": null,
    "path": "/board",
    "method": "GET"
  },
  "timestamp": "2025-06-04 16:50:15 수 오후",
  "requestId": "b6f45658-2a0d-4806-bd7a-bff182dafd51"
}
```

---

## 3. 인증 및 권한 관리

### 3.1 DB 기반 인증

#### 3.1.1 사원번호 인증 API
```yaml
GET /auth/verify
Content-Type: application/json

Request Body:
{
  "username":"홍길동",
  "employeeNum":"SK12355"
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "employeeNum": "SK12355"
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-01T17:54:40.869629",
  "requestId": "fa770080-8844-4a1d-8367-e7315d2cdad4"
}

Response (400 Bad Request):
{
  "success": false,
  "code": 400,
  "error": {
    "code": "A006",
    "message": "이미 가입된 사원 번호입니다.",
    "details": null,
    "path": "/auth/verify",
    "method": "GET"
  },
  "timestamp": "2025-06-01 17:58:51 일 오후",
  "requestId": "1e9a9e79-4243-416e-9787-e8300095021c"
}

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "EA001",
    "message": "존재하지 않는 사번이거나 가입 완료된 사번입니다.",
    "details": null,
    "path": "/auth/verify",
    "method": "GET"
  },
  "timestamp": "2025-06-01 17:55:39 일 오후",
  "requestId": "a3b3f558-7588-45b8-9a64-f15babdf6e2f"
}
```

#### 3.1.2 이메일 인증 API
```yaml
GET /auth/email/check
Content-Type: application/json

Request Body:
{
  "email":"test534@naver.com"
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": false,
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-05T13:07:37.268072",
  "requestId": "87c34240-2db9-4d97-8a6d-0973efbe3b6d"
}

# 중복일 경우 data: false
Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": true,
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-05T13:07:16.701726",
  "requestId": "9369f5d2-1e78-4872-a0f4-6a16382ab51e"
}
```

#### 3.1.3 회원가입 API
```yaml
POST /auth/signup
Content-Type: application/json

Request Body:
{
  "employeeNum":"SK12351",
  "email":"test1239@naver.com",
  "password":"test1234!",
  "passwordCheck":"test1234!"
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "employeeNum": "SK12351",
    "email": "test1239@naver.com"
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-01T18:09:37.723384",
  "requestId": "90251105-619b-4053-be05-f8e33febf66c"
}

Response (400 Bad Request):
{
  "success": false,
  "code": 400,
  "error": {
    "code": "A005",
    "message": "중복된 이메일입니다.",
    "details": null,
    "path": "/auth/signup",
    "method": "POST"
  },
  "timestamp": "2025-06-01 18:10:09 일 오후",
  "requestId": "6b8bf51d-ad45-4e6c-942e-5a5535e8ce7b"
}

Response (400 Bad Request):
{
  "success": false,
  "code": 400,
  "error": {
    "code": "A002",
    "message": "일치하지 않는 비밀번호입니다.",
    "details": null,
    "path": "/auth/signup",
    "method": "POST"
  },
  "timestamp": "2025-06-01 18:10:44 일 오후",
  "requestId": "10cdb5ec-fd20-4c0d-982d-8f7505a4b169"
}

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "EA001",
    "message": "존재하지 않는 사번이거나 가입 완료된 사번입니다.",
    "details": null,
    "path": "/auth/signup",
    "method": "POST"
  },
  "timestamp": "2025-06-01 18:11:42 일 오후",
  "requestId": "d1b0cfb5-0019-42d9-83bf-ed06d2042d85"
}
```

### 3.2 JWT 토큰 기반 인증

#### 3.2.1 사용자 정보 조회 API
```yaml
GET /auth/userinfo
Content-Type: application/json

Request Body:
{
  "refreshToken":"eyJhbGciOiJIUzI1NiJ9..."
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "username": "홍길동",
    "employeeType": "TRAINEE",
    "score": 0
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-05T13:10:10.245944",
  "requestId": "949e6c42-af62-4ab9-89ff-61c4801a2e2d"
}

Response (500 Internal Server Error):
{
  "success": false,
  "code": 500,
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "서버 오류가 발생했습니다. 다시 시도해주세요.",
    "details": null,
    "path": "/auth/userinfo",
    "method": "GET"
  },
  "timestamp": "2025-06-07 15:16:41 토 오후",
  "requestId": "59260252-1703-4de3-83d4-ce87c902aa29"
}
```

#### 3.2.2 로그인 API
```yaml
POST /auth/signin
Content-Type: application/json

Request Body:
{
  "email":"test1234@naver.com",
  "password":"test1234!"
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-05T16:13:10.277412",
  "requestId": "93faf853-1a7a-4632-81e6-7d9b99c25407"
}

Response (401 Unauthorized):
{
  "success": false,
  "code": 401,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
    "details": null,
    "path": "/auth/signin",
    "method": "POST"
  },
  "timestamp": "2025-06-05 16:09:54 목 오후",
  "requestId": "801905a8-6c32-4dff-88ad-cf51568817e9"
}
```

#### 3.2.3 토큰 갱신 API
```yaml
POST /auth/refresh
Content-Type: application/json

Request Body:
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI..."
}

Response (200 OK):
  {
    "success": true,
    "code": 200,
    "data": {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...",
      "tokenType": "Bearer",
      "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI..."
    },
    "message": "요청이 성공적으로 처리되었습니다.",
    "timestamp": "2025-06-05T13:07:11.154826",
    "requestId": "d3169d07-f5c8-4e25-9e70-ac1ecf22fe59"
  }
```

#### 3.2.4 로그아웃 API
```yaml
POST /auth/refresh
Authorization: Bearer {JWT_TOKEN}

Response (204 No Content)
```

### 3.3 권한 레벨 정의
| 역할              | 권한          | 설명                 |
|-----------------|-------------|--------------------|
| **TRAINEE**     | Rookies 수강생 | 문제 풀이, 게시글 작성      |
| **EMPLOYEE**    | 조직 내 임직원    | 문제 풀이, 게시글 작성 |
| **ADMIN**       | 관리자         | 모든 권한 + 회원 관리, 질문 생성 |

---

## 4. 상세 API 명세

### 4.1 메인 페이지 API

#### 4.1.1 회원 정보 조회
```yaml
GET /api/home
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "user": {
      "userId": 5,
      "name": "홍길동",
      "groupNum": 2,
      "employeeType": "TRAINEE",
      "personalScore": 20
    },
    "groupScores": [
      {
        "groupNum": 1,
        "groupScore": 51
      },
      {
        "groupNum": 2,
        "groupScore": 10
      }
    ]
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-05T09:36:06.9867889",
  "requestId": "2e03c56b-482d-4336-a171-0787e3b2a5de"
}
```

### 4.2 문제 관련 API

#### 4.2.1 문제 및 풀이 가능 여부
```yaml
GET /api/questions/me
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "id": 1,
    "question": "피싱 공격을 예방하기 위한 방법을 설명하세요.",
    "canSolve": true
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T11:24:53.2878191",
  "requestId": "ba717d7e-f0a5-488e-835b-5994f19f064a"
}

Response (403 Forbidden):
{
  "success": false,
  "code": 403,
  "error": {
    "code": "U012",
    "message": "학습 마감일이 지나 문제를 풀 수 없습니다. 관리자에게 문의하세요.",
    "details": null,
    "path": "/api/questions/me",
    "method": "GET"
  },
  "timestamp": "2025-06-04 11:08:16 수 오전",
  "requestId": "80074b4c-f877-4a98-a8e4-502afa59a011"
}
```

### 4.3 AI 관련 API

#### 4.3.1 문제 풀이
```yaml
POST /api/chat/gpt
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "score": 20,
    "status": "success",
    "model_answer": "피싱 공격을 예방하기 위한 방법은 사용자 교육 및 인식 확대, 이메일 필터링 및 사용자 인증 절차 강화 등이 있습니다. 예를 들어, 회사 내 직원들에게 정기적으로 피싱 공격에 대한 교육을 실시하고, 이메일 시스템에 스팸 필터를 설치하여 의심스러운 링크나 첨부 파일을 차단하는 등의 조치를 취할 수 있습니다.",
    "feedback": "답변이 부족하여 전문성&정확성에서 0점을 부여하였습니다. 향후 관련된 주제에 대해 조금 더 학습하고 나서 다시 답변해보시길 권장드립니다."
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T23:30:13.1316398",
  "requestId": "fa2bea25-8cd4-4ac8-a1fe-7635ca1be32e"
}

Response (200 OK):
  {
    "success": true,
    "code": 200,
    "data": {
      "score": 85,
      "status": "success",
      "model_answer": "비밀번호를 암호화하지 않고 문자열 그대로 저장하는 방식은 개인정보 유출뿐 아니라 다른 서비스로의 연쇄 피해(계정 탈취 등)로 이어질 수 있습니다. 예를 들어, 해커가 DB에 접근하여 비밀번호를 확인하는 경우, 해당 비밀번호로 다른 온라인 서비스에 로그인하여 더 큰 피해가 발생할 수 있습니다.",
      "feedback": "정확한 내용 전달 및 예시 포함이 좋습니다. 다만, 추가적인 기술적 조치나 정책적 조치를 더 포함하면 더 완벽한 답변이 될 것입니다."
    },
    "message": "요청이 성공적으로 처리되었습니다.",
    "timestamp": "2025-06-05T22:59:43.1271503",
    "requestId": "51fd0443-38e9-4d4d-a414-e998f66c243b"
  }

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "A001",
    "message": "해당하는 답변을 찾을 수 없습니다. User ID : 5 & Question ID : 5",
    "details": null,
    "path": "/api/chat/gpt/5",
    "method": "POST"
  },
  "timestamp": "2025-06-04 17:10:42 수 오후",
  "requestId": "36b127e6-949d-46bb-9fd1-7e2a79daea37"
}
```

#### 4.3.2 그룹별 답변 요약
```yaml
POST /api/chat/groq
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "message": "ok"
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-05T23:45:03.0308531",
  "requestId": "fbfa1b39-19bf-4a89-8fcf-48302e809ec9"
}
```

### 4.4 사용자 답변 관련 API

#### 4.4.1 답변 등록
```yaml
POST /api/record
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Request Body:
  {
    "questionId": 1,
    "userId": 5,
    "userAnswer": "복잡하고 고유한 비밀번호를 사이트별로 사용하고, 비밀번호 관리 도구와 2단계 인증으로 안전하게 관리해요."
  }

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "id": 7,
    "userAnswer": "복잡하고 고유한 비밀번호를 사이트별로 사용하고, 비밀번호 관리 도구와 2단계 인증으로 안전하게 관리해요.",
    "gptAnswer": "비밀번호를 안전하게 관리하기 위해서는 안전한 암호화 기술을 사용하고, 주기적으로 변경하며 외부에 노출되지 않도록 주의해아 합니다. 또한...",
    "question": {
      "id": 1,
      "question": "비밀번호를 안전하게 관리하는 방법은 무엇인가요?",
      "canSolve": false
    },
    "userId": 5,
    "bookMarked": false
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:40:26.3214248",
  "requestId": "9d25b877-e2e8-40a9-b9f8-e7077da5dbb3"
}
```

#### 4.4.2 질문 북마크
```yaml
POST /api/record/bookmarked
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Request Body:
  {
    "questionId": 1,
    "userId": 5
  }

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "id": 7,
    "userAnswer": "복잡하고 고유한 비밀번호를 사이트별로 사용하고, 비밀번호 관리 도구와 2단계 인증으로 안전하게 관리해요.",
    "gptAnswer": "비밀번호를 안전하게 관리하기 위해서는 안전한 암호화 기술을 사용하고, 주기적으로 변경하며 외부에 노출되지 않도록 주의해아 합니다. 또한...",
    "question": {
      "id": 1,
      "question": "비밀번호를 안전하게 관리하는 방법은 무엇인가요?",
      "canSolve": false
    },
    "userId": 5,
    "bookMarked": false
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:44:54.1037108",
  "requestId": "fce05c5b-a937-4104-a459-5c42257d6fe8"
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "id": 7,
    "userAnswer": "복잡하고 고유한 비밀번호를 사이트별로 사용하고, 비밀번호 관리 도구와 2단계 인증으로 안전하게 관리해요.",
    "gptAnswer": "비밀번호를 안전하게 관리하기 위해서는 안전한 암호화 기술을 사용하고, 주기적으로 변경하며 외부에 노출되지 않도록 주의해아 합니다. 또한...",
    "question": {
      "id": 1,
      "question": "비밀번호를 안전하게 관리하는 방법은 무엇인가요?",
      "canSolve": false
    },
    "userId": 5,
    "bookMarked": true
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:51:06.4875291",
  "requestId": "9694fc61-42fd-487a-a709-30713d82a1ff"
}
```

#### 4.4.3 사용자 답변 목록 조회
```yaml
GET /api/record
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": [
    {
      "id": 1,
      "userAnswer": "EDR나 행위 기반 탐지 솔루션을 도입하여 파일 암호화 시도를 실시간 탐지가 가능하도록 합니다. 또한 백업 정책 강화해서 주기적 자동 백업 및 백업 데이터의 오프라인 저장하거나 네트워크 분리를 통해 중요 시스템과 일반 사용자의 네트워크를 분리해 전파 차단합니다",
      "gptAnswer": "피싱 공격 예방을 위해 EDR나 행위 기반 탐지 솔루션을 도입하여 실시간 탐지 가능하게 하며, 백업 정책 강화를 통해 주기적 자동 백업과 오프라인 저장 또는 네트워크 분리를 통해 중요 시스템과 일반 사용자의 네트워크를 분리하여 전파 차단. 예를 들어, 금융 기관에서는 중요한 서버에 대한 액세스를 제한하고, 일반 사용자의 네트워크와 분리하여 피싱 공격의 위험을 줄일 수 있습니다.",
      "question": {
        "id": 2,
        "question": "피싱 공격을 예방하기 위한 방법을 설명하세요.",
        "canSolve": false
      },
      "userId": 1,
      "bookMarked": false
    },
    {
      "id": 2,
      "userAnswer": "비밀번호는 마법의 암호처럼 비밀스럽게, 절대 노출되지 않도록 꼭꼭 숨겨야 해요",
      "gptAnswer": "비밀번호를 안전하게 관리하기 위해서는 안전한 암호화 기술을 사용하고, 주기적으로 변경하며 외부에 노출되지 않도록 주의해아 합니다. 또한...",
      "question": {
        "id": 1,
        "question": "비밀번호를 안전하게 관리하는 방법은 무엇인가요?",
        "canSolve": false
      },
      "userId": 4,
      "bookMarked": false
    },
    {
      "id": 3,
      "userAnswer": "아웃도어 공격이 발생하여 DB가 유출되었을 때, 비밀번호가 암호화되어 있지 않기 때문에 더 큰 위험이 발생할 수 있습니다.",
      "gptAnswer": "비밀번호를 암호화하지 않고 저장하는 것은 데이터 유출 시 고객의 비밀번호가 노출될 위험이 크며, 이는 개인정보 보호법 위반이 될 수 있습니다. 해시 함수를 사용하여 비밀번호를 암호화하고, 솔트 기법을 적용함으로써 보안을 강화해야 합니다. 예를 들어, bcrypt와 같은 강력한 해시 알고리즘을 사용하여 비밀번호를 안전하게 저장할 수 있습니다.",
      "question": {
        "id": 5,
        "question": "모 기업은 고객 정보를 DB에 저장하고 있습니다. 다만, DB에 값을 저장할 때 비밀번호를 암호화하지 않고, 문자열 그대로 저장하는 방식으로 고객 정보를 저장하고 있습니다. 이런 방식은 어떤 문제를 야기할 수 있을까요?",
        "canSolve": false
      },
      "userId": 4,
      "bookMarked": false
    }
  ],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:45:15.5352579",
  "requestId": "49bffd14-cd8b-4d74-86cf-fff5fec69dd2"
}
```

#### 4.4.4 모든 요약 답변 조회
```yaml
GET /api/group
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": [
    {
      "id": 9,
      "title": "보안 강화 방안",
      "summary": "직원들은 비밀번호를 안전하게 관리하여 보안을 강화할 것을 제안합니다.",
      "questionId": 1,
      "groupNum": 1
    },
    {
      "id": 10,
      "title": "보안 강화 방안",
      "summary": "직원들은 EDR나 행위 기반 탐지 솔루션을 도입하여 실시간 탐지를 가능하도록 하며, 추가적으로 백업 정책 강화, 피싱 대책, 2단계 인증 설정 등을 제안합니다.",
      "questionId": 2,
      "groupNum": 1
    },
    {
      "id": 11,
      "title": "DB 유출 위험",
      "summary": "암호화되지 않은 비밀번호로 인해 더 큰 위험이 발생할 수 있습니다.",
      "questionId": 5,
      "groupNum": 2
    }
  ],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T23:34:07.1952837",
  "requestId": "66a031b2-cb25-4ca2-886b-31076976398a"
}
```

#### 4.4.5 본인이 풀이한 문제 조회
```yaml
GET /api/record/solved
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": [
    {
      "id": 7,
      "userAnswer": "복잡하고 고유한 비밀번호를 사이트별로 사용하고, 비밀번호 관리 도구와 2단계 인증으로 안전하게 관리해요.",
      "gptAnswer": "비밀번호를 안전하게 관리하기 위해서는 안전한 암호화 기술을 사용하고, 주기적으로 변경하며 외부에 노출되지 않도록 주의해아 합니다. 또한...",
      "question": {
        "id": 1,
        "question": "비밀번호를 안전하게 관리하는 방법은 무엇인가요?",
        "canSolve": false
      },
      "userId": 5,
      "bookMarked": false
    },
    {
      "id": 8,
      "userAnswer": "모르겠다.",
      "gptAnswer": "피싱 공격을 예방하려면, 먼저 출처가 불분명한 이메일이나 메시지, 링크는 절대 클릭하지 않아야 하며, 의심스러운 경우 보낸 사람의 주소나 도메인을 꼼꼼히 확인해야 한다...",
      "question": {
        "id": 2,
        "question": "피싱 공격을 예방하기 위한 방법을 설명하세요.",
        "canSolve": false
      },
      "userId": 5,
      "bookMarked": false
    }
  ],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:49:27.4041625",
  "requestId": "a2534718-3e40-447d-be2c-4a55c6dd9113"
}

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "A003",
    "message": "해당 사용자가 푼 문제 기록을 찾을 수 없습니다. User ID : 6",
    "details": null,
    "path": "/api/record/solved",
    "method": "GET"
  },
  "timestamp": "2025-06-04 10:58:09 수 오전",
  "requestId": "5026c13f-835f-4b2b-95b3-e0f82a39390e"
}
```

#### 4.4.6 본인이 북마크한 문제 조회
```yaml
GET /api/record/solved/bookmarked
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": [
    {
      "id": 7,
      "userAnswer": "복잡하고 고유한 비밀번호를 사이트별로 사용하고, 비밀번호 관리 도구와 2단계 인증으로 안전하게 관리해요.",
      "gptAnswer": "비밀번호를 안전하게 관리하기 위해서는 안전한 암호화 기술을 사용하고, 주기적으로 변경하며 외부에 노출되지 않도록 주의해아 합니다. 또한...",
      "question": {
        "id": 1,
        "question": "비밀번호를 안전하게 관리하는 방법은 무엇인가요?",
        "canSolve": false
      },
      "userId": 5,
      "bookMarked": true
    }
  ],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:51:45.4005965",
  "requestId": "20dbaa54-ea7f-4654-9ad9-582e00558dba"
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": [],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T10:52:55.5005536",
  "requestId": "da9cf137-5692-4745-8fd4-85703eb7e602"
}
```

### 4.5 그룹 관리 API

#### 4.5.1 그룹 전체 조회
```yaml
POST /api/chat/groq/all
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": [
    {
      "id": 1,
      "question_id": 2,
      "title": "보안 강화 방안",
      "question": "피싱 공격을 예방하기 위한 방법을 설명하세요.",
      "summary": "실시간 탐지 솔루션 도입, 백업 정책 강화, 피싱 대책, 2단계 인증 설정, 의심스러운 링크나 첨부파일에 대한 주의"
    },
    {
      "id": 2,
      "question_id": 3,
      "title": "강화된 보안 의식",
      "question": "2단계 인증(Two-factor authentication)의 장점은 무엇인가요?",
      "summary": "의심스러운 링크나 첨부파일 클릭금지, 출처 확인, 2단계 인증 설정, 최신 보안 패치 적용, 주기적 보안 교육, 2단계 인증은 비밀번호 외에 추가 인증 수단을 요구해 계정 탈취 위험을 획기적으로 줄이고, 피싱·크리덴셜 스터핑 등 공격에 강력한 방어막을 제공합니다."
    },
    {
      "id": 3,
      "question_id": 4,
      "title": "랜섬웨어 위협 완화를 위한 대응 방안",
      "question": "랜섬웨어가 정교해지고 있습니다. 보안 담당자로서 감염 탐지 및 대응 방안을 기술하시오. (기술적/정책적/교육 측면)",
      "summary": "랜섬웨어 감염 탐지, 의심스러운 파일 암호화·프로세스 이상 행위 모니터링, 중요 데이터 정기 백업 및 분리 보관, 네트워크 격리 및 IOC 기반 전파 차단"
    },
    {
      "id": 4,
      "question_id": 5,
      "title": "비밀번호 암호화 미비의 위험",
      "question": "모 기업은 고객 정보를 DB에 저장하고 있습니다. 다만, DB에 값을 저장할 때 비밀번호를 암호화하지 않고, 문자열 그대로 저장하는 방식으로 고객 정보를 저장하고 있습니다. 이런 방식은 어떤 문제를 야기할 수 있을까요?",
      "summary": "암호화되지 않은 비밀번호로 인해 개인정보 유출, 계정 탈취 등 연쇄 위험이 발생할 수 있습니다."
    },
    {
      "id": 5,
      "question_id": 9,
      "title": "APT 공격 탐지 및 예방 전략: UEBA, EADR/XDR, 위협 인텔리전스 및 Zero Trust 모델",
      "question": "APT 공격(Advanced Persistent Threat)이 일반적인 공격과 다른 점은 무엇이며, 조직 내에서 이를 탐지하고 대응하기 위한 효과적인 전략은 무엇인가?",
      "summary": "APT 공격은 장기간에 걸쳐 은밀하게 침투해 정보를 탈취하는 고도화된 공격입니다. 탐지를 위해 이상 행위 기반 탐지(UEBA), EDR/XDR 도입, 위협 인텔리전스 연계, Zero Trust 모델 적용 등이 효과적입니다. 주요 키워드는 UEBA, EDR/XDR, 위협 인텔리전스, Zero Trust 모델입니다."
    }
  ],
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-06T21:54:47.0342424",
  "requestId": "b8560d02-81ef-42e4-85a0-0327b24ad69c"
}
```

#### 4.5.2 그룹 정보 조회
```yaml
GET /api/group/{groupNum}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Path Parameters:
  - groupNum: Long (required) - 그룹 번호
    
Response (200 OK):
  {
    "success": true,
    "code": 200,
    "data": [
      {
        "id": 1,
        "title": "랜섬웨어 위협 완화를 위한 종합 대응 방안",
        "summary": "랜섬웨어 감염 탐지 및 대응 방안을 위해 EDR 및 행위 기반 탐지 시스템을 도입하고, 의심스러운 파일 암호화·프로세스 이상 행위를 모니터링하여 중요 데이터 정기 백업 및 분리 보관을 시행하고, 감염 시 네트워크 격리 및 IOC 기반 전파 차단을 수행할 수 있도록 한다. 또한, 보안 담당자는 랜섬웨어에 대응하기 위해 정기적인 보안 교육과 정책을 시행하여 직원의 보안 意識을 높이는 데 초점을 두어야 한다.",
        "questionId": 4,
        "questionName": "랜섬웨어가 정교해지고 있습니다. 보안 담당자로서 감염 탐지 및 대응 방안을 기술하시오. (기술적/정책적/교육 측면)",
        "groupNum": 4
      },
      {
        "id": 2,
        "title": "보안 위험 요인: 고객 정보 유출 및 연쇄 피해",
        "summary": "고객 정보를 암호화하지 않고 문자열로 저장하는 것은 개인정보 유출 및 연쇄 피해 가능성을 높입니다. 이 방식은 계정 탈취 등 다른 서비스로의 연쇄 피해를 야기할 수 있습니다. 중요 보안 포인트는 고객 정보를 안전하게 저장하는 것입니다.",
        "questionId": 5,
        "questionName": "SK쉴더스는 고객 정보를 DB에 저장하고 있습니다. 다만, DB에 값을 저장할 때 비밀번호를 암호화하지 않고, 문자열 그대로 저장하는 방식으로 고객 정보를 저장하고 있습니다. 이런 방식은 어떤 문제를 야기할 수 있을까요?",
        "groupNum": 4
      },
      {
        "id": 3,
        "title": "APT 공격 탐지 및 대응 전략",
        "summary": "APT 공격은 장기간에 걸쳐 은밀하게 침투해 정보를 탈취하는 고도화된 공격입니다. 탐지를 위해 이상 행위 기반 탐지(UEBA), EDR/XDR 도입, 위협 인텔리전스 연계, Zero Trust 모델 적용 등이 효과적입니다. 조직 내에서는 지속적인 모니터링, 위협 분석, 취약점 관리 등이 필요합니다. 특히, UEBA, EDR/XDR, 위협 인텔리전스, Zero Trust 모델이 주요 키워드입니다. APT 공격에 대응하기 위해서는 지속적인 모니터링과 위협 분석이 필요하며, 조직 내에서는 취약점 관리 등이 필요합니다.",
        "questionId": 9,
        "questionName": "APT 공격(Advanced Persistent Threat)이 일반적인 공격과 다른 점은 무엇이며, 조직 내에서 이를 탐지하고 대응하기 위한 효과적인 전략은 무엇인가?",
        "groupNum": 4
      }
    ],
    "message": "요청이 성공적으로 처리되었습니다.",
    "timestamp": "2025-06-08T14:28:41.1331352",
    "requestId": "73a0da3a-ca60-4aa0-bcaa-5425a4edeef9"
  }
```

#### 4.5.3 그룹 디테일 정보 조회
```yaml
GET /api/group/{groupNum}/{questionId}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE

Path Parameters:
  - groupNum: Long (required) - 그룹 번호
  - questionId: Long (required) - 문제 번호    
    
Response (200 OK):
  {
    "success": true,
    "code": 200,
    "data": {
        "id": 1,
        "title": "랜섬웨어 위협 완화를 위한 종합 대응 방안",
        "summary": "랜섬웨어 감염 탐지 및 대응 방안을 위해 EDR 및 행위 기반 탐지 시스템을 도입하고, 의심스러운 파일 암호화·프로세스 이상 행위를 모니터링하여 중요 데이터 정기 백업 및 분리 보관을 시행하고, 감염 시 네트워크 격리 및 IOC 기반 전파 차단을 수행할 수 있도록 한다. 또한, 보안 담당자는 랜섬웨어에 대응하기 위해 정기적인 보안 교육과 정책을 시행하여 직원의 보안 意識을 높이는 데 초점을 두어야 한다.",
        "questionId": 4,
        "questionName": "랜섬웨어가 정교해지고 있습니다. 보안 담당자로서 감염 탐지 및 대응 방안을 기술하시오. (기술적/정책적/교육 측면)",
        "groupNum": 4
      },
    "message": "요청이 성공적으로 처리되었습니다.",
    "timestamp": "2025-06-08T14:28:41.1331352",
    "requestId": "73a0da3a-ca60-4aa0-bcaa-5425a4edeef9"
  }
```

### 4.6 게시판 API

#### 4.6.1 게시글 목록 조회
```yaml
GET /board
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Query Parameters:
  - page: integer (default: 0) - 페이지 번호
  - size: integer (default: 20) - 페이지 크기

Request Body:
{
  "page": 20,
  "size": 10
}

Response (200 OK):
  {
    "success": true,
    "code": 200,
    "data": {
      "content": [
        {
          "id": 1,
          "title": "첫 번째 게시글",
          "createdAt": "2025-06-07 20:42:16",
          "role": "TRAINEE",
          "author": "최직원"
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
          "empty": false,
          "sorted": true,
          "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
      },
      "last": true,
      "totalPages": 1,
      "totalElements": 1,
      "size": 10,
      "number": 0,
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "first": true,
      "numberOfElements": 1,
      "empty": false
    },
    "message": "요청이 성공적으로 처리되었습니다.",
    "timestamp": "2025-06-07T20:42:55.025425",
    "requestId": "dd6eeadd-40de-490e-84b2-445a41fc9428"
  }

Request Body:
{
  "page": 21,
  "size": 10
}

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "B003",
    "message": "해당 페이지에 게시글이 존재하지 않습니다.",
    "details": null,
    "path": "/board",
    "method": "GET"
  },
  "timestamp": "2025-06-04 16:50:15 수 오후",
  "requestId": "b6f45658-2a0d-4806-bd7a-bff182dafd51"
}
```

#### 4.6.2 단일 게시글 조회
```yaml
GET /board/{id}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Path Parameters:
  - id: integer (required) - 게시글 ID
    
Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "id": 50,
    "author": "홍길동",
    "title": "45번째 제목",
    "contents": "45번째 본문",
    "createdAt": "2025-06-03 18:42:38.454277",
    "mine": false
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:47:17.382956",
  "requestId": "8b789e15-5f58-46ac-bd4c-fa78ed5e1b0c"
}

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "B001",
    "message": "ID에 해당하는 게시글을 찾을 수 없습니다.",
    "details": null,
    "path": "/board/999",
    "method": "GET"
  },
  "timestamp": "2025-06-04 16:50:41 수 오후",
  "requestId": "2b2bfbdb-8005-40c5-9702-13e2de5b3d59"
}
```

#### 4.6.3 게시판 페이지 개수 조회
```yaml
GET /board/total-pages
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN
    
Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": 21,
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:48:23.228584",
  "requestId": "4edf4d4a-60f1-4a0a-82d9-1ca3bb7ca930"
}
```

#### 4.6.4 게시글 생성
```yaml
POST /board
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Request Body:
{
  "title":"보안의 중요성",
  "contents":"최근 모 기업의 기사 보셨나요?..."
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": {
    "boardId": 208
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:48:36.730049",
  "requestId": "3ef5874e-aa3e-45c2-8df9-6a2c484957b9"
}

Request Body:
{
  "title":"테스트",
}
  
Response (404 Not Found):
{
  "success": false,
  "code": 400,
  "error": {
    "code": "INVALID_REQUEST_FORMAT",
    "message": "잘못된 요청 형식입니다. JSON 문법을 확인해주세요.",
    "details": null,
    "path": "/board",
    "method": "POST"
  },
  "timestamp": "2025-06-04 16:51:10 수 오후",
  "requestId": "5edb4737-e8d7-4d10-a5af-04eaecb46a36"
}
```

#### 4.6.5 게시글 삭제
```yaml
DELETE /board/{id}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Path Parameters:
  - id: integer (required) - 게시글 ID
    
Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": 210,
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:58:47.37467",
  "requestId": "a6e010a5-f86e-4b3f-a11a-21092e54f048"
}

Response (403 Forbidden):
{
  "success": false,
  "code": 403,
  "error": {
    "code": "B002",
    "message": "작성자와 일치하지 않는 ID입니다.",
    "details": null,
    "path": "/board/50",
    "method": "DELETE"
  },
  "timestamp": "2025-06-04 16:48:51 수 오후",
  "requestId": "1d30be06-5341-4889-b6d9-8cfff8e09104"
}
```

#### 4.6.6 게시글 수정
```yaml
PUT /board/{id}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: TRAINEE, EMPLOYEE, ADMIN

Path Parameters:
  - id: integer (required) - 게시글 ID

Request Body:
{
  "title":"보안의 중요성!! 꼭 보세요.",
  "contents":"최근 모 기업의 기사 보셨나요?..."
}

Response (200 OK):
{
  "success": true,
  "code": 200,
  "data": null,
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2025-06-04T16:49:16.175771",
  "requestId": "d5c96da4-17cc-4259-b7dd-2f54c5df2be6"
}

Response (404 Not Found):
{
  "success": false,
  "code": 404,
  "error": {
    "code": "B001",
    "message": "ID에 해당하는 게시글을 찾을 수 없습니다.",
    "details": null,
    "path": "/board/1",
    "method": "PUT"
  },
  "timestamp": "2025-06-04 16:59:54 수 오후",
  "requestId": "c7c08b38-8891-4350-900c-c37125fe1492"
}
```


---

## 5. 에러 코드 및 처리

### 5.1 비즈니스 로직 에러 코드

#### 5.1.1 EmployeeNumber 관련 에러 코드
| 코드 | 설명                            | 해결 방법           |
|------|-------------------------------|-----------------|
| **EMPLOYEE_AUTH_ERROR** | 사원 번호를 찾을 수 없거나 이미 가입된 사원 번호임 | 사원번호 확인         |
| **EMPLOYEE_AUTH_NOT_EQUAL_USERNAME_EMPLOYEE_NUMBER** | 사용자의 이름과 사원 번호가 일치하지 않음       | 회원 이름 및 사원번호 확인 |
| **EMPLOYEE_AUTH_NOT_FOUNT_TYPE** | 해당 사원 타입을 찾을 수 없음             | 사원 타입 확인        |

#### 5.1.2 Auth 관련 에러 코드
| 코드 | 설명              | 해결 방법          |
|------|-----------------|----------------|
| **AUTH_PASSWORD_NOT_EQUAL_ERROR** | 비밀번호가 일치하지 않음   | 회원 Password 확인 |
| **AUTH_EMAIL_DUPLICATE_ERROR** | 중복된 이메일         | 회원 Email 확인    |
| **AUTH_REGISTERED_EMPLOYEE_NUMBER_ERROR** | 이미 회원가입된 사원 번호임 | 사원번호 확인        |
| **AUTH_NOT_FOUND_BY_ID** | 해당 ID를 찾을 수 없음  | 회원 ID 확인       |
| **AUTH_INVALID_TOKEN** | 유효하지 않은 토큰임     | 회원 토큰 확인       |

#### 5.1.3 Users 관련 에러 코드
| 코드 | 설명                       | 해결 방법              |
|------|--------------------------|--------------------|
| **USER_NOT_FOUND** | 해당 id를 가진 회원을 찾을 수 없음    | 회원 ID 확인           |
| **USER_NOT_FOUND_BY_EMAIL** | 해당 email을 가진 회원을 찾을 수 없음 | 회원 Email 확인        |
| **USER_NOT_FOUND_BY_USERNAME** | 해당 이름을 가진 회원을 찾을 수 없음    | 회원 Username 확인     |
| **USER_DEADLINE_BEFORE_TODAY** | 학습 마감일을 과거 날짜로 선택했을 시    | 오늘 날짜 확인 후 수정      |
| **USER_DEADLINE_SAME_AS_BEFORE** | 학습 마감일이 수정 전과 동일함        | 기존 학습 마감일 확인 후 수정  |
| **USER_DEADLINE_EXPIRED** | 학습 마감일이 지났을 경우           | 관리자가 회원 정보 확인 후 갱신 |

#### 5.1.4 Group 관련 에러 코드
| 코드 | 설명             | 해결 방법    |
|------|----------------|----------|
| **GROUP_NOT_FOUND** | 해당 그룹을 찾을 수 없음 | 그룹 ID 확인 |

#### 5.1.5 Question 관련 에러 코드
| 코드 | 설명             | 해결 방법               |
|------|----------------|---------------------|
| **QUESTION_NOT_FOUND** | 해당 문제를 찾을 수 없음 | 문제 INDEX 확인         |
| **NO_MORE_QUESTION** | 더이상 풀 문제가 없음   | 관리자가 문제를 추가할 때까지 대기 |
| **NO_QUESTION** | 문제 내용이 비어있음    | 관리자가 확인 후 문제 내용 입력  |

#### 5.1.6 UserAiRecord 관련 에러 코드
| 코드 | 설명            | 해결 방법                |
|------|---------------|----------------------|
| **ANSWER_NOT_FOUND** | 해당 답변을 찾을 수 없음 | 회원 및 문제 ID 확인        |
| **RECORD_NOT_FOUND** | 해당 기록을 찾을 수 없음 | 회원 및 문제 ID 확인        |
| **SOLVED_RECORD_NOT_FOUND** | 사용자가 푼 문제 기록을 찾을 수 없음 | 회원 ID 확인             |
| **BOOKMARKED_RECORD_NOT_FOUND** | 북마크한 문제 기록을 찾을 수 없음 | 회원 ID 확인             |
| **API_SERVER_ERROR** | IO 에러     | 새로고침 후 다시 풀이         |
| **API_RESPONSE_TYPE_ERROR** | JSON 파싱 실패    | 새로고침 후 다시 풀이 및 다시 답변 |
| **API_REVERSE_JSON_ERROR** | JSON 추출 실패    | 새로고침 후 다시 풀이 및 다시 답변    |

#### 5.1.7 Board 관련 에러 코드
| 코드 | 설명                | 해결 방법          |
|------|-------------------|----------------|
| **BOARD_NOT_FOUND_BY_ID** | 해당 ID의 글을 찾을 수 없음 | 게시글 ID 확인      |
| **BOARD_FORBIDDEN_USER** | 본인이 작성한 글이 아님     | 게시글 및 회원 ID 확인 |
| **BOARD_PAGE_OUT_ERROR** | 해당 페이지에 게시글이 없음   | 페이지 이동         |
---

## 7. 마무리

### 7.1 주요 포인트 요약
1. **RESTful 설계**: HTTP 메서드와 상태 코드의 올바른 사용
2. **일관성 유지**: 모든 API에서 동일한 규칙과 형식 적용
3. **보안 강화**: 인증, 인가, 입력 검증을 통한 보안 확보
4. **문서화**: 명확하고 완전한 API 문서 제공

### 7.2 추천 도구 및 라이브러리
- **문서화**: Postman
- **보안**: Spring Security, JWT

### 7.3 향후 고도화 방안
- **GraphQL 지원**: 클라이언트별 맞춤 데이터 제공
- **WebSocket**: 실시간 알림 및 채팅 기능
- **이벤트 기반 아키텍처**: 마이크로서비스 간 느슨한 결합
- **API 게이트웨이**: 중앙화된 API 관리
- **테스트**: REST Assured, WireMock, TestContainers
- **모니터링**: 로깅, 메트릭을 통한 운영 상황 파악 Micrometer, Prometheus, Grafana