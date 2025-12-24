# 공통 스캐폴딩 가이드 (Common Scaffolding Guide)

## 1. 문서 목적 및 적용 범위
본 문서는 MSA(Microservice Architecture) 환경에서 각 서비스의 일관된 구조를 유지하고, 신규 개발자가 빠르게 프로젝트에 적응하며, 서비스 간의 책임과 경계를 명확히 하기 위한 스캐폴딩 가이드를 제공합니다.

### 적용 범위
- **대상 서비스 (6개):** Membership, Banking, Money, Remittance, Payment, Settlement
- **제외 모듈:** 공통 모듈(`common`), 로깅 모듈 등 인프라성 공통 컴포넌트

---

## 2. 공통 설계 원칙

### 2.1 기술 스택 기준선 (Technical Baseline)
모든 서비스는 다음 기술 스택을 엄격히 준수해야 합니다.
- **Java 버전:** Java 21
- **Spring Boot:** 3.5.x
- **Build Tool:** Gradle (Multi-module 구조)
- **Local Profile:** 로컬 실행을 위해 `local` 프로필을 기본으로 사용합니다.

### 2.2 핵심 아키텍처: Hexagonal Architecture
모든 서비스는 **Hexagonal Architecture (Port & Adapter)** 구조를 공통 골격으로 사용합니다. 이는 외부 프레임워크나 기술로부터 도메인 로직을 격리하여 유지보수성과 테스트 용이성을 극대화하기 위함입니다.

---

## 3. 공통 스캐폴딩 규칙

### 3.1 모듈 및 패키지 네이밍
- **모듈명:** `{service-name}-service` (예: `membership-service`)
- **패키지 루트:** `org.opennuri.study.architecture.{service-name}`
- **애플리케이션 엔트리 클래스:** `{ServiceName}Application` (예: `MembershipApplication`)

### 3.2 공통 설정 파일 구조
- `src/main/resources/application.yml`: 기본 설정
- `src/main/resources/application-local.yml`: 로컬 개발용 설정 (DB 연결 등)

### 3.3 공통 디렉토리 구조 (Hexagonal 상세)
```text
src/main/java/.../{service}
├─ adapter
│  ├─ in
│  │  ├─ web       // REST API 컨트롤러, 요청/응답 DTO
│  │  ├─ batch     // Spring Batch Job, Step 설정
│  │  └─ message   // Message Consumer (Kafka, RabbitMQ 등)
│  └─ out
│     ├─ persistence // DB 접근 (JPA Entity, Repository), 영속성 어댑터
│     └─ external    // 외부 시스템 연동 (FeignClient, RestTemplate) 어댑터
├─ application
│  └─ service     // 비즈니스 로직 구현 (UseCase 구현체)
├─ port
│  ├─ in          // 입력 인터페이스 (UseCase, Command)
│  └─ out         // 출력 인터페이스 (DB access, 외부 API 호출 인터페이스)
└─ domain         // 순수 비즈니스 객체 (Entity, VO, 도메인 로직)
```

### 3.4 계층별 책임 및 의존성 방향
- **의존성 방향:** `adapter` → `application` → `domain` (안쪽으로만 향해야 함)
- **domain:** 프레임워크 의존성 없이 순수 Java 코드로만 작성 (JPA 어노테이션 사용 금지 권장).
- **application:** 비즈니스 유스케이스를 오케스트레이션하며, `port.in`을 구현하고 `port.out`을 사용함.
- **adapter:** 외부 기술(HTTP, JPA, Kafka 등)과 애플리케이션을 연결하는 변환기 역할.

---

## 4. 서비스별 스캐폴딩 가이드

각 서비스는 고유의 책임에 따라 필수적으로 포함해야 하는 어댑터와 의존성이 달라집니다.

| 서비스명 | 핵심 책임 (Responsibility) | 대표 Inbound | 대표 Outbound | 필수 어댑터 패키지 | 비고 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Membership** | 회원 등록/수정/조회 및 인증 상태 관리 | API | DB | `adapter.out.persistence` | - |
| **Banking** | 외부 은행 시스템 연동 및 계좌 확인 | API | 외부 은행 API | `adapter.out.external` | 외부 시스템 연동 중심 |
| **Money** | 선불 잔액 관리 및 충전/사용 이력 기록 | API, Consumer | DB | `adapter.in.message` | 트랜잭션 무결성 중요 |
| **Remittance** | 송금 흐름 제어 (멤버십/머니/뱅킹 연동) | API | 타 서비스 API | `adapter.out.external` | 서비스 간 오케스트레이션 |
| **Payment** | 결제 승인 및 취소 트랜잭션 처리 | API | DB, 외부 PG | `adapter.out.external` | 외부 결제 대행사 연동 |
| **Settlement** | 대량 데이터를 기반으로 한 정산 처리 | Batch | DB, 파일 생성 | `adapter.in.batch` | Spring Batch 필수 사용 |

---

## 5. 빌드 및 실행 가이드

### 5.1 Gradle 빌드
- **전체 빌드:**
  ```powershell
  ./gradlew build
  ```
- **특정 서비스 단일 빌드:**
  ```powershell
  ./gradlew :membership-service:build
  ```

### 5.2 로컬 실행
- **IDE 실행:** `{ServiceName}Application` 클래스의 `main` 메서드 실행 (VM 옵션: `-Dspring.profiles.active=local`)
- **Docker Compose 기반 실행:**
  ```powershell
  docker-compose up -d
  ```

### 5.3 테스트 실행
- **전체 테스트:** `./gradlew test`
- **단일 서비스 테스트:** `./gradlew :membership-service:test`

---

## 6. 금지 사항 및 안티 패턴 (Strict Rules)

본 규칙을 위반할 경우 코드 리뷰 단계에서 반려됩니다.

### 6.1 타 서비스 DB/테이블 직접 접근 금지
- **잘못된 예:** `MembershipRepository`에서 `money_table`을 직접 조인하여 조회.
- **올바른 대안:** 필요한 데이터는 해당 서비스의 API를 호출하거나, 이벤트를 구독하여 로컬 DB에 동기화(Read Model)하여 사용.

### 6.2 도메인 엔티티(Entity, Aggregate) 공유 금지
- **잘못된 예:** `common` 모듈에 `User` 엔티티를 두고 모든 서비스가 이를 상속받아 사용.
- **올바른 대안:** 각 서비스는 자신의 맥락(Bounded Context)에 맞는 고유의 도메인 모델을 가짐.

### 6.3 domain 계층의 프레임워크 의존성 사용 금지
- **잘못된 예:** `domain` 클래스에 `@Entity`, `@Table` 등 JPA 어노테이션 사용.
- **올바른 대안:** `domain`은 순수 POJO로 작성하고, `adapter.out.persistence` 계층에서 JPA Entity를 별도로 정의하여 매핑.

### 6.4 어댑터 간 직접 호출 금지
- **잘못된 예:** `WebAdapter`에서 `PersistenceAdapter`를 직접 주입받아 호출.
- **올바른 대안:** 반드시 `application.service` (UseCase)를 거쳐서 호출해야 함.

---

## 7. 신규 개발자 Quick Start
1. Repository 클론
2. Java 21 및 Gradle 환경 확인
3. `docker-compose up -d`로 인프라 실행 (필요 시)
4. `./gradlew :membership-service:bootRun`으로 서비스 기동 확인
5. `http://localhost:8081/swagger-ui.html` 접속하여 API 문서 확인
