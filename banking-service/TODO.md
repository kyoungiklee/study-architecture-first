# Banking Service 개선사항 TODO

**담당자**: kyoungik.lee
**생성일**: 2025-12-19
**기준 서비스**: membership-service (표준 참조)

---

## 🔴 CRITICAL (배포 차단 이슈)

### 1. 테스트 커버리지 확대
**현재 상태**: ~5-10% (BankingControllerTest 1개만 존재)
**목표 상태**: ~80-90% (membership-service 수준)

- [ ] **BankAccountController 테스트 작성**
  - [ ] Controller Layer: MockMvc 기반 웹 계층 테스트
  - [ ] Service Layer: BankAccountCommandService 단위 테스트
  - [ ] Service Layer: BankAccountQueryService 단위 테스트
  - [ ] Repository Layer: 리포지토리 통합 테스트 (H2)
  - [ ] Adapter Layer: BankAccountPersistenceAdapter 테스트
  - [ ] 부정적 테스트 케이스 추가 (에러 시나리오)

- [ ] **FirmbankingController 테스트 작성** (현재 0개)
  - [ ] Controller Layer: MockMvc 기반 테스트 (5개 엔드포인트)
  - [ ] Service Layer: FirmbankingCommandService 테스트
  - [ ] Repository Layer: 펌뱅킹 리포지토리 테스트
  - [ ] 상태 전이 테스트 (신청 → 완료, 신청 → 실패)
  - [ ] 비즈니스 룰 검증 테스트

- [ ] **Resilience4j 통합 테스트**
  - [ ] CircuitBreaker 동작 검증 테스트
  - [ ] Retry 로직 테스트
  - [ ] Fallback 시나리오 테스트
  - [ ] 타임아웃 처리 테스트

**참조 파일**:
- `membership-service/src/test/java/.../{Controller,Service,Repository}Test.java`

**예상 작업 시간**: 3-4일

---

### 2. FirmbankingController OpenAPI 문서화
**현재 상태**: OpenAPI 어노테이션 0% (완전 누락)
**목표 상태**: 100% (membership-service 수준)

- [ ] **클래스 레벨 문서화**
  - [ ] `@Tag(name = "Firmbanking", description = "펌뱅킹 관리 API")` 추가
  - [ ] 클래스 Javadoc 작성 (아키텍처 설명 포함)

- [ ] **POST /firmbanking/request (펌뱅킹 신청)**
  - [ ] `@Operation(summary, description)` 추가
  - [ ] `@ApiResponses` 추가 (201, 400)
  - [ ] `@RequestBody` 설명 추가
  - [ ] 메서드 Javadoc 작성

- [ ] **GET /firmbanking/{id} (단건 조회)**
  - [ ] `@Operation` 추가
  - [ ] `@ApiResponses` 추가 (200, 404)
  - [ ] `@Parameter` 설명 추가 (id)
  - [ ] 메서드 Javadoc 작성

- [ ] **GET /firmbanking (목록 조회)**
  - [ ] `@Operation` 추가
  - [ ] `@ApiResponses` 추가 (200)
  - [ ] 쿼리 파라미터 `@Parameter` 추가
  - [ ] 메서드 Javadoc 작성

- [ ] **PUT /firmbanking/{id} (상태 업데이트)**
  - [ ] `@Operation` 추가
  - [ ] `@ApiResponses` 추가 (200, 400, 404)
  - [ ] `@Parameter` 및 `@RequestBody` 설명
  - [ ] 메서드 Javadoc 작성

- [ ] **DELETE /firmbanking/{id} (삭제)**
  - [ ] `@Operation` 추가
  - [ ] `@ApiResponses` 추가 (204, 404)
  - [ ] `@Parameter` 설명 추가
  - [ ] 메서드 Javadoc 작성

**참조 파일**:
- `membership-service/.../MembershipController.java` (Line 25-220)

**예상 작업 시간**: 1일

---

### 3. 필드 명명 일관성 수정
**이슈**: Java 컨벤션 위반, membership-service와 불일치

- [ ] **BankAccountDto.valid → isValid 변경**
  - [ ] 필드명 변경: `private boolean valid` → `private boolean isValid`
  - [ ] Getter/Setter 자동 생성 (Lombok)
  - [ ] 참조하는 모든 코드 업데이트
  - [ ] 테스트 코드 업데이트

- [ ] **BankAccountEntity.valid → isValid 변경**
  - [ ] JPA Entity 필드 변경
  - [ ] 데이터베이스 컬럼 매핑 확인 (`@Column(name = "is_valid")`)
  - [ ] 마이그레이션 스크립트 작성 (필요 시)

- [ ] **관련 Command/Query 객체 일괄 수정**
  - [ ] `RegisterBankAccountCommand`
  - [ ] `UpdateBankAccountCommand`
  - [ ] 모든 mapper/converter 로직

**영향 범위**: BankAccount 도메인 전체
**예상 작업 시간**: 0.5일

---

## 🟠 HIGH (조속히 처리)

### 4. 도메인별 커스텀 예외 처리
**현재**: Generic exception만 처리
**목표**: 비즈니스 의미 있는 예외 처리

- [ ] **커스텀 예외 클래스 생성**
  ```java
  // common 모듈 또는 banking-service/domain
  - BankAccountNotFoundException extends RuntimeException
  - InsufficientBalanceException extends RuntimeException
  - ExternalBankServiceException extends RuntimeException
  - FirmbankingRequestFailedException extends RuntimeException
  - InvalidBankAccountStatusException extends RuntimeException
  ```

- [ ] **GlobalExceptionHandler 확장**
  - [ ] `@ExceptionHandler(BankAccountNotFoundException.class)` → 404
  - [ ] `@ExceptionHandler(InsufficientBalanceException.class)` → 409 Conflict
  - [ ] `@ExceptionHandler(ExternalBankServiceException.class)` → 503 Service Unavailable
  - [ ] CircuitBreakerOpenException 처리 → 503
  - [ ] RequestNotPermitted (rate limit) 처리 → 429

- [ ] **서비스 계층에 예외 적용**
  - [ ] 기존 `IllegalArgumentException` → 적절한 커스텀 예외로 교체
  - [ ] 명확한 에러 메시지 작성

**참조**:
- Resilience4j Exception Types
- Spring HTTP Status Codes

**예상 작업 시간**: 1일

---

### 5. 커스텀 Validation 어노테이션 추가
**목표**: 비즈니스 룰을 재사용 가능한 검증으로 추상화

- [ ] **@ValidBankCode 어노테이션 생성**
  ```java
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @Constraint(validatedBy = BankCodeValidator.class)
  public @interface ValidBankCode {
      String message() default "유효하지 않은 은행 코드입니다";
      // ...
  }
  ```
  - [ ] Validator 구현: 4자리 숫자, 실존 은행 코드 검증
  - [ ] FirmbankingDto에 적용

- [ ] **@ValidAccountNumber 어노테이션 생성**
  - [ ] Validator 구현: 10-14자리, 숫자만 허용
  - [ ] 체크섬 검증 (선택)

- [ ] **@ValidAmount 어노테이션 생성**
  - [ ] 최소/최대 금액 제한
  - [ ] 0보다 큰 값 검증
  - [ ] 소수점 제한

- [ ] **FirmbankingDto에 적용**
  ```java
  @ValidBankCode
  private String fromBankCode;

  @ValidAccountNumber
  private String fromAccountNumber;

  @ValidAmount(min = 100, max = 10000000)
  private Long amount;
  ```

**참조**:
- Jakarta Bean Validation custom constraints

**예상 작업 시간**: 1.5일

---

### 6. Controller 아키텍처 어노테이션 표준화

- [ ] **@WebAdapter 어노테이션 추가**
  - [ ] `BankAccountController`에 추가
  - [ ] `FirmbankingController`에 추가
  - [ ] `BankingController`에 추가

- [ ] **UseCase 어노테이션 일관성**
  - [ ] 인터페이스와 구현체 모두에 `@UseCase` 적용
  - [ ] 패키지 구조 검토 (application.port.in)

**예상 작업 시간**: 0.5일

---

### 7. FirmbankingController Javadoc 작성
**현재**: 인라인 주석 4개, 메서드 Javadoc 0개
**목표**: 모든 메서드 및 클래스 문서화

- [ ] **클래스 레벨 Javadoc**
  - [ ] 펌뱅킹 프로세스 설명
  - [ ] 아키텍처 패턴 설명 (Web → UseCase → Port)
  - [ ] 외부 은행 연동 설명

- [ ] **메서드별 Javadoc** (5개 메서드)
  - [ ] `@param` 설명
  - [ ] `@return` 설명
  - [ ] 비즈니스 룰 설명
  - [ ] 에러 시나리오 설명

- [ ] **인라인 주석 보강**
  - [ ] 복잡한 로직 설명
  - [ ] 외부 서비스 호출 시점 표시

**참조 파일**:
- `membership-service/.../MembershipController.java` (Line 25-42, 57-62 등)

**예상 작업 시간**: 0.5일

---

### 8. Resilience4j 통합 테스트 및 문서화

- [ ] **CircuitBreaker 동작 테스트**
  - [ ] 장애 임계치 도달 시 Open 상태 전환 검증
  - [ ] Half-Open → Closed 복구 검증
  - [ ] Fallback 메서드 호출 검증

- [ ] **Retry 로직 테스트**
  - [ ] 최대 재시도 횟수 검증
  - [ ] Exponential backoff 검증
  - [ ] 재시도 불가 예외 검증

- [ ] **Configuration 문서화**
  - [ ] `application-local.yml`에 인라인 주석 추가
  - [ ] 각 파라미터 의미 설명 (failureRateThreshold, waitDurationInOpenState 등)
  - [ ] 값 선정 근거 문서화

**예상 작업 시간**: 2일

---

## 🟡 MEDIUM (품질 개선)

### 9. DTO 어노테이션 표준화

- [ ] **모든 DTO에 @Schema 추가**
  - [ ] `FirmbankingDto` (현재 누락)
  - [ ] `BankAccountHistoryDto` (현재 누락)
  - [ ] 각 필드에 `description`, `example` 속성 추가
  - [ ] `requiredMode = Schema.RequiredMode.REQUIRED` 명시

- [ ] **Validation 메시지 표준화**
  - [ ] `BankAccountDto`: 모든 validation에 message 속성 추가
  - [ ] `FirmbankingDto`: message 상세화
  - [ ] 한글 메시지 일관성 유지

- [ ] **Validation 어노테이션 보강**
  - [ ] `@Size` 추가 (bankCode: 4자리, accountNumber: 10-14자리)
  - [ ] `@Min`, `@Max` 추가 (amount 범위 제한)
  - [ ] `@Pattern` 추가 (숫자/영문 제한)

**참조 파일**:
- `membership-service/.../RegisterMembershipRequest.java` (Line 9-46)

**예상 작업 시간**: 1일

---

### 10. 설정 파일 문서화

- [ ] **application-local.yml 주석 추가**
  ```yaml
  resilience4j:
    circuitbreaker:
      instances:
        externalBank:
          # 실패율 임계치: 50% 이상 실패 시 Circuit Open
          failureRateThreshold: 50
          # Open 상태 유지 시간: 10초 후 Half-Open 전환
          waitDurationInOpenState: 10000
          # ...
  ```

- [ ] **프로파일별 설정 분리 계획**
  - [ ] `application-dev.yml` 작성 계획
  - [ ] `application-prod.yml` 작성 계획
  - [ ] 환경별 차이점 문서화

- [ ] **Health Check 설정 추가**
  ```yaml
  management:
    health:
      circuitbreakers:
        enabled: true
    endpoint:
      health:
        show-details: always
  ```

**예상 작업 시간**: 0.5일

---

### 11. Lombok 어노테이션 패턴 통일

- [ ] **Domain 객체에 @Value 적용**
  - [ ] `BankAccount` domain model → `@Value` (불변)
  - [ ] `Firmbanking` domain model → `@Value` (불변)
  - [ ] 현재 `@Data` 사용 중이면 변경

- [ ] **DTO는 @Data 유지**
  - [ ] `BankAccountDto`
  - [ ] `FirmbankingDto`
  - [ ] Request/Response 객체

- [ ] **Builder 패턴 일관성**
  - [ ] 모든 Command 객체에 `@Builder` 추가
  - [ ] Constructor 패턴 통일

**참조**:
- `membership-service/domain/Membership.java` (immutable with @Value)

**예상 작업 시간**: 1일

---

### 12. CQRS 패턴 명확화

- [ ] **FirmbankingController 리팩토링**
  - [ ] Command 작업과 Query 작업 분리
  - [ ] `FirmbankingCommandService` 생성 (신청, 수정, 삭제)
  - [ ] `FirmbankingQueryService` 생성 (조회)
  - [ ] Controller에서 명확히 구분하여 호출

- [ ] **Port 인터페이스 정리**
  - [ ] `CommandFirmbankingPort` 생성
  - [ ] `QueryFirmbankingPort` 생성
  - [ ] 기존 Port 인터페이스 리팩토링

**예상 작업 시간**: 1.5일

---

### 13. 추가 에러 메시지 로컬라이제이션

- [ ] **messages.properties 파일 생성**
  ```properties
  # 한글 메시지
  validation.bankaccount.name.required=계좌 소유자 이름은 필수입니다.
  validation.bankaccount.balance.positive=잔액은 0보다 커야 합니다.
  # ...
  ```

- [ ] **Validation 메시지에 적용**
  ```java
  @NotBlank(message = "{validation.bankaccount.name.required}")
  private String accountHolderName;
  ```

- [ ] **ErrorResponse 메시지 i18n 지원**
  - [ ] MessageSource 설정
  - [ ] GlobalExceptionHandler에서 메시지 해석

**예상 작업 시간**: 1일

---

### 14. 헬스체크 및 메트릭 강화

- [ ] **Custom HealthIndicator 추가**
  ```java
  @Component
  public class ExternalBankHealthIndicator implements HealthIndicator {
      // Circuit Breaker 상태 확인
      // 외부 은행 API 응답 확인
  }
  ```

- [ ] **Actuator 엔드포인트 확장**
  - [ ] `/actuator/health` 상세 정보 노출
  - [ ] `/actuator/metrics` 커스텀 메트릭 추가
  - [ ] `/actuator/circuitbreakers` 상태 확인

- [ ] **Micrometer 통합**
  - [ ] 비즈니스 메트릭 측정 (펌뱅킹 성공률, 평균 처리 시간)
  - [ ] Counter, Timer, Gauge 활용

**예상 작업 시간**: 1.5일

---

## 📊 진행 상황 요약

| 우선순위 | 완료 | 전체 | 진행률 |
|---------|------|------|--------|
| 🔴 CRITICAL | 0 | 3 | 0% |
| 🟠 HIGH | 0 | 6 | 0% |
| 🟡 MEDIUM | 0 | 6 | 0% |
| **전체** | **0** | **15** | **0%** |

---

## 📝 체크리스트 템플릿

각 작업 완료 시:
- [ ] 코드 작성 완료
- [ ] 단위 테스트 작성 및 통과
- [ ] 통합 테스트 통과
- [ ] 코드 리뷰 완료
- [ ] 문서 업데이트 (Javadoc, README)
- [ ] Swagger UI 확인

---

## 📚 참조 자료

1. **membership-service** (표준 참조)
   - `MembershipController.java` - OpenAPI 문서화 패턴
   - `GlobalExceptionHandler.java` - 예외 처리 패턴
   - 테스트 패키지 전체 - 테스트 작성 방법

2. **Spring Boot 공식 문서**
   - Bean Validation
   - OpenAPI 3.0 Specification
   - Resilience4j Integration

3. **프로젝트 내부 문서**
   - `common` 모듈 - 공통 어노테이션 및 유틸리티

---

## 💡 작업 시 주의사항

1. **호환성 유지**: 기존 API 계약 변경 시 버전 관리 고려
2. **테스트 우선**: 모든 변경사항은 테스트 코드 작성 후 진행
3. **단계적 적용**: CRITICAL → HIGH → MEDIUM 순서로 진행
4. **코드 리뷰**: 각 작업 단위별 리뷰 요청
5. **문서 동기화**: 코드 변경 시 관련 문서 즉시 업데이트

---

**최종 업데이트**: 2025-12-19
**다음 리뷰 예정일**: 2025-12-26 (1주 후)
