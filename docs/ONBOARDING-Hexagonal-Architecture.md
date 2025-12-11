# 아키텍처 온보딩: 헥사고날(포트-어댑터) 아키텍처 가이드

본 문서는 이 저장소에서 헥사고날(Ports & Adapters) 아키텍처가 어떻게 적용되어 있는지, 코드 기준으로 설명하고 신규 개발자가 빠르게 온보딩할 수 있도록 도움을 제공합니다.

## 핵심 개념 요약

- 도메인 중심: 비즈니스 규칙은 `domain`과 `application` 레이어에 위치합니다.
- 포트(Port): 애플리케이션이 외부와 상호작용하는 인터페이스입니다.
  - `application.port.in` = 유스케이스 입력 포트
  - `application.port.out` = 외부 시스템(예: DB)에 대한 출력 포트
- 어댑터(Adapter): 포트의 구현입니다.
  - `adapter.in` = 웹, 메시지 등 외부 진입점 구현
  - `adapter.out` = DB, 외부 API 등 인프라 접근 구현
- 공통 어노테이션/규약: `common` 모듈의 어노테이션으로 각 레이어 역할을 명확히 태깅합니다.

```
┌─────────────── 외부(사용자/시스템) ───────────────┐
│            HTTP/JSON 요청 (Controller)            │
└────────────────────┬──────────────────────────────┘
                     ▼
              adapter.in.web
                     │ (입력 DTO → 도메인)
                     ▼
               application(service)
         (port.in 유스케이스 구현, 비즈 규칙)
                     │ (port.out 호출)
                     ▼
              adapter.out.persistence
          (Repository/JPA 구현, DB 접근)
                     │
                     ▼
                       DB
```

---

## 모듈 및 주요 패키지

- 루트: `C:/project/study/study-architecture-first`
- 공통 모듈: `common`
  - `org.opennuri.study.archtecture.common.PersistanceAdapter`
  - `org.opennuri.study.archtecture.common.WebAdapter`
  - `org.opennuri.study.archtecture.common.UseCase`
  - `org.opennuri.study.archtecture.common.SelfValidating`
    - 유효성 선검증 패턴을 위한 베이스 클래스(필요 시 커맨드/쿼리 DTO에서 상속)

- 멤버십 서비스: `membership-service`
  - 부트 스트랩: `MembershipApplication`
  - 도메인: `membership.domain.Membership`
  - 애플리케이션
    - 입력 포트: `application.port.in.*` (예: `RegisterMembershipUseCase` 등)
    - 출력 포트: `application.port.out.*` (예: `CommandMembershipPort`, `QueryMembershipPort`)
    - 서비스: `application.service.MembershipService` (유스케이스 구현체)
  - 어댑터 인(웹): `adapter.in.web.*`
    - 컨트롤러: `MembershipController`
    - 요청/응답 DTO: `RegisterMembershipRequest`, `UpdateMembershipRequest`, `MembershipResponse`
  - 어댑터 아웃(영속성): `adapter.out.persistance.*`
    - 엔티티: `MembershipEntity`
    - 스프링 데이터: `MembershipRepository`, `MembershipRepositoryCustom`, `MembershipRepositoryImpl`
    - 어댑터: `MembershipPersistenceAdapter` (port.out 구현)

참고: 패키지 경로에 `archtecture`/`persistance`처럼 철자가 독특한 부분이 있으나 코드 전반에서 일관되게 사용되므로 그대로 따릅니다.

---

## 레이어 별 역할과 책임

### 1) Domain (`membership.domain.Membership`)
- 순수한 비즈니스 객체와 규칙을 담습니다.
- 프레임워크 의존도를 최소화합니다.

### 2) Application
- 유스케이스를 인터페이스(Port-in)로 정의하고 서비스에서 구현합니다.
- 외부 자원 접근은 Port-out 인터페이스를 통해서만 수행합니다.

예) Port-out 정의
```
// membership-service/src/main/java/.../application/port/out/CommandMembershipPort.java
public interface CommandMembershipPort {
    Membership crateMembership(Membership membership);
    Membership updateMembership(Membership membership);
    boolean deleteById(String membershipId);
}
```

서비스는 위 Port-out을 주입받아 호출합니다.

### 3) Adapter-in (Web)
- HTTP 요청을 받아 입력 DTO를 도메인/유스케이스 입력 모델로 변환합니다.
- `MembershipController`가 `RegisterMembershipUseCase` 등 Port-in 인터페이스를 호출합니다.

### 4) Adapter-out (Persistence)
- DB 접근 구현체로서 Port-out 인터페이스를 구현합니다.
- 스프링 데이터 JPA/QueryDSL을 통해 실제 저장소 연산을 수행합니다.

예) Port-out 구현
```
@PersistanceAdapter
@RequiredArgsConstructor
public class MembershipPersistenceAdapter implements CommandMembershipPort, QueryMembershipPort {
    private final MembershipRepository membershipRepository;

    @Override
    public Membership crateMembership(Membership membership) {
        MembershipEntity entity = new MembershipEntity();
        entity.setName(membership.getName());
        entity.setEmail(membership.getEmail());
        entity.setAddress(membership.getAddress());
        entity.setCorp(membership.isCorp());
        entity.setValid(membership.isValid());
        entity.setCreatedAt(LocalDateTime.now());
        MembershipEntity saved = membershipRepository.save(entity);
        return Membership.builder()
                .membershipId(String.valueOf(saved.getMembershipId()))
                .name(saved.getName())
                .email(saved.getEmail())
                .address(saved.getAddress())
                .isCorp(saved.isCorp())
                .isValid(saved.isValid())
                .build();
    }
}
```

Repository 레벨에는 세 가지 검색 방법이 제공됩니다.
- JPQL 메서드 `search(...)`
- Example 기반 메서드 `searchByExample(...)`
- QueryDSL 기반 메서드 `searchUsingQuerydsl(...)` (권장)

---

## 도메인 모델의 사용 원칙 (Domain Model vs. Persistence Model)

이 프로젝트는 도메인 모델(`membership.domain.Membership`)과 영속성 모델(JPA `MembershipEntity`)을 명확히 분리합니다. 목적과 책임, 변경 주기가 다른 두 모델을 혼용하지 않기 위함입니다.

- 분리 목적
  - 도메인 모델은 비즈니스 언어와 규칙을 표현합니다. 프레임워크나 저장 기술로부터 독립적이어야 합니다.
  - 영속성 모델은 데이터베이스 매핑, 컬럼 제약, 생성 시각 등 기술적 관심사에 집중합니다.

- 사용 위치와 방향
  - Controller 계층에서는 요청 DTO를 도메인/유스케이스 입력 모델(커맨드/쿼리)로 변환합니다. 엔티티를 직접 반환하거나 의존하지 않습니다.
  - Application Service는 도메인 모델을 생성/변경하고, Port-out을 통해 저장/조회합니다. 서비스는 절대 엔티티 API에 의존하지 않습니다.
  - Adapter-out(persistence)은 Port-out 구현체에서만 엔티티를 다룹니다. 도메인↔엔티티 변환을 담당합니다.

- 매핑 규칙(권장)
  - 도메인 → 엔티티: Adapter-out에서만 수행. 생성 시간(`createdAt`) 등 인프라 필드는 어댑터에서 책임집니다.
  - 엔티티 → 도메인: 저장/조회 결과를 도메인으로 복원하여 상위 레이어로 반환합니다.
  - 도구 선택: 현재는 수동 매핑을 사용합니다. 필요 시 MapStruct 등을 사용할 수 있으나 도메인 의존성 오염이 없도록 Adapter에 한정합니다.

- 도메인 모델 작성 팁
  - 불변 값 선호: 가능하면 `builder()` 또는 생성자를 통해 완전한 상태로 생성하고, 무효 상태가 되지 않도록 합니다.
  - 불변식(Invariants) 검증: 생성 시점 또는 애플리케이션 서비스에서 `SelfValidating` 패턴을 활용해 유효성 검사를 수행합니다.
  - ID 표현: 현재 도메인에서는 `String membershipId`를 사용합니다. 인프라 ID 타입(Long)과 분리되어 있어 어댑터에서 변환합니다.

도메인 모델과 엔티티를 뒤섞으면 테스트가 어려워지고, 기술 교체 비용이 커집니다. 반드시 레이어 별 사용 원칙을 지켜 주세요.

---

### DTO vs Domain Model: 역할과 분리 이유

웹 계층에서 사용하는 DTO와 도메인 모델은 목적과 수명주기가 다릅니다. 이 프로젝트에서는 다음과 같이 구분합니다.

- 웹 DTO (Controller I/O 전용)
  - 입력 DTO: `RegisterMembershipRequest`, `UpdateMembershipRequest`
  - 출력 DTO: `MembershipResponse`
  - 특징/역할
    - HTTP I/O 스키마를 표현합니다(필드명, 직렬화 규칙, swagger 문서 등).
    - `@Valid` 등 입력 유효성 검증 어노테이션을 포함할 수 있습니다.
    - 비즈니스 로직은 금지합니다. 단, 아주 가벼운 정규화(공백 트림 등)만 허용합니다.
    - 변경 주기: 외부 계약(API 버전/프론트 요구) 변화에 민감합니다.

- 유스케이스 입력 모델(애플리케이션 계층 DTO)
  - 예: `RegisterMembershipCommand`, `UpdateMembershipCommand`, `SearchMembershipQuery`
  - 특징/역할
    - 유스케이스가 필요로 하는 데이터와 의도를 명확히 캡슐화합니다.
    - 컨트롤러에서 웹 DTO를 이 모델로 변환하여 서비스에 전달합니다.
    - 컨트롤러/웹과 도메인의 결합을 낮추는 완충재 역할을 합니다.

- 도메인 모델
  - 예: `membership.domain.Membership`
  - 특징/역할
    - 비즈니스 언어와 규칙(불변식)을 표현합니다.
    - 프레임워크와 I/O 스키마로부터 독립적이어야 합니다.
    - 생성 시점의 완전성 보장과 상태 불변(가능하면)을 선호합니다.

- 영속성 엔티티
  - 예: `adapter.out.persistance.MembershipEntity`
  - 특징/역할
    - DB 매핑, 컬럼 제약, 생성시각 등 기술적 관심사 담당.
    - 외부 노출 금지. 컨트롤러/도메인에 스며들지 않도록 합니다.

왜 분리해야 하나요?

- 안정성/유연성
  - API 필드가 바뀌어도 도메인 모델은 그대로일 수 있고, 반대로 도메인 규칙이 바뀌어도 외부 계약은 유지될 수 있습니다.
- 테스트 용이성
  - 도메인 모델을 프레임워크/웹 의존 없이 순수 단위 테스트로 검증할 수 있습니다.
- 보안/경계 명확화
  - 엔티티를 반환하지 않음으로써 내부 스키마/제약(예: `createdAt`, 내부 ID 등)이 외부로 새는 것을 막습니다.
- 진화 비용 절감
  - 웹/도메인/영속성 각 축을 독립적으로 버전업/교체할 수 있습니다(MapStruct 도입, 응답 스키마 변경 등).

권장 매핑 위치와 흐름

1) Controller
- RequestDTO → Command/Query → 도메인 생성에 필요한 데이터만 전달
- 도메인/유스케이스 결과 → ResponseDTO로 변환

2) Application Service
- Command/Query를 사용해 도메인 모델을 생성/수정하고, Port-out 호출

3) Adapter-out (Persistence)
- 도메인 ↔ 엔티티 변환을 전담. 도메인에는 JPA 어노테이션을 절대 추가하지 않습니다.

간단 예시

```java
// Controller (요약)
@PostMapping("/membership")
public ResponseEntity<MembershipResponse> create(@Valid @RequestBody RegisterMembershipRequest req) {
  RegisterMembershipCommand cmd = RegisterMembershipCommand.builder()
      .name(req.getName())
      .email(req.getEmail())
      .address(req.getAddress())
      .isCorp(req.isCorp())
      .build();
  Membership created = registerMembershipUseCase.registerMembership(cmd);
  return ResponseEntity.status(201)
      .header("Location", "/membership/" + created.getMembershipId())
      .body(MembershipResponse.from(created));
}

// Adapter-out (요약)
public Membership crateMembership(Membership membership) {
  MembershipEntity entity = MembershipEntity.builder()
      .name(membership.getName())
      .email(membership.getEmail())
      .address(membership.getAddress())
      .isCorp(membership.isCorp())
      .isValid(membership.isValid())
      .createdAt(LocalDateTime.now())
      .build();
  MembershipEntity saved = membershipRepository.save(entity);
  return Membership.builder()
      .membershipId(String.valueOf(saved.getMembershipId()))
      .name(saved.getName())
      .email(saved.getEmail())
      .address(saved.getAddress())
      .isCorp(saved.isCorp())
      .isValid(saved.isValid())
      .build();
}
```

지양해야 할 안티패턴

- 엔티티를 컨트롤러에서 직접 반환하거나 요청 바인딩에 사용
- 도메인 모델에 JPA/웹 어노테이션을 추가하여 계층 경계를 흐림
- DTO에 비즈니스 규칙/계산 로직을 넣어 테스트와 재사용성을 떨어뜨림
- 서비스가 엔티티에 직접 의존(포트 우회)하여 인프라 결합을 키움

이 프로젝트에서는 위 원칙을 기본 규약으로 삼습니다. 새 기능에서도 동일하게 적용해 주세요.

---

## 읽기/쓰기 서비스 분리 (경량 CQRS)

애플리케이션 서비스는 쓰기(Command)와 읽기(Query)를 분리하여 유지보수성과 트랜잭션 경계를 명확히 합니다.

- 구현 위치와 시그니처
  - `MembershipCommandService` (쓰기 전용)
    - 구현 인터페이스: `RegisterMembershipUseCase`, `UpdateMembershipUseCase`, `DeleteMembershipUseCase`
    - 의존 포트: `CommandMembershipPort`
    - 트랜잭션: `@Transactional` (기본 read/write)
  - `MembershipQueryService` (읽기 전용)
    - 구현 인터페이스: `GetMembershipByIdUseCase`, `SearchMembershipUseCase`
    - 의존 포트: `QueryMembershipPort`
    - 트랜잭션: `@Transactional(readOnly = true)`

- 코드 스니펫 (요약)
```
@UseCase
@Transactional
class MembershipCommandService implements RegisterMembershipUseCase, ... {
  private final CommandMembershipPort commandPort;
  public Membership registerMembership(RegisterMembershipCommand command) { ... }
}

@UseCase
@Transactional(readOnly = true)
class MembershipQueryService implements GetMembershipByIdUseCase, ... {
  private final QueryMembershipPort queryPort;
  public Optional<Membership> getMembershipById(String id) { ... }
}
```

- 컨트롤러 연계
  - 작성/수정/삭제 HTTP 엔드포인트는 Command 유스케이스 빈을 주입받아 호출합니다.
  - 조회 HTTP 엔드포인트는 Query 유스케이스 빈을 주입받아 호출합니다.

- 장점
  - 읽기 트래픽 최적화, 쓰기 로직의 트랜잭션 경계 분리, 책임 명확화.
  - 필요 시 읽기 모델 확장(QueryDSL/전용 Projection) 등으로 독립 진화 가능.

- 팀 규약
  - 새로운 유스케이스 추가 시, 데이터 변경이 있으면 Command 서비스에, 순수 조회면 Query 서비스에 구현합니다.
  - 출력 포트도 `CommandMembershipPort`/`QueryMembershipPort`로 구분하여 의존을 최소화합니다.

---

## 요청 흐름(HTTP → 도메인 → DB)

1. `MembershipController`가 HTTP 요청(JSON)을 수신합니다.
2. 요청 DTO(`RegisterMembershipRequest`, `UpdateMembershipRequest`)를 도메인/유스케이스 입력 모델로 변환합니다.
3. 컨트롤러는 Port-in(예: `RegisterMembershipUseCase`)을 호출합니다.
4. `MembershipService`가 유스케이스 로직을 수행하고 Port-out(예: `CommandMembershipPort`)을 호출합니다.
5. `MembershipPersistenceAdapter`가 Repository/JPA를 통해 DB에 접근합니다.
6. 결과를 도메인 객체로 반환해 컨트롤러에서 응답 DTO로 변환 후 전송합니다.

---

## 테스트 작성 가이드라인과 커버리지 기준

이 프로젝트는 헥사고날 아키텍처의 경계를 테스트에서도 명확히 지키는 것을 목표로 합니다. 아래 가이드는 현재 저장소의 테스트 패턴(@DataJpaTest, MockMvc, Mockito, AssertJ 등)에 맞춰 작성되었습니다.

### 1) 테스트 피라미드와 범위
- 단위(Unit) 테스트 우선: 도메인, 애플리케이션 서비스는 스프링 컨테이너 없이 순수 단위 테스트를 선호합니다.
- 슬라이스(Slice) 테스트: 영속성(@DataJpaTest), 웹(@WebMvcTest)처럼 필요한 슬라이스만 로딩합니다.
- 통합(Integration) 테스트: 꼭 필요한 경우에만 `@SpringBootTest` 사용을 검토합니다.

### 2) 레이어별 권장 테스트 종류
- Domain (`membership.domain.*`)
  - 순수 단위 테스트. 규칙/불변식, 경계값과 예외 상황 포함.
- Application (`application.service.*`)
  - 포트(Adapters)를 Mockito로 목킹하여 유스케이스 로직만 검증.
  - 예: Command 서비스는 `CommandMembershipPort`를 mock, Query 서비스는 `QueryMembershipPort`를 mock.
- Adapter-out (Persistence)
  - `@DataJpaTest`로 엔티티 매핑, JPQL/Example/QueryDSL 쿼리 동작 검증.
  - 현재 저장소 예시: `MembershipRepositoryTest`에 JPQL/Example/QueryDSL 각 케이스 테스트 존재.
- Adapter-in (Web)
  - `@WebMvcTest(MembershipController.class)` + MockMvc로 컨트롤러 레이어만 검증.
  - Port-in 유스케이스 인터페이스를 `@MockBean`으로 주입하여 입출력/상태코드 매핑 확인.

### 3) 작성 규칙(코딩 스타일)
- 네이밍
  - 테스트 클래스: 대상 + `Test` (예: `MembershipCommandServiceTest`).
  - 테스트 메서드: `should_...` 또는 `given_..._when_..._then_...` 형식 권장.
- 구조
  - AAA 패턴(Arrange-Act-Assert) 유지, 불필요한 로깅/표준출력 지양.
  - AssertJ 사용: 가독성 높은 검증(`assertThat(...)`).
- 픽스처
  - 중복 데이터 생성 로직은 private 헬퍼(예: `saveMember(...)`, `aCommand()`)로 추출.
  - 시간/UUID 등 비결정 값은 가능하면 주입(Clock)하거나 고정값을 사용해 결정적 테스트 유지.
- 예외/에러 경로
  - 정상 흐름과 함께 필수 에러 경로(404, 400, 유효성 실패, 존재하지 않는 ID 등)도 반드시 포함.

### 4) 커버리지 목표(권장)
- 전체 라인 커버리지: 최소 80%.
- 레이어별 기준(권장치)
  - Domain: 95%+ (비즈니스 규칙 핵심)
  - Application(Service): 90%+ (분기/예외 포함)
  - Persistence(Adapter-out, Repository): 85%+ (주요 쿼리 브랜치 포함)
  - Web(Adapter-in, Controller): 80%+ (상태코드 매핑/검증 오류 경로 포함)
- 기능 추가 시 최소 요구사항
  - 새로운 유스케이스: 도메인/서비스 단위 테스트 필수, 관련 포트 호출 분기/에러 경로 포함
  - 새로운 엔드포인트: 컨트롤러 슬라이스 테스트로 2xx/4xx/404 경로 검증
  - 저장소/쿼리 변경: `@DataJpaTest`로 정상/경계/대소문자/부분일치/불리언 조합 등 대표 케이스 검증

### 5) 현재 저장소 테스트와 정렬(alignment)
- `MembershipRepositoryTest`
  - JPQL/Example/QueryDSL 3가지 검색 구현을 각각 검증합니다(부분일치, 대소문자 무시, 불리언 필터 등).
- 서비스/컨트롤러 테스트 파일이 존재하며, 포트/DTO 변환 및 상태코드 매핑 관점의 검증을 권장합니다.

### 6) 실행 방법
- 루트 또는 모듈에서 Gradle 실행
  - Windows: `gradlew.bat test`
  - macOS/Linux: `./gradlew test`
  - 테스트 로깅은 `membership-service/build.gradle`의 `test { testLogging { ... } }` 설정을 따릅니다.

### 7) 커버리지 측정(선택)
프로젝트에 JaCoCo를 추가해 커버리지를 수치화할 수 있습니다. 빌드 스크립트 변경이 필요한 작업이므로 팀 합의 후 적용하세요.

예시(참고용): `membership-service/build.gradle`에 추가
```groovy
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = '0.8.12'
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}
```

CI 기준선 예시(팀 합의 후 적용):
```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80
            }
        }
    }
}
test.finalizedBy jacocoTestCoverageVerification
```

### 8) MR 체크리스트(요약)
- [ ] 새/변경 유스케이스의 단위 테스트가 존재하고 실패/경계 케이스 포함
- [ ] 컨트롤러 엔드포인트의 상태코드/검증 오류 테스트 포함(MockMvc)
- [ ] 저장소/쿼리 로직 변경 시 `@DataJpaTest` 케이스 추가
- [ ] 커버리지 리포트(선택)를 확인하여 기준 충족
- [ ] 테스트 데이터/픽스처가 중복되지 않고 결정적으로 동작

위 가이드를 기본 규약으로 삼아 테스트를 작성해 주세요. 헥사고날 아키텍처의 경계를 기준으로 “무엇을 어디에서 검증할지”를 먼저 결정하는 습관을 권장합니다.

## 컨트롤러 CRUD 매핑 가이드 (MembershipController)

이 프로젝트의 웹 어댑터(`adapter.in.web`)는 REST 스타일의 CRUD 엔드포인트를 제공합니다. 컨트롤러는 Port-in 유스케이스를 호출하며, DTO 변환과 HTTP 상태 코드 매핑만 담당합니다. 아래는 `MembershipController`의 실제 매핑 규약과 예시입니다.

### 공통 규약
- 베이스 경로: `/membership`
- 미디어 타입: `application/json`
- 식별자 타입: `String id` (내부적으로 Persistence 어댑터에서 `Long`으로 변환)
- DTO
  - 요청: `RegisterMembershipRequest`, `UpdateMembershipRequest`
  - 응답: `MembershipResponse`
- 검증: 요청 DTO는 `@Valid` 사용. 필수 필드 누락 시 400 Bad Request

### 1) 생성 Create
- 메서드/경로: `POST /membership`
- 요청 바디 예시
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "address": "Seoul",
  "corp": false
}
```
- 처리 흐름: `RegisterMembershipRequest` → `RegisterMembershipCommand` → `registerMembershipUseCase.registerMembership`
- 응답: 201 Created, 바디는 `MembershipResponse`, `Location` 헤더에 `/membership/{id}` 설정
- cURL
```bash
curl -i -X POST http://localhost:8080/membership \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","address":"Seoul","corp":false}'
```

### 2) 단건 조회 Read by ID
- 메서드/경로: `GET /membership/{id}`
- 처리 흐름: `getMembershipByIdUseCase.getMembershipById(id)`
- 응답
  - 성공: 200 OK + `MembershipResponse`
  - 미존재: 404 Not Found (바디 없음)
- cURL
```bash
curl -i http://localhost:8080/membership/1
```

### 3) 검색 Search (Query)
- 메서드/경로: `GET /membership`
- 쿼리 파라미터(모두 선택): `name`, `email`, `address`, `isCorp`, `isValid`
- 처리 흐름: 쿼리 파라미터 → `SearchMembershipQuery` → `searchMembershipUseCase.searchMemberships`
- 응답: 200 OK + `MembershipResponse[]`
- 주의
  - 부분 일치/대소문자 무시는 저장소 구현에 위임됩니다(기본: QueryDSL containsIgnoreCase)
  - 페이징이 필요하면 Query 객체/리포지토리 확장을 통해 `page`, `size`, `sort`를 도입하세요
- cURL
```bash
curl -i "http://localhost:8080/membership?name=ali&isValid=true"
```

### 4) 수정 Update
- 메서드/경로: `PUT /membership/{id}`
- 요청 바디 예시
```json
{
  "name": "Alice Kim",
  "email": "alice.kim@example.com",
  "address": "Busan",
  "corp": true
}
```
- 처리 흐름: `UpdateMembershipRequest` + `{id}` → `UpdateMembershipCommand` → `updateMembershipUseCase.updateMembership`
- 응답
  - 성공: 200 OK + `MembershipResponse`
  - 미존재(도메인/어댑터에서 `IllegalArgumentException` 발생 시): 404 Not Found
- cURL
```bash
curl -i -X PUT http://localhost:8080/membership/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Kim","email":"alice.kim@example.com","address":"Busan","corp":true}'
```

### 5) 삭제 Delete
- 메서드/경로: `DELETE /membership/{id}`
- 처리 흐름: `deleteMembershipUseCase.deleteMembership(id)` → boolean 반환
- 응답
  - 성공: 204 No Content
  - 미존재: 404 Not Found
- cURL
```bash
curl -i -X DELETE http://localhost:8080/membership/1
```

### 에러 및 상태 코드 요약
- 200 OK: 조회/수정 성공
- 201 Created: 생성 성공, `Location` 헤더 포함
- 204 No Content: 삭제 성공
- 400 Bad Request: 유효성 검증 실패(요청 DTO `@Valid`)
- 404 Not Found: 리소스 미존재

### 컨트롤러 계층 팁
- 컨트롤러는 비즈니스 규칙을 포함하지 않습니다. DTO 변환/상태 코드/헤더 설정에 집중하세요.
- 예외를 공통 처리하려면 `@ControllerAdvice`로 400/404/409 등의 매핑 정책을 중앙화할 수 있습니다.
- 응답 스키마 안정성을 위해 `MembershipResponse`를 지속 사용하고, 엔티티/도메인 직접 노출을 피합니다.

---

## OpenAPI(Swagger UI) 접속 경로 및 테스트 방법

이 프로젝트는 springdoc-openapi를 사용하여 자동으로 OpenAPI 문서와 Swagger UI를 제공합니다. 의존성은 `membership-service/build.gradle`에 이미 포함되어 있습니다.

- UI 주소
  - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- 스펙 문서
  - OpenAPI JSON: `http://localhost:8080/v3/api-docs`
  - OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

### 실행 방법 (로컬)
1. Gradle로 애플리케이션 실행
   - IDE에서 `MembershipApplication` 실행 또는 터미널에서:
   ```bash
   ./gradlew :membership-service:bootRun
   ```
   Windows PowerShell:
   ```powershell
   .\gradlew.bat :membership-service:bootRun
   ```
2. 브라우저에서 Swagger UI 접속: `http://localhost:8080/swagger-ui/index.html`
3. 좌측 상단에서 엔드포인트 목록을 펼쳐 각 API를 Try it out → Execute로 바로 호출/검증합니다.

### Swagger UI로 CRUD 테스트 예시
- Create: `POST /membership`
  - Request body 예시를 입력하고 Execute → 응답 코드 201과 Location 헤더를 확인합니다.
- Read: `GET /membership/{id}`
  - Path parameter에 생성된 `id`를 넣고 Execute → 200 또는 404 확인.
- Search: `GET /membership`
  - `name`, `isValid` 등 쿼리 파라미터를 추가해 Execute → 200과 리스트 확인.
- Update: `PUT /membership/{id}`
  - Path `id`와 Request body를 입력 후 Execute → 200 확인.
- Delete: `DELETE /membership/{id}`
  - Execute → 204 또는 404 확인.

### cURL 기반 빠른 테스트
이미 본 문서의 "컨트롤러 CRUD 매핑 가이드" 섹션에 각 엔드포인트별 cURL 예제가 포함되어 있습니다. 서버가 실행 중인 상태에서 터미널에서 그대로 실행하면 됩니다.

### Postman / Insomnia 사용 팁
- OpenAPI 스펙을 이용하여 컬렉션을 자동 생성할 수 있습니다.
  - Import URL로 `http://localhost:8080/v3/api-docs.yaml`을 입력하면 엔드포인트가 자동 등록됩니다.
  - 환경 변수로 `baseUrl = http://localhost:8080`을 설정해 두면 편리합니다.

### 주의 사항
- 기본 포트는 `8080`입니다. 포트를 변경했다면 위 URL의 포트도 함께 바꾸세요.
- 보안(인증/인가)이 추가되면 Swagger UI 상단의 Authorize 버튼으로 토큰을 설정한 뒤 테스트하십시오.

---

## 코드 가이드라인

- Port와 Adapter의 분리: 새 기능을 추가할 때 항상 Port(인터페이스)를 먼저 정의하고 Adapter(구현)를 나중에 작성합니다.
- DTO 변환: Controller에는 DTO↔도메인 변환만, 비즈니스 규칙은 Application/Domain에 위치시킵니다.
- 트랜잭션 경계: 일반적으로 Application Service에서 시작(예: `@Transactional`)하고 Adapter는 단순 위임에 집중합니다.
- 테스트: Repository 레벨은 `@DataJpaTest`, 서비스/컨트롤러는 슬라이스/통합 테스트를 적절히 사용합니다.

주의사항 및 팀 규약
- 현재 `MembershipEntity`는 Lombok `@Builder`가 활성화되어 있으며, 일부 테스트와 리포지토리 코드가 빌더 API에 의존합니다.
- 출력 포트의 `crateMembership` 메서드명은 오탈자처럼 보이지만, 인터페이스-구현-호출부가 일관되게 사용하므로 리팩토링 시 전역 일괄 변경이 필요합니다.
- 검색 표준은 QueryDSL 기반 `searchUsingQuerydsl` 사용을 권장합니다.

---

## 새 유스케이스 추가 예시

가정: "멤버 상태 토글" 유스케이스 추가

1) Port-in 정의
```
public interface ToggleMembershipValidityUseCase {
    Membership toggleValidity(String membershipId);
}
```

2) Port-out 확장(필요 시)
```
public interface CommandMembershipPort {
    // ... 기존
    Membership updateMembership(Membership membership);
}
```

3) Application Service 구현(`@UseCase` 또는 스프링 컴포넌트 애노테이션)
```
@UseCase
@RequiredArgsConstructor
public class MembershipService implements ToggleMembershipValidityUseCase { // 기존 서비스에 병합 가능
    private final QueryMembershipPort queryPort;
    private final CommandMembershipPort commandPort;

    @Override
    public Membership toggleValidity(String membershipId) {
        Membership current = queryPort.findById(membershipId)
            .orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        Membership updated = current.toBuilder().isValid(!current.isValid()).build();
        return commandPort.updateMembership(updated);
    }
}
```

4) Adapter-in(Controller) 엔드포인트 추가 → DTO 변환/검증 → Port-in 호출

5) 테스트 작성
- 서비스 단위 테스트(포트 mock)
- 컨트롤러 슬라이스 테스트(WebMvcTest)
- 필요한 경우 통합 테스트

---

## 개발/빌드/실행 방법

- JDK/Gradle: 루트 `gradlew` 스크립트 사용
- 빌드
```
./gradlew build
```

- 단일 모듈 빌드
```
./gradlew :membership-service:build
```

- 애플리케이션 실행(멤버십 서비스)
```
./gradlew :membership-service:bootRun
```

- 테스트 실행 예시
```
./gradlew :membership-service:test
```

로컬 설정 팁
- JPA 설정은 테스트에서 `create-drop`로 지정되어 있습니다. 운영/개발 프로필과 분리하여 사용하세요.
- QueryDSL 기반 검색 구현체(`MembershipRepositoryImpl`)가 사용됩니다. 새로운 검색 조건을 추가할 때는 QueryDSL 구현을 우선 검토하세요.

---

## 자주 하는 질문(FAQ)

Q1. 왜 컨트롤러에서 바로 JPA를 호출하지 않나요?
- A. 유스케이스 중심 설계를 위해 애플리케이션 서비스가 비즈니스 규칙을 담당하고, 저장소 접근은 Port-out을 통해 간접 호출합니다. 이는 테스트 용이성과 기술 독립성을 높입니다.

Q2. 엔티티와 도메인 모델이 분리된 이유는?
- A. 영속성 모델(JPA 엔티티)과 도메인 모델(비즈니스 객체)의 변경 주기가 다르고, 외부 기술에 대한 의존을 도메인에서 차단하기 위함입니다.

Q3. `crateMembership` 오탈자를 바꿔도 되나요?
- A. 가능합니다. 단, Port/Adapter/Service/테스트 전역에서 함께 변경해야 하며 대규모 리팩토링이므로 별도 MR로 진행하세요.

---

## 마무리

이 프로젝트는 헥사고날 아키텍처의 원칙을 따르며, 포트-어댑터 분리를 통해 유연한 변경과 테스트 용이성을 목표로 합니다. 새로운 기능을 추가할 때는 "Port 먼저, Adapter 나중" 규칙과 레이어의 책임 분리를 지켜주세요.
