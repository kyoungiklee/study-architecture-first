# Banking Service

---

## 1) 책임과 범위 (Responsibility & Scope)

### 핵심 책임

Banking Service는 **외부 금융기관(은행/이체 API)과의 연동을 단일 책임으로 캡슐화**한다.

- 외부 계좌/은행 API 호출의 단일 진입점
- 이체(Transfer) 실행 및 결과 확정
- 외부 연동 장애의 격리 (Timeout / Retry / Circuit Breaker)
- Saga Participant로서 단계 결과(Event / Response) 제공
- Idempotency 기반 중복 이체 방지

---

### 범위에 포함 (In Scope)

- 이체 요청 접수 (내부 Command)
- 외부 은행 API 호출 및 응답 해석
- 이체 상태 추적 (요청 / 진행 / 성공 / 실패)
- 재시도 정책
    - 제한적 자동 재시도
    - 운영자 수동 재처리
- 감사 / 추적 로그 기록 (Log / Trace / Audit)

---

### 범위에서 제외 (Out of Scope)

- 잔액 관리 (Money Service 책임)
- 결제 승인 상태 관리 (Payment Service 책임)
- 정산 처리 (Settlement Service 책임)
- 사용자 인증 / 권한 발급 (Membership Service 책임)

---

## 2) 외부 계좌 연동 (External Account Integration)

### 2.1 연동 목표

- 은행 API 변경 또는 장애가 **Payment / Money 도메인으로 전파되지 않도록 차단**
- “외부 호출은 언제나 실패할 수 있다”는 전제하에 **Fail-Closed** 기본 적용

---

### 2.2 연동 방식 (권장)

- Banking Service 내부에서 **Bank Client Adapter**를 통해 외부 은행 API 호출
- 호출 시 반드시 포함해야 할 정보:
    - `traceId` : 분산 추적
    - `idempotencyKey` : 중복 이체 방지
    - `referenceId` : paymentId 또는 sagaId
    - `amount`
    - `fromAccount`
    - `toAccount`

---

### 2.3 외부 계좌 정보 처리 원칙 (보안)

- 계좌번호 / 개인정보 처리 원칙:
    - 로그에 원문 저장 금지 (마스킹 또는 토큰화)
    - Vault 또는 별도 안전한 저장소에서 시크릿 / 키 관리
- 외부 API Key / 인증서 / 토큰:
    - Vault 기반 관리
    - 관련 ADR: `ADR-005`, `ADR-046`

---

## 3) Adapter 구조 (Hexagonal 관점)

Banking Service는 외부 은행 호출이 핵심이므로 **Hexagonal Architecture를 명시적으로 유지**한다.

---

### 3.1 내부 패키지 템플릿 (요약)

#### Domain
- Transfer 규칙 및 상태 머신
- 실패 분류 (재시도 가능 / 재시도 불가)
- 외부 은행 호출 Port 정의

#### Application
- Transfer UseCase
- 요청 처리 흐름
- 트랜잭션 경계 정의

#### Adapter

- **In**
    - 내부 Command API
        - 예: `/api/v1/banking/transfers`

- **Out**
    - Bank API Client Adapter (HTTP / gRPC)
    - Persistence Adapter (이체 요청 / 상태 저장)
    - Messaging Adapter (이체 결과 이벤트 발행)

---

### 3.2 Port & Adapter 예시 (개념)

#### BankTransferPort
- `requestTransfer(command)`
- `queryTransferStatus(referenceId)`

#### BankApiClientAdapter
- 은행별 프로토콜
- 인증 / 서명 처리 포함

#### TransferRepositoryAdapter
- 이체 요청 및 상태 저장
- 중복 방지 및 재처리 근거 제공

---

## 4) API 목록 (MVP 기준)

> 모든 이체 Command는 **Idempotency-Key 필수**  
> 동일 요청 재시도 시 중복 이체 금지

---

### 4.1 이체 요청

POST /api/v1/banking/transfers


**Header**


Idempotency-Key: <uuid>


**Request**
```json
{
  "referenceId": "string",
  "fromAccountRef": "string",
  "toAccountRef": "string",
  "amount": 10000,
  "reason": "string"
}
```

Response
```json
{
  "transferId": "string",
  "status": "REQUESTED | PROCESSING | SUCCESS | FAILED"
}
```

---

### 4.2 이체 상태 조회 (내부용)

```
GET /api/v1/banking/transfers/{transferId}
```

**Response**

```json
{
  "transferId": "string",
  "referenceId": "string",
  "status": "SUCCESS | FAILED | PROCESSING",
  "failureCode": "string | null",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

---

### 4.3 재처리 (관리자)

```
POST /api/v1/banking/transfers/{transferId}/retry
```

* 권한: `ADMIN` / `OPERATOR`

**Response**

```json
{
  "status": "PROCESSING | SUCCESS | FAILED"
}
```

---

## 5) 실패 처리 (Failure Handling)

### 5.1 실패 분류 (필수)

외부 은행 연동 실패는 최소 아래 3가지로 분류한다.

#### 1. Transient Failure (재시도 가능)

* 네트워크 타임아웃
* 일시적 5xx 오류

#### 2. Permanent Failure (재시도 불가)

* 계좌 오류
* 한도 초과
* 인증 실패 (잘못된 자격)

#### 3. Unknown / Indeterminate Failure (상태 불명확)

* 요청이 전달되었는지 확신 불가
* 전송 후 타임아웃 발생

---

### 5.2 재시도 전략 (권장)

* 자동 재시도는 **짧고 제한적으로**

    * 예: 최대 3회
    * 지수 백오프 적용
* Indeterminate 상태의 경우:

    * 즉시 무한 재시도 ❌
    * 상태 조회(Query) 또는 운영자 확인 후 재처리
* 모든 재처리는 **Audit Log 필수 기록**

---

### 5.3 Saga 관점의 실패 처리

Banking Service는 **Saga Participant** 역할을 수행한다.

* 실패 시:

    * Money Service에 직접 보상 요청 ❌
    * Payment / Money Orchestrator에 실패 결과 전달 ⭕
* Orchestrator가 보상 트랜잭션 실행 여부 결정

---

### 5.4 중복 이체 방지 (가장 중요)

* `Idempotency Key` + `referenceId` 기반 중복 방지
* 동일 요청 재수신 시:

    * 외부 은행 API 재호출 ❌
    * 기존 `transferId` / `status` 반환 ⭕
* `Unknown` 상태일 경우:

    * 우선 상태 조회
    * 이후 필요 시 재시도

---

## 6) 운영 / 관측성 (필수 최소치)

* 모든 요청에 `traceId` 부여 및 전파 (ADR-031)
* 이체 성공률 / 실패율 / 타임아웃 모니터링 (ADR-014)
* 외부 은행 API 응답 지연 추적
* Graceful Shutdown 시:

    * 처리 중 이체 완료 또는 상태 확정 후 종료 (ADR-006)

---

## 7) 요약 (One-liner)

**Banking Service는
“이체를 한다”가 아니라
외부 은행의 불확실성을 내부 도메인으로부터 격리한다.**

```
```
