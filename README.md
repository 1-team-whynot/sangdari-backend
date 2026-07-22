# 상다리 (Sangdari) Backend

행사 조건에 맞는 밥차·푸드트럭 업체를 연결하고, 견적 요청·예약·결제 흐름을 제공하는 **상다리** 서비스의 REST API 서버입니다.

프론트엔드 저장소: [sangdari-frontend](https://github.com/1-team-whynot/sangdari-frontend)

## 주요 기능

- JWT 기반 회원가입, 로그인·로그아웃, 토큰 재발급
- 이메일 중복 확인, 비밀번호 재설정 및 회원 정보 관리
- 행사 조건 기반 업체 조회와 업체·메뉴 상세 조회
- 견적 요청 생성, 내 요청 목록 조회, 예약 취소
- 계약금·잔금 결제 준비 및 확인, 토스페이먼츠 연동과 모의 결제
- 공통 응답 형식과 사용자 친화적 예외 응답

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot `4.0.7`, Spring MVC, Spring Security, Validation |
| Persistence | MyBatis Spring Boot Starter `4.0.1`, MySQL Connector/J |
| Authentication | JJWT `0.12.6` |
| Build | Gradle Wrapper |
| External API | 토스페이먼츠 결제 승인 API |
| Deployment | Docker |

## 프로젝트 구조

```text
src/main/
├── java/com/sangdari/
│   ├── domain/
│   │   ├── auth/           # 인증
│   │   ├── user/           # 회원 정보
│   │   ├── store/          # 업체·메뉴 조회
│   │   ├── reservation/    # 견적 요청·예약
│   │   └── payment/        # 결제
│   └── global/
│       ├── config/         # CORS, Web 설정
│       ├── exception/      # 전역 예외 처리
│       ├── response/       # GlobalResponse
│       └── security/       # JWT, 필터, Security 설정
└── resources/
    ├── mapper/             # MyBatis XML Mapper
    ├── application.yml
    └── application-prod.yml
```

## 시작하기

### 요구 사항

- JDK 17
- MySQL 8.4 이상 권장
- 토스페이먼츠 API 키(실결제 승인 API를 사용할 경우)

### 환경 변수

`.env.template`을 복사해 프로젝트 루트에 `.env`를 만들고 값을 채웁니다. `.env`는 Git에 커밋하지 않습니다.

```bash
cp .env.template .env
```

기본 프로필(`application.yml`)은 아래 환경 변수를 사용합니다.

```dotenv
SPRING_DB_URL=jdbc:mysql://localhost:3306/sangdari?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
SPRING_DB_USERNAME=DB_사용자명
SPRING_DB_PASSWORD=DB_비밀번호

CORS_ALLOWED_ORIGINS=http://localhost:5173
CORS_MAX_AGE=3600

SECURITY_JWT_ISSUER=sangdari@example.com
SECURITY_JWT_TYPE=JWT
SECURITY_JWT_ACCESS_TOKEN_EXPIRY=1000000
SECURITY_JWT_REFRESH_TOKEN_EXPIRY=1296000000
SECURITY_JWT_REFRESH_TOKEN_COOKIE_NAME=refresh-token
SECURITY_JWT_REFRESH_TOKEN_COOKIE_EXPIRY=1296000
SECURITY_JWT_SECRET_KEY=Base64로_인코딩한_충분히_긴_비밀값
SECURITY_JWT_HEADER_KEY=Authorization
SECURITY_JWT_SCHEME=Bearer
SECURITY_JWT_REISSUE_URI=/api/reissue-token

TOSS_SECRET_KEY=토스페이먼츠_시크릿_키
```

운영 프로필(`application-prod.yml`)은 DB에 `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`를, JWT에 `JWT_SECRET`를 사용합니다. 운영 환경에 맞춰 해당 값을 별도로 주입하세요.

### 실행 및 테스트

```bash
git clone https://github.com/1-team-whynot/sangdari-backend.git
cd sangdari-backend
cp .env.template .env
bash ./gradlew bootRun
```

Windows에서는 `gradlew.bat bootRun`을 사용합니다. 기본 포트는 `8080`이며, API 기본 경로는 `http://localhost:8080/api`입니다.

| 명령어 | 설명 |
| --- | --- |
| `bash ./gradlew bootRun` | 로컬 API 서버 실행 |
| `bash ./gradlew test` | 테스트 실행 |
| `bash ./gradlew bootJar` | 실행 가능한 JAR 생성 |

## API 개요

모든 일반 응답은 아래 형식을 사용합니다.

```json
{
  "code": "00",
  "message": "처리 결과 메시지",
  "data": {}
}
```

인증이 필요한 API에는 `Authorization: Bearer <access-token>` 헤더를 포함합니다. 리프레시 토큰은 HTTP 쿠키로 처리됩니다.

| 도메인 | Method | Endpoint | 설명 |
| --- | --- | --- | --- |
| 인증 | POST | `/api/auth/login` | 로그인 |
| 인증 | POST | `/api/auth/logout` | 로그아웃 |
| 인증 | POST | `/api/reissue-token` | 액세스 토큰 재발급 |
| 인증 | POST | `/api/users/signup` | 회원가입 |
| 인증 | GET | `/api/email-check` | 이메일 중복 확인 |
| 인증 | PUT | `/api/find-password` | 비밀번호 재설정 |
| 회원 | GET / PUT / DELETE | `/api/users/info`, `/api/users/info-update`, `/api/users/withdraw` | 내 정보 조회·수정·탈퇴 |
| 업체 | GET | `/api/allStores`, `/api/stores`, `/api/stores/{storeId}` | 전체/필터 업체 목록 및 상세 |
| 예약 | GET / POST | `/api/reservations/my`, `/api/reservations` | 내 요청 목록, 견적 요청 생성 |
| 예약 | PATCH | `/api/reservations/{reservationId}/cancel` | 예약 취소 |
| 결제 | POST | `/api/payments/ready` | 결제 정보 준비 |
| 결제 | POST | `/api/payments/mock-confirm` | 모의 결제 승인 |
| 결제 | POST | `/api/payments/confirm` | 토스페이먼츠 결제 승인 |

각 API의 요청·응답 DTO는 해당 도메인의 `requests`, `responses` 패키지에서 확인할 수 있습니다.

## 도메인 흐름

```text
체크리스트 입력 → 업체 탐색·메뉴 선택 → 견적 요청
→ 견적 확인 → 계약금 결제 → 예약 확정 → 잔금 결제
```

예약 상태는 `REQUESTED`, `ESTIMATED`, `CONFIRMED`, `PAYMENT_COMPLETED`, `COMPLETED`, `CANCELED`로 관리합니다. 결제 완료 시점과 예약 상태 전환은 서버에서 검증합니다.

## Docker 실행

```bash
docker build -t sangdari-backend .
docker run --rm -p 8080:8080 --env-file .env sangdari-backend
```

운영 프로필을 사용할 경우 필요한 운영 환경 변수와 함께 `SPRING_PROFILES_ACTIVE=prod`를 전달하세요.

## 보안 및 운영 주의 사항

- DB 비밀번호, JWT 비밀값, 토스페이먼츠 시크릿 키는 저장소에 커밋하지 마세요.
- JWT 비밀값은 충분히 긴 Base64 인코딩 값으로 설정합니다.
- `CORS_ALLOWED_ORIGINS`에는 허용할 프론트엔드 주소만 쉼표로 구분해 등록합니다.
- `mock-confirm`은 시연·개발용 결제 흐름입니다. 운영 결제는 토스페이먼츠 승인 결과를 서버에서 검증하는 `/confirm` 흐름을 사용합니다.
