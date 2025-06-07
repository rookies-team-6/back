# REST API 설계서

##  문서 정보
- **프로젝트명**: [제대로 보안니]
- **작성자**: [팀명/최지은]
- **작성일**: [2025-06-07]
- **버전**: [v1.0]
- **검토자**: [박효영, 이무현]
- **API 버전**: v1
- **Base URL**: https://localhost:8080/api

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
- **API 문서**: OpenAPI 3.0 (Swagger)

---

## 2. API 공통 규칙

### 2.1 URL 설계 규칙
| 규칙 | 설명 | 좋은 예 | 나쁜 예 |
|------|------|---------|---------|
| **명사 사용** | 동사가 아닌 명사로 리소스 표현 | `/api/books` | `/api/getBooks` |
| **복수형 사용** | 컬렉션은 복수형으로 표현 | `/api/members` | `/api/member` |
| **계층 구조** | 리소스 간 관계를 URL로 표현 | `/api/members/123/loans` | `/api/getMemberLoans` |
| **소문자 사용** | URL은 소문자와 하이픈 사용 | `/api/book-categories` | `/api/BookCategories` |
| **동작 표현** | HTTP 메서드로 동작 구분 | `POST /api/loans` | `/api/createLoan` |

### 2.2 HTTP 메서드 사용 규칙
| 메서드 | 용도 | 멱등성 | 안전성 | 예시 |
|--------|------|--------|--------|------|
| **GET** | 리소스 조회 | ✅ | ✅ | `GET /api/books` |
| **POST** | 리소스 생성 | ❌ | ❌ | `POST /api/books` |
| **PUT** | 리소스 전체 수정 | ✅ | ❌ | `PUT /api/books/123` |
| **PATCH** | 리소스 부분 수정 | ❌ | ❌ | `PATCH /api/books/123` |
| **DELETE** | 리소스 삭제 | ✅ | ❌ | `DELETE /api/books/123` |

### 2.3 HTTP 상태 코드 가이드
| 코드 | 상태 | 설명 | 사용 예시 |
|------|------|------|----------|
| **200** | OK | 성공 (데이터 포함) | GET 요청 성공 |
| **201** | Created | 리소스 생성 성공 | POST 요청 성공 |
| **204** | No Content | 성공 (응답 데이터 없음) | DELETE 요청 성공 |
| **400** | Bad Request | 잘못된 요청 | 검증 실패 |
| **401** | Unauthorized | 인증 필요 | 토큰 없음/만료 |
| **403** | Forbidden | 권한 없음 | 접근 거부 |
| **404** | Not Found | 리소스 없음 | 존재하지 않는 ID |
| **409** | Conflict | 충돌 | 중복 생성 시도 |
| **422** | Unprocessable Entity | 의미상 오류 | 비즈니스 규칙 위반 |
| **500** | Internal Server Error | 서버 오류 | 예기치 못한 오류 |

### 2.4 공통 요청 헤더
```
Content-Type: application/json
Accept: application/json
Authorization: Bearer {JWT_TOKEN}
X-Request-ID: {UUID}  // 요청 추적용
Accept-Language: ko-KR
User-Agent: LibraryApp/1.0.0
```

### 2.5 공통 응답 형식
#### 성공 응답 (단일 객체)
```json
{
  "success": true,
  "data": {
    // 실제 데이터
  },
  "message": "요청이 성공적으로 처리되었습니다",
  "timestamp": "2025-05-26T10:30:00Z",
  "requestId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 성공 응답 (목록/페이지네이션)
```json
{
  "success": true,
  "data": {
    "content": [
      // 데이터 배열
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 150,
      "totalPages": 8,
      "first": true,
      "last": false,
      "numberOfElements": 20
    },
    "sort": {
      "sorted": true,
      "criteria": [
        {
          "property": "createdAt",
          "direction": "DESC"
        }
      ]
    }
  },
  "message": "목록 조회가 완료되었습니다",
  "timestamp": "2025-05-26T10:30:00Z",
  "requestId": "550e8400-e29b-41d4-a716-446655440001"
}
```

#### 에러 응답
```json
{
  "statusCode": "400",
  "message": "이메일 형식이 올바르지 않습니다",
  "timestamp": "2025-05-26T10:30:00Z"
}
```

---

## 3. 인증 및 권한 관리

### 3.1 JWT 토큰 기반 인증

#### 3.1.1 로그인 API
```yaml
POST /api/auth/login
Content-Type: application/json

Request Body:
{
  "email": "user@example.com",
  "password": "password123!"
}

Response (200 OK):
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "refreshExpiresIn": 604800,
    "user": {
      "id": 1,
      "name": "김철수",
      "email": "user@example.com",
      "memberNumber": "M000000001",
      "role": "MEMBER"
    }
  },
  "message": "로그인이 성공하였습니다"
}

Response (401 Unauthorized):
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이메일 또는 비밀번호가 올바르지 않습니다"
  }
}
```

#### 3.1.2 토큰 갱신 API
```yaml
POST /api/auth/refresh
Content-Type: application/json

Request Body:
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Response (200 OK):
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  }
}
```

#### 3.1.3 로그아웃 API
```yaml
POST /api/auth/logout
Authorization: Bearer {JWT_TOKEN}

Response (204 No Content)
```

### 3.2 권한 레벨 정의
| 역할 | 권한 | 설명 |
|------|------|------|
| **MEMBER** | 일반 회원 | 도서 조회, 대출 신청, 내 정보 관리 |
| **LIBRARIAN** | 사서 | 회원 권한 + 대출 승인, 도서 관리 |
| **ADMIN** | 관리자 | 사서 권한 + 회원 관리, 시스템 설정 |
| **SUPER_ADMIN** | 슈퍼 관리자 | 모든 권한 + 관리자 계정 관리 |

---

## 4. 상세 API 명세

### 4.1 회원 관리 API

#### 4.1.1 회원 목록 조회
```yaml
GET /api/members
Authorization: Bearer {JWT_TOKEN}
Required Role: LIBRARIAN, ADMIN, SUPER_ADMIN

Query Parameters:
- page: integer (default: 0) - 페이지 번호 (0부터 시작)
- size: integer (default: 20, max: 100) - 페이지 크기
- sort: string (default: "createdAt,desc") - 정렬 기준
- status: string - 회원 상태 필터 (ACTIVE, SUSPENDED, WITHDRAWN)
- memberType: string - 회원 유형 필터 (GENERAL, VIP, STUDENT)
- search: string - 검색어 (이름, 이메일, 회원번호)
- joinDateFrom: date - 가입일 시작일 (YYYY-MM-DD)
- joinDateTo: date - 가입일 종료일 (YYYY-MM-DD)

Response (200 OK):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "memberNumber": "M000000001",
        "name": "김철수",
        "email": "kim@example.com",
        "phoneNumber": "010-1234-5678",
        "status": "ACTIVE",
        "memberType": "GENERAL",
        "maxLoanCount": 5,
        "currentLoanCount": 2,
        "joinDate": "2025-05-01",
        "lastLoginAt": "2025-05-26T09:30:00Z",
        "createdAt": "2025-05-01T10:00:00Z",
        "updatedAt": "2025-05-26T09:30:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 150,
      "totalPages": 8,
      "first": true,
      "last": false
    }
  }
}

Response (403 Forbidden):
{
  "success": false,
  "error": {
    "code": "ACCESS_DENIED",
    "message": "해당 리소스에 접근할 권한이 없습니다"
  }
}
```

#### 4.1.2 회원 상세 조회
```yaml
GET /api/members/{memberId}
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER (본인만), LIBRARIAN, ADMIN, SUPER_ADMIN

Path Parameters:
- memberId: integer (required) - 회원 ID

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 1,
    "memberNumber": "M000000001",
    "name": "김철수",
    "email": "kim@example.com",
    "phoneNumber": "010-1234-5678",
    "birthDate": "1990-05-15",
    "address": "서울시 강남구 테헤란로 123",
    "postalCode": "06159",
    "status": "ACTIVE",
    "memberType": "GENERAL",
    "maxLoanCount": 5,
    "joinDate": "2025-05-01",
    "lastLoginAt": "2025-05-26T09:30:00Z",
    "statistics": {
      "totalLoans": 45,
      "currentLoans": 2,
      "overdueCount": 0,
      "totalOverdueFee": 0,
      "favoriteCategories": ["소설/문학", "과학/기술"]
    },
    "createdAt": "2025-05-01T10:00:00Z",
    "updatedAt": "2025-05-26T09:30:00Z"
  }
}

Response (404 Not Found):
{
  "success": false,
  "error": {
    "code": "MEMBER_NOT_FOUND",
    "message": "회원을 찾을 수 없습니다",
    "details": [
      {
        "field": "memberId",
        "message": "존재하지 않는 회원 ID입니다",
        "rejectedValue": 999
      }
    ]
  }
}
```

#### 4.1.3 회원 등록
```yaml
POST /api/members
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: LIBRARIAN, ADMIN, SUPER_ADMIN

Request Body:
{
  "name": "김철수",
  "email": "kim@example.com",
  "password": "password123!",
  "phoneNumber": "010-1234-5678",
  "birthDate": "1990-05-15",
  "address": "서울시 강남구 테헤란로 123",
  "postalCode": "06159",
  "memberType": "GENERAL"
}

Validation Rules:
- name: 필수, 2-50자, 한글/영문만
- email: 필수, 이메일 형식, 중복 불가
- password: 필수, 8-20자, 영문+숫자+특수문자 조합
- phoneNumber: 선택, 010-XXXX-XXXX 형식
- birthDate: 선택, YYYY-MM-DD 형식, 과거 날짜만
- memberType: 선택, GENERAL|VIP|STUDENT

Response (201 Created):
{
  "success": true,
  "data": {
    "id": 156,
    "memberNumber": "M000000156",
    "name": "김철수",
    "email": "kim@example.com",
    "phoneNumber": "010-1234-5678",
    "status": "ACTIVE",
    "memberType": "GENERAL",
    "maxLoanCount": 5,
    "joinDate": "2025-05-26",
    "createdAt": "2025-05-26T10:30:00Z"
  },
  "message": "회원 등록이 완료되었습니다"
}

Response (400 Bad Request):
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다",
    "details": [
      {
        "field": "email",
        "message": "이미 사용 중인 이메일입니다",
        "rejectedValue": "kim@example.com"
      },
      {
        "field": "password",
        "message": "비밀번호는 8-20자의 영문, 숫자, 특수문자 조합이어야 합니다",
        "rejectedValue": "123"
      }
    ]
  }
}
```

#### 4.1.4 회원 정보 수정
```yaml
PUT /api/members/{memberId}
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER (본인만), LIBRARIAN, ADMIN, SUPER_ADMIN

Path Parameters:
- memberId: integer (required) - 회원 ID

Request Body:
{
  "name": "김철수",
  "phoneNumber": "010-1234-5679",
  "address": "서울시 서초구 반포대로 456",
  "postalCode": "06543"
}

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 1,
    "memberNumber": "M000000001",
    "name": "김철수",
    "email": "kim@example.com",
    "phoneNumber": "010-1234-5679",
    "address": "서울시 서초구 반포대로 456",
    "postalCode": "06543",
    "updatedAt": "2025-05-26T10:35:00Z"
  },
  "message": "회원 정보가 수정되었습니다"
}
```

#### 4.1.5 회원 상태 변경
```yaml
PATCH /api/members/{memberId}/status
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: ADMIN, SUPER_ADMIN

Path Parameters:
- memberId: integer (required) - 회원 ID

Request Body:
{
  "status": "SUSPENDED",
  "reason": "연체료 미납으로 인한 일시 정지",
  "suspensionEndDate": "2025-06-26"
}

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 1,
    "status": "SUSPENDED",
    "reason": "연체료 미납으로 인한 일시 정지",
    "suspensionEndDate": "2025-06-26",
    "updatedAt": "2025-05-26T10:40:00Z",
    "updatedBy": "admin@library.com"
  },
  "message": "회원 상태가 변경되었습니다"
}
```

### 4.2 도서 관리 API

#### 4.2.1 도서 목록 조회
```yaml
GET /api/books
Public API (인증 불필요)

Query Parameters:
- page: integer (default: 0) - 페이지 번호
- size: integer (default: 20, max: 100) - 페이지 크기
- sort: string (default: "title,asc") - 정렬 기준
- categoryId: integer - 카테고리 ID 필터
- search: string - 검색어 (제목, 저자, ISBN)
- author: string - 저자 필터
- publisher: string - 출판사 필터
- language: string - 언어 필터 (KO, EN, etc.)
- available: boolean - 대출 가능 여부 필터
- publicationDateFrom: date - 출간일 시작일
- publicationDateTo: date - 출간일 종료일
- ageRating: string - 연령 등급 (ALL, 12, 15, 18)

Response (200 OK):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "isbn": "9788932917245",
        "title": "82년생 김지영",
        "subtitle": null,
        "author": "조남주",
        "publisher": "민음사",
        "publicationDate": "2016-10-14",
        "edition": 1,
        "totalCopies": 3,
        "availableCopies": 1,
        "reservedCopies": 1,
        "price": 13800.00,
        "imageUrl": "https://image.example.com/books/1.jpg",
        "thumbnailUrl": "https://image.example.com/books/thumb/1.jpg",
        "pageCount": 215,
        "language": "KO",
        "ageRating": "ALL",
        "bookCondition": "NEW",
        "category": {
          "id": 8,
          "name": "한국소설",
          "parentCategory": {
            "id": 1,
            "name": "소설/문학"
          }
        },
        "isAvailable": true,
        "averageRating": 4.5,
        "reviewCount": 128,
        "createdAt": "2025-05-01T10:00:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 1250,
      "totalPages": 63
    },
    "filters": {
      "appliedFilters": {
        "categoryId": 1,
        "available": true
      },
      "availableFilters": {
        "categories": [
          {"id": 1, "name": "소설/문학", "count": 450},
          {"id": 2, "name": "과학/기술", "count": 320}
        ],
        "languages": [
          {"code": "KO", "name": "한국어", "count": 980},
          {"code": "EN", "name": "영어", "count": 270}
        ]
      }
    }
  }
}
```

#### 4.2.2 도서 상세 조회
```yaml
GET /api/books/{bookId}
Public API (인증 불필요)

Path Parameters:
- bookId: integer (required) - 도서 ID

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 1,
    "isbn": "9788932917245",
    "title": "82년생 김지영",
    "subtitle": null,
    "author": "조남주",
    "coAuthor": null,
    "translator": null,
    "publisher": "민음사",
    "publicationDate": "2016-10-14",
    "edition": 1,
    "totalCopies": 3,
    "availableCopies": 1,
    "reservedCopies": 1,
    "price": 13800.00,
    "description": "한국 여성의 현실을 그린 베스트셀러 소설...",
    "tableOfContents": "1장. 김지영, 1982년...",
    "imageUrl": "https://image.example.com/books/1.jpg",
    "thumbnailUrl": "https://image.example.com/books/thumb/1.jpg",
    "pageCount": 215,
    "weight": 0.32,
    "dimensions": "148x210x15",
    "language": "KO",
    "originalLanguage": "KO",
    "ageRating": "ALL",
    "bookCondition": "NEW",
    "locationCode": "A-001-15",
    "category": {
      "id": 8,
      "name": "한국소설",
      "parentCategory": {
        "id": 1,
        "name": "소설/문학"
      }
    },
    "statistics": {
      "totalLoans": 45,
      "currentLoans": 2,
      "reservationCount": 1,
      "averageRating": 4.5,
      "reviewCount": 128,
      "popularityRank": 15
    },
    "availability": {
      "isAvailable": true,
      "nextAvailableDate": "2025-06-10",
      "estimatedWaitTime": "약 1주일"
    },
    "relatedBooks": [
      {
        "id": 25,
        "title": "완전한 행복",
        "author": "정유정",
        "imageUrl": "https://image.example.com/books/25.jpg"
      }
    ],
    "createdAt": "2025-05-01T10:00:00Z",
    "updatedAt": "2025-05-25T14:20:00Z"
  }
}
```

#### 4.2.3 도서 등록
```yaml
POST /api/books
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: LIBRARIAN, ADMIN, SUPER_ADMIN

Request Body:
{
  "isbn": "9788932917245",
  "title": "82년생 김지영",
  "subtitle": null,
  "author": "조남주",
  "coAuthor": null,
  "translator": null,
  "publisher": "민음사",
  "publicationDate": "2016-10-14",
  "edition": 1,
  "totalCopies": 3,
  "price": 13800.00,
  "description": "한국 여성의 현실을 그린 베스트셀러 소설",
  "pageCount": 215,
  "weight": 0.32,
  "dimensions": "148x210x15",
  "language": "KO",
  "ageRating": "ALL",
  "categoryId": 8,
  "locationCode": "A-001-15",
  "purchaseDate": "2025-05-26",
  "purchasePrice": 12420.00,
  "vendor": "교보문고"
}

Validation Rules:
- isbn: 필수, 13자리 숫자, 중복 불가, ISBN-13 체크섬 검증
- title: 필수, 1-200자
- author: 필수, 1-100자
- categoryId: 필수, 존재하는 카테고리 ID
- totalCopies: 필수, 1 이상
- price: 선택, 0 이상
- language: 필수, ISO 639-1 코드
- ageRating: 필수, ALL|12|15|18

Response (201 Created):
{
  "success": true,
  "data": {
    "id": 1251,
    "isbn": "9788932917245",
    "title": "82년생 김지영",
    "author": "조남주",
    "totalCopies": 3,
    "availableCopies": 3,
    "category": {
      "id": 8,
      "name": "한국소설"
    },
    "createdAt": "2025-05-26T10:45:00Z",
    "createdBy": "librarian@library.com"
  },
  "message": "도서가 성공적으로 등록되었습니다"
}

Response (409 Conflict):
{
  "success": false,
  "error": {
    "code": "DUPLICATE_ISBN",
    "message": "이미 등록된 ISBN입니다",
    "details": [
      {
        "field": "isbn",
        "message": "동일한 ISBN의 도서가 이미 존재합니다",
        "rejectedValue": "9788932917245",
        "existingBookId": 1
      }
    ]
  }
}
```

### 4.3 대출 관리 API

#### 4.3.1 대출 신청
```yaml
POST /api/loans
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER, LIBRARIAN, ADMIN, SUPER_ADMIN

Request Body:
{
  "bookId": 1,
  "notes": "급하게 필요한 도서입니다"
}

Business Rules Validation:
- 회원 상태가 ACTIVE인지 확인
- 현재 대출 권수가 최대 한도 미만인지 확인
- 연체 중인 도서가 없는지 확인
- 도서가 대출 가능한지 확인
- 동일 도서를 이미 대출 중이 아닌지 확인

Response (201 Created):
{
  "success": true,
  "data": {
    "id": 5001,
    "loanNumber": "L202505260001",
    "member": {
      "id": 1,
      "name": "김철수",
      "memberNumber": "M000000001",
      "email": "kim@example.com"
    },
    "book": {
      "id": 1,
      "title": "82년생 김지영",
      "author": "조남주",
      "isbn": "9788932917245"
    },
    "status": "REQUESTED",
    "requestedAt": "2025-05-26T10:50:00Z",
    "notes": "급하게 필요한 도서입니다",
    "estimatedApprovalTime": "영업일 기준 1-2일"
  },
  "message": "대출 신청이 완료되었습니다"
}

Response (400 Bad Request):
{
  "success": false,
  "error": {
    "code": "LOAN_NOT_AVAILABLE",
    "message": "대출 신청이 불가능합니다",
    "details": [
      {
        "code": "EXCEED_LOAN_LIMIT",
        "message": "최대 대출 권수를 초과했습니다",
        "currentLoanCount": 5,
        "maxLoanCount": 5
      },
      {
        "code": "HAS_OVERDUE_LOANS",
        "message": "연체 중인 도서가 있습니다",
        "overdueLoansCount": 1,
        "totalOverdueFee": 500
      }
    ]
  }
}

Response (409 Conflict):
{
  "success": false,
  "error": {
    "code": "BOOK_NOT_AVAILABLE",
    "message": "해당 도서는 현재 대출할 수 없습니다",
    "details": [
      {
        "code": "OUT_OF_STOCK",
        "message": "모든 권수가 대출 중입니다",
        "availableCopies": 0,
        "totalCopies": 3,
        "nextAvailableDate": "2025-06-05"
      }
    ]
  }
}
```

#### 4.3.2 대출 승인 (관리자)
```yaml
PATCH /api/loans/{loanId}/approve
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: LIBRARIAN, ADMIN, SUPER_ADMIN

Path Parameters:
- loanId: integer (required) - 대출 ID

Request Body:
{
  "notes": "승인 완료",
  "dueDate": "2025-06-09"  // 선택사항, 기본 14일
}

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 5001,
    "loanNumber": "L202505260001",
    "status": "APPROVED",
    "loanDate": "2025-05-26",
    "dueDate": "2025-06-09",
    "approvedBy": {
      "id": 1,
      "name": "사서김",
      "email": "librarian@library.com"
    },
    "approvedAt": "2025-05-26T11:00:00Z",
    "updatedAt": "2025-05-26T11:00:00Z"
  },
  "message": "대출이 승인되었습니다"
}

Response (400 Bad Request):
{
  "success": false,
  "error": {
    "code": "INVALID_LOAN_STATUS",
    "message": "승인할 수 없는 상태입니다",
    "currentStatus": "CANCELLED"
  }
}
```

#### 4.3.3 대출 목록 조회
```yaml
GET /api/loans
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER (본인만), LIBRARIAN, ADMIN, SUPER_ADMIN

Query Parameters:
- page: integer (default: 0) - 페이지 번호
- size: integer (default: 20) - 페이지 크기
- sort: string (default: "createdAt,desc") - 정렬 기준
- memberId: integer - 회원 ID 필터 (관리자만)
- bookId: integer - 도서 ID 필터
- status: string - 상태 필터 (REQUESTED,APPROVED,BORROWED,RETURNED,OVERDUE,CANCELLED)
- overdue: boolean - 연체 여부 필터
- loanDateFrom: date - 대출일 시작일
- loanDateTo: date - 대출일 종료일
- dueDateFrom: date - 반납예정일 시작일
- dueDateTo: date - 반납예정일 종료일

Response (200 OK):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 5001,
        "loanNumber": "L202505260001",
        "member": {
          "id": 1,
          "name": "김철수",
          "memberNumber": "M000000001"
        },
        "book": {
          "id": 1,
          "title": "82년생 김지영",
          "author": "조남주",
          "isbn": "9788932917245",
          "imageUrl": "https://image.example.com/books/1.jpg"
        },
        "status": "BORROWED",
        "loanDate": "2025-05-26",
        "dueDate": "2025-06-09",
        "returnDate": null,
        "extensionCount": 0,
        "overdueDays": 0,
        "overdueFee": 0.00,
        "isOverdue": false,
        "canExtend": true,
        "maxExtensionCount": 1,
        "notes": "급하게 필요한 도서입니다",
        "approvedBy": {
          "id": 1,
          "name": "사서김"
        },
        "createdAt": "2025-05-26T10:50:00Z",
        "approvedAt": "2025-05-26T11:00:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 25,
      "totalPages": 2
    },
    "summary": {
      "totalLoans": 25,
      "activeLoans": 3,
      "overdueLoans": 0,
      "totalOverdueFee": 0.00
    }
  }
}
```

#### 4.3.4 도서 반납
```yaml
PATCH /api/loans/{loanId}/return
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: LIBRARIAN, ADMIN, SUPER_ADMIN

Path Parameters:
- loanId: integer (required) - 대출 ID

Request Body:
{
  "returnCondition": "GOOD",  // GOOD, DAMAGED, LOST
  "notes": "정상 반납 완료",
  "damageFee": 0.00,  // 파손료 (DAMAGED인 경우)
  "lostFee": 0.00     // 분실료 (LOST인 경우)
}

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 5001,
    "loanNumber": "L202505260001",
    "status": "RETURNED",
    "returnDate": "2025-06-05",
    "returnCondition": "GOOD",
    "isOverdue": false,
    "overdueDays": 0,
    "overdueFee": 0.00,
    "damageFee": 0.00,
    "lostFee": 0.00,
    "totalFee": 0.00,
    "returnedBy": {
      "id": 1,
      "name": "사서김",
      "email": "librarian@library.com"
    },
    "returnedAt": "2025-06-05T14:30:00Z"
  },
  "message": "반납 처리가 완료되었습니다"
}

Response (400 Bad Request):
{
  "success": false,
  "error": {
    "code": "INVALID_RETURN_STATUS",
    "message": "반납할 수 없는 상태입니다",
    "currentStatus": "REQUESTED",
    "allowedStatuses": ["BORROWED", "OVERDUE"]
  }
}
```

#### 4.3.5 대출 연장
```yaml
PATCH /api/loans/{loanId}/extend
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER (본인만), LIBRARIAN, ADMIN, SUPER_ADMIN

Path Parameters:
- loanId: integer (required) - 대출 ID

Request Body:
{
  "extensionDays": 7,  // 연장 일수 (기본 7일)
  "reason": "추가 학습이 필요합니다"
}

Business Rules:
- 최대 연장 횟수: 1회
- 연장 가능 기간: 반납일 3일 전부터
- 연체 상태에서는 연장 불가
- 해당 도서에 예약이 있으면 연장 불가

Response (200 OK):
{
  "success": true,
  "data": {
    "id": 5001,
    "loanNumber": "L202505260001",
    "originalDueDate": "2025-06-09",
    "extendedDueDate": "2025-06-16",
    "extensionCount": 1,
    "maxExtensionCount": 1,
    "canExtendAgain": false,
    "extensionDate": "2025-06-06",
    "reason": "추가 학습이 필요합니다",
    "updatedAt": "2025-06-06T10:30:00Z"
  },
  "message": "대출 기간이 연장되었습니다"
}

Response (400 Bad Request):
{
  "success": false,
  "error": {
    "code": "EXTENSION_NOT_ALLOWED",
    "message": "대출 연장이 불가능합니다",
    "details": [
      {
        "code": "MAX_EXTENSION_REACHED",
        "message": "최대 연장 횟수를 초과했습니다",
        "currentExtensionCount": 1,
        "maxExtensionCount": 1
      },
      {
        "code": "BOOK_RESERVED",
        "message": "해당 도서에 예약이 있어 연장할 수 없습니다",
        "reservationCount": 2
      }
    ]
  }
}
```

### 4.4 예약 관리 API

#### 4.4.1 도서 예약
```yaml
POST /api/reservations
Content-Type: application/json
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER, LIBRARIAN, ADMIN, SUPER_ADMIN

Request Body:
{
  "bookId": 1,
  "notes": "다음 주까지 필요합니다"
}

Business Rules:
- 대출 가능한 도서는 예약 불가
- 동일 도서 중복 예약 불가
- 최대 예약 권수: 3권

Response (201 Created):
{
  "success": true,
  "data": {
    "id": 1001,
    "reservationNumber": "R202505260001",
    "member": {
      "id": 1,
      "name": "김철수",
      "memberNumber": "M000000001"
    },
    "book": {
      "id": 1,
      "title": "82년생 김지영",
      "author": "조남주",
      "isbn": "9788932917245"
    },
    "status": "ACTIVE",
    "priorityOrder": 3,
    "reservationDate": "2025-05-26",
    "expiryDate": "2025-05-29",
    "estimatedAvailableDate": "2025-06-10",
    "estimatedWaitTime": "약 2주",
    "notes": "다음 주까지 필요합니다",
    "createdAt": "2025-05-26T11:30:00Z"
  },
  "message": "예약이 완료되었습니다"
}

Response (409 Conflict):
{
  "success": false,
  "error": {
    "code": "RESERVATION_NOT_ALLOWED",
    "message": "예약할 수 없는 상태입니다",
    "details": [
      {
        "code": "BOOK_AVAILABLE",
        "message": "현재 대출 가능한 도서입니다",
        "availableCopies": 1
      }
    ]
  }
}
```

#### 4.4.2 예약 목록 조회
```yaml
GET /api/reservations
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER (본인만), LIBRARIAN, ADMIN, SUPER_ADMIN

Query Parameters:
- page: integer (default: 0)
- size: integer (default: 20)
- memberId: integer - 회원 ID 필터 (관리자만)
- bookId: integer - 도서 ID 필터
- status: string - 상태 필터 (ACTIVE, COMPLETED, CANCELLED, EXPIRED)

Response (200 OK):
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1001,
        "reservationNumber": "R202505260001",
        "book": {
          "id": 1,
          "title": "82년생 김지영",
          "author": "조남주",
          "imageUrl": "https://image.example.com/books/1.jpg"
        },
        "status": "ACTIVE",
        "priorityOrder": 2,
        "reservationDate": "2025-05-26",
        "expiryDate": "2025-05-29",
        "estimatedAvailableDate": "2025-06-08",
        "estimatedWaitTime": "약 1주일",
        "queueStatus": {
          "currentPosition": 2,
          "totalQueue": 3,
          "progressPercentage": 33
        },
        "canCancel": true,
        "createdAt": "2025-05-26T11:30:00Z"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 5
    }
  }
}
```

#### 4.4.3 예약 취소
```yaml
DELETE /api/reservations/{reservationId}
Authorization: Bearer {JWT_TOKEN}
Required Role: MEMBER (본인만), LIBRARIAN, ADMIN, SUPER_ADMIN

Path Parameters:
- reservationId: integer (required) - 예약 ID

Response (204 No Content)

Response (400 Bad Request):
{
  "success": false,
  "error": {
    "code": "RESERVATION_CANNOT_CANCEL",
    "message": "취소할 수 없는 예약 상태입니다",
    "currentStatus": "COMPLETED"
  }
}
```

---

## 5. 에러 코드 및 처리

### 5.1 표준 에러 코드 정의
| 코드 | HTTP 상태 | 설명 | 해결 방법 |
|------|-----------|------|-----------|
| **VALIDATION_ERROR** | 400 | 입력값 검증 실패 | 요청 데이터 확인 후 재시도 |
| **INVALID_CREDENTIALS** | 401 | 인증 정보 오류 | 로그인 정보 확인 |
| **TOKEN_EXPIRED** | 401 | 토큰 만료 | 토큰 갱신 또는 재로그인 |
| **ACCESS_DENIED** | 403 | 권한 없음 | 권한 확인 또는 관리자 문의 |
| **RESOURCE_NOT_FOUND** | 404 | 리소스 없음 | 요청 URL 및 ID 확인 |
| **DUPLICATE_RESOURCE** | 409 | 중복 생성 시도 | 기존 리소스 확인 |
| **BUSINESS_RULE_VIOLATION** | 422 | 비즈니스 규칙 위반 | 규칙 확인 후 조건 충족 |
| **RATE_LIMIT_EXCEEDED** | 429 | 요청 한도 초과 | 잠시 후 재시도 |
| **INTERNAL_SERVER_ERROR** | 500 | 서버 내부 오류 | 관리자 문의 |

### 5.2 비즈니스 로직 에러 코드
| 코드 | 설명 | 해결 방법 |
|------|------|-----------|
| **MEMBER_NOT_FOUND** | 회원을 찾을 수 없음 | 회원 ID 확인 |
| **MEMBER_SUSPENDED** | 정지된 회원 | 정지 해제 후 이용 |
| **BOOK_NOT_FOUND** | 도서를 찾을 수 없음 | 도서 ID 확인 |
| **BOOK_NOT_AVAILABLE** | 대출 불가능한 도서 | 예약 또는 다른 도서 선택 |
| **LOAN_LIMIT_EXCEEDED** | 대출 한도 초과 | 기존 도서 반납 후 대출 |
| **HAS_OVERDUE_LOANS** | 연체 도서 보유 | 연체 도서 반납 및 연체료 납부 |
| **DUPLICATE_LOAN** | 중복 대출 시도 | 기존 대출 확인 |
| **RESERVATION_LIMIT_EXCEEDED** | 예약 한도 초과 | 기존 예약 취소 후 예약 |

---

## 6. API 문서화

### 6.1 OpenAPI 3.0 스펙 예시
```yaml
openapi: 3.0.3
info:
  title: Library Management API
  description: 도서관 관리 시스템 REST API
  version: 1.0.0
  contact:
    name: API 지원팀
    email: api-support@library.com
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.library.com/v1
    description: 운영 서버
  - url: https://staging-api.library.com/v1
    description: 스테이징 서버

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    ErrorResponse:
      type: object
      properties:
        success:
          type: boolean
          example: false
        error:
          $ref: '#/components/schemas/ErrorDetail'

    Book:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 1
        isbn:
          type: string
          pattern: '^[0-9]{13}
          example: "9788932917245"
        title:
          type: string
          maxLength: 200
          example: "82년생 김지영"

paths:
  /books:
    get:
      summary: 도서 목록 조회
      tags: [Books]
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            minimum: 0
            default: 0
      responses:
        '200':
          description: 성공
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PagedBooksResponse'
```

### 6.2 API 문서 생성 도구
```java
// Spring Boot + Swagger 설정
@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .version("v1.0.0")
                        .description("도서관 관리 시스템 REST API"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

// Controller 어노테이션 예시
@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "도서 관리 API")
public class BookController {

    @GetMapping
    @Operation(summary = "도서 목록 조회", description = "페이지네이션된 도서 목록을 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = PagedBooksResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<Page<BookDTO>>> getBooks(
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "검색어", example = "김지영")
            @RequestParam(required = false) String search) {
        // 구현 코드
    }
}
```

## 7. 체크리스트 및 품질 관리

### 7.1 API 설계 체크리스트
```
□ RESTful 원칙을 준수하는가?
□ URL 네이밍 규칙이 일관되는가?
□ HTTP 메서드를 올바르게 사용하는가?
□ HTTP 상태 코드를 적절히 사용하는가?
□ 요청/응답 형식이 표준화되어 있는가?
□ 에러 응답이 명확하고 도움이 되는가?
□ 페이지네이션이 구현되어 있는가?
□ 적절한 인증/인가가 구현되어 있는가?
```

### 7.2 보안 체크리스트
```
□ 입력값 검증이 충분한가?
□ JWT 토큰이 안전하게 관리되는가?
□ 권한 체크가 모든 엔드포인트에 적용되는가?
□ 에러 메시지에서 시스템 정보가 노출되지 않는가?
```

### 7.3 성능 체크리스트
```
□ N+1 쿼리 문제가 해결되었는가?
□ 데이터베이스 인덱스가 적절한가?
```

## 8. 마무리

### 8.1 주요 포인트 요약
1. **RESTful 설계**: HTTP 메서드와 상태 코드의 올바른 사용
2. **일관성 유지**: 모든 API에서 동일한 규칙과 형식 적용
3. **보안 강화**: 인증, 인가, 입력 검증을 통한 보안 확보
4. **문서화**: 명확하고 완전한 API 문서 제공

### 8.2 추천 도구 및 라이브러리
- **문서화**: Swagger/OpenAPI, Postman
- **보안**: Spring Security, JWT

### 8.3 향후 고도화 방안
- **GraphQL 지원**: 클라이언트별 맞춤 데이터 제공
- **WebSocket**: 실시간 알림 및 채팅 기능
- **이벤트 기반 아키텍처**: 마이크로서비스 간 느슨한 결합
- **API 게이트웨이**: 중앙화된 API 관리
- **성능**: 캐싱, 압축을 통한 성능 향상 Redis, Caffeine Cache
- **테스트**: REST Assured, WireMock, TestContainers
- **모니터링**: 로깅, 메트릭을 통한 운영 상황 파악 Micrometer, Prometheus, Grafana

---
            