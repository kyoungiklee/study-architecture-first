## 형상관리 시뮬레이션 방향
* 회사의 솔루션 개발 및 형상관리 현행을 반영하여 시뮬레이션한다.
* 개선사항들을 확인하고 해당 내용 반영에 문제없음을 확인한다.
* Git Flow 시뮬레이션용 프로젝트는 미완성 프로젝트이다. 
* 프로젝트 완성 후 v5, v4 릴리즈를 생성한다. 
* 차기버전을 위한 v6 릴리즈를 생성한다. 
* 이후 각 릴리즈의 기능, 버그, 문서, 리픽토링 등의 동작을 수행하며 형상관리를 시뮬레이션 한다.

---

## 1. 현재 상황 진단 및 적합한 Git Flow 제안

### 1-1. 현재 상태 정리

* 회사 규모: 소규모 / 인원 제한 → 브랜치 구조가 너무 복잡하면 유지 불가
* 제품:

    * v5, v4 두 라인 병행 유지
    * 각 릴리즈에서 마이너 버전 업 커밋이 약 20개 이상 유지되고 있음
* 운영 방식:

    * `v5`, `v4` 릴리즈 브랜치에서 고객 수정사항을 직접 반영
* `master` 브랜치:

    * 2017년 이후 변경 없음 (사실상 “역사 보관용 스냅샷”)

→ 결론:
지금 구조는 “유지보수 중심”으로는 동작하고 있지만,

* 브랜치 역할이 명확히 정의되어 있지 않고
* 릴리즈/태그 정책이 없어서 “어느 커밋이 어느 고객사에 배포된 버전인지” 추적하기 어려운 위험이 있습니다.

---

### 1-2. 목표로 하는 Git Flow 방향

소규모 조직 + 유지보수 중심이라는 전제에서 “풀 GitFlow( master + develop + release + hotfix )”는 과합니다.
아래와 같은 **경량 Git Flow**로 진행한다.

#### [브랜치 종류 정의]

1. `main` — v6 (JDK 21 / Spring Boot 3)

    * “최신 정식 출시 버전” 브랜치로, 신규 사이트는 이 버전으로 구축
    * 릴리즈 태그만 생성(예: `v6.x.y`), 다른 브랜치로의 병합 대상은 아님
    * 차기 메이저 라인의 기준이며, 운영 유지보수 라인(`support/*`)과는 직접 병합하지 않음(필요 시 cherry-pick만)

2. `develop` — v6 (JDK 21 / Spring Boot 3)

    * 차세대 솔루션(v6) 개발 통합 브랜치
    * 기능 개발(`feature/*`)·버그 수정(`bugfix/*`) 브랜치는 기본적으로 `develop`으로 MR 생성(Squash 권장)
    * 충분한 QA를 거친 배포 시점에 `develop` → `main`으로 병합(필요 시 릴리즈 히스토리 보존을 위해 Merge commit 유지), 태그는 `main`에서 생성

3. `support/v5` — v5 (JDK 11 / Spring Boot 2)

    * 기존 40개 사이트 중 대다수를 위한 v5 유지보수 트랙(운영 라인)
    * 자체 배포 라인으로 태그는 `v5.x.x`를 사용하며(`support/v5`에서 태깅), 신규 기능은 허용하지 않음
    * 허용 변경: 긴급/일반 버그 수정, 문서(`docs/*`), 비기능 정리(`chore/*`)
    * `main`/`develop`과 직접 병합하지 않으며, 필요한 경우 cherry-pick으로만 선택 반영

4. `support/v4`

    * v4 (JDK 8 / Legacy) 제품 라인의 공식 유지보수 브랜치(수명 종료 시까지 유지)
    * 자체 배포 라인으로 태그는 `v4.x.x`
    * `hotfix-v4/*`에서 수정 후 `support/v4`로 MR, 다른 라인과 직접 병합하지 않음(필요 시 cherry-pick)

5. 작업 브랜치 (feature / bugfix / hotfix)

    * 네이밍 예시:

        * 기능 개발: `feature/v5-<이슈키>-<요약>`
          예) `feature/v5-IQ-123-add-scheduler-option`
        * 버그 수정: `bugfix/v5-<이슈키>-<요약>`
        * v6 개발 관련: `feature/<요약>`, `bugfix/<요약>` → 대상: `develop`
        * v5 운영 긴급 수정: `hotfix-v5/<요약>` 또는 `hotfix-v5/<이슈키>-<요약>` → 대상: `support/v5`
        * v4 운영 긴급 수정: `hotfix-v4/<요약>` 또는 `hotfix-v4/<이슈키>-<요약>`

    * 기본 원칙:
        * `feature/*`, `bugfix/*`는 `develop`으로 MR 생성(Squash 권장)
        * `hotfix-v5/*`는 `support/v5`로 MR 생성(운영상 필요한 경우에 한해 `develop`으로 cherry-pick 고려)
        * `hotfix-v4/*`는 `support/v4`로 MR 생성

#### [브랜치 간 병합 규칙 (MR 기반)]

안정적인 운영을 위해 브랜치 간 병합은 반드시 Merge Request(MR)로만 수행하며, 다음 규칙을 따른다.

- 출발지: `feature/*` → 도착지: `develop` (v6)
  - 조건/시점: 기능 개발 완료 및 단위 테스트 통과 시
  - 병합 방식: Squash Merge 권장(히스토리 단순화)

- 출발지: `bugfix/*` → 도착지: `develop` (v6)
  - 조건/시점: 버그 수정 완료 시
  - 병합 방식: Squash Merge 권장

- 출발지: `develop` → 도착지: `main` (v6 릴리즈)
  - 조건/시점: v6 정식 배포 시점(QA 완료)
  - 병합 방식: 필요한 경우 Merge commit 보존(릴리즈 히스토리 보존). 태그는 `main`에 `v6.x.y`로 생성

- 출발지: `hotfix-v5/*` → 도착지: `support/v5`
  - 조건/시점: v5 운영 중 긴급 장애/패치 필요 시
  - 비고: v6 라인에 동일 이슈가 존재하면 cherry-pick으로 `develop`에 선별 반영 가능

- 출발지: `hotfix-v4/*` → 도착지: `support/v4`
  - 조건/시점: v4 운영 중 긴급 장애 발생 시

- 출발지: `main` ↔ 운영 라인(`support/v5`, `support/v4`)
  - 규칙: 상호 병합 금지. 메이저 개발 라인과 운영 유지보수 라인은 직접 병합하지 않음
  - 예외: 필요한 커밋에 한해 cherry-pick으로만 선별 반영
  - 태깅 위치: v6 라인은 `main`에 `v6.x.y`, v5 라인은 `support/v5`에 `v5.x.x`, v4 라인은 `support/v4`에 `v4.x.x`

---

#### 참고: 현재 고객 운영 현황 반영 메모

* 고객은 v4, v5 두 버전을 동시에 사용 중임
* 이에 따라 유지보수 라인은 다음 두 축으로 병행 운영함
  - v5: `support/v5` (상시 유지, v5.x.x 태깅, hotfix-v5 수용)
  - v4: `support/v4` (상시 유지, v4.x.x 태깅, hotfix-v4 수용)
* v6 개발 라인은 `develop`(개발)→`main`(정식 릴리즈, 태깅 `v6.x.y`) 흐름으로 운영
* `main`과 `support/*` 라인 간 직접 병합은 금지(필요 시 cherry-pick)

6. 비기능(Non-Functional) 변경 브랜치: `chore/…` (문서/리팩터링 제외)

    * “기능 동작에 영향을 주지 않는” 변경에 사용
      (예: 코드 포맷팅, 빌드/CI 설정, 의존성 정리 등)
    * 네이밍 규칙(예시, v5 라인 기준):
        * 포맷팅: `chore/v5-format-<이슈키>-<요약>`
          예) `chore/v5-IQ-311-format-spotless-apply`
        * 그 외(빌드/CI/의존성): `chore/v5-build-…`, `chore/v5-ci-…`, `chore/v5-deps-…`
    * 주의: 리팩토링이라도 “기능 변경”이 포함되면 `feature/` 또는 `bugfix/`를 사용

6. 문서(Documentation) 변경 브랜치: `docs/…`

    * 문서 관련 모든 작업은 전용 접두어 `docs/`를 사용
    * 네이밍 규칙(예시, v5 라인 기준):
        * 문서 수정: `docs/v5-<이슈키>-<요약>`
          예) `docs/v5-IQ-310-update-onboarding`

7. 리팩터링(기능 변화 없음) 브랜치: `refactor/…`

    * “행위(기능) 변화가 없는” 순수 리팩터링 작업에 사용
    * 네이밍 규칙: `refactor/<요약>`
      예) `refactor-extract-service-layer`, `refactor-rename-adapter`
    * 주의: 리팩터링 중 동작 변경이 포함되면 `feature/` 또는 `bugfix/`를 사용

8. 필요시 “고객별 브랜치” (최소화 권장)

    * 공통 패치로 해결이 어려운 수준의 커스터마이징이 있는 경우에만 사용
    * 예시: `customer/<고객코드>/v5`
      예) `customer/CUSTA/v5`
    * 이 경우에도, 공통으로 쓸 수 있는 버그 픽스는
      `support/v5` → `customer/CUSTA/v5`로 머지하는 것을 원칙으로 함
      (고객 브랜치를 “포크”가 아니라 “파생 브랜치”처럼 관리)

---

### 1-3. 비기능(Non-Functional) 변경 브랜치 규칙과 예시

비기능 변경은 제품 기능의 외형/행위를 바꾸지 않는 변경을 의미한다. 문서 작업은 `docs/` 접두어를, 그 외 비기능 변경은 `chore/` 접두어를 사용한다.

권장 규칙

* 브랜치 접두어: `docs/`(문서), `chore/`(그 외 비기능)
* 라인 구분: 현재 유지보수 라인에 맞춰 `v5` 또는 `v4`를 포함
* 카테고리: `docs`, `format`, `refactor`, `build`, `ci`, `deps` 등 세분화하여 의도를 명확히 함

네이밍 예시

* 문서 수정: `docs/v5-<이슈키>-<요약>`
  - 예) `docs/v5-IQ-401-update-api-guide`
* 코드 포맷팅: `chore/v5-format-<이슈키>-<요약>`
  - 예) `chore/v5-IQ-402-format-apply-spotless`
* 순수 리팩터링(행위 동일): `refactor/<요약>`
  - 예) `refactor-rename-adapter`, `refactor-extract-service`
* CI 설정 변경: `chore/v5-ci-<이슈키>-<요약>`
  - 예) `chore/v5-IQ-404-ci-cache-gradle-wrapper`
* 빌드/의존성: `chore/v5-build-…`, `chore/v5-deps-…`

커밋 메시지 가이드(예)

* `docs: ONBOARDING 업데이트 – 비기능 브랜치 규칙 추가`
* `chore(format): spotlessApply 실행 – 코드 스타일 정렬`
* `refactor: MembershipService 의존성 주입 단순화 (동작 동일)`

머지 대상

* 유지보수 라인에 맞춰 작업: v5는 `support/v5`, v4는 `support/v4`로 MR 생성
* 공통 변경(문서/포맷팅)은 가급적 최신 유지보수 라인에 정리한 뒤 필요 시 다른 라인으로 체리픽

### 1-4. master 브랜치 처리 방안

현재 `master`는 2017년 이후 변경 없음.

선택지:

1. **그대로 두고 “역사 보관용(legacy)”으로 명시**

    * 예: README에

      > `master` 브랜치는 2017년 기준의 레거시 코드이며, 현재는 사용하지 않습니다.
    * 장점: 과거 빌드/스크립트가 master를 기준으로 잡고 있어도 깨지지 않음

2. **브랜치 이름 변경 (예: `legacy/master-2017`) 후, 새 `main` 생성**

    * 장점: 새로 합류한 개발자는 `main`, `support/v5`, `support/v4`만 보면 됨
    * 단점: 기존 스크립트·CI 설정 등 수정 필요

→ 소규모 조직 + 기존 자동화가 이미 얽혀 있을 가능성을 고려하면
**1번(그대로 두되 명시적으로 “legacy”라고 문서화)**를 추천합니다.
향후 차기 제품 개발 시작 시, `develop`에서 v6 라인을 진행하고 안정화 후 `main`으로 승격(병합)하는 흐름이 안전합니다.
---

### 1-5. 메이저 업그레이드 시 브랜치 전략: "Cut the Cord" (연결 고리 끊기)

기존 v5와 새로운 v6(Java/Spring 업그레이드 버전)는 **더 이상 서로 Merge 되지 않는 독립된 트랙**으로 관리해야 합니다.

#### 변경된 브랜치 구조도

#### 단계별 실행 프로세스

1.  **v5 라인 동결 (Freeze & Archive):**

    * 현재의 `develop` 브랜치(v5 기반)에서 **`support/v5`** 브랜치를 생성합니다.
    * 이제부터 v5의 유지보수, 버그 수정, 추가 개발은 모두 `support/v5`에서 이루어집니다. `main`이나 `develop`으로 돌아오지 않습니다.

2.  **v6 개발 착수 (Next Gen Development):**

    * 기존 `develop` 브랜치는 이제 **v6(차세대) 전용**이 됩니다.
    * 여기서 JDK 버전을 올리고, Spring Boot 버전을 3.x로 올리는 작업을 진행합니다.
    * **핵심:** 이 시점부터 `support/v5`와 `develop`은 영원히 병합되지 않습니다.

3.  **메인 스트림 교체 (Switching Main):**

    * v6 개발이 완료되고 안정화되면, `develop`을 `main`으로 병합(또는 Reset)하여 `main` 브랜치의 내용을 v6로 교체합니다.
    * 이 시점부터 '기본 제품'은 v6가 되며, v4, v5는 `support/*` 브랜치를 통해 유지보수됩니다.

-----

### 1-6. 브랜치별 역할 및 운영 규칙 (Release 관리)

병합이 불가능한 상태에서 3개의 메이저 버전(v4, v5, v6)을 운영하는 방법입니다.

| 브랜치 | 상태 (Java/Spring) | 역할 및 배포 전략 | 병합 대상 |
| :--- | :--- | :--- | :--- |
| **`develop`** | **v6 (JDK 21 / Boot 3)** | 차세대 솔루션 개발. | `main` (배포 시) |
| **`main`** | **v6 (JDK 21 / Boot 3)** | 최신 정식 출시 버전. 신규 사이트는 이 버전으로 구축. | 없음 (Tag만 생성) |
| **`support/v5`** | **v5 (JDK 11 / Boot 2)** | 기존 40개 사이트 중 대다수를 위한 유지보수 트랙. | **자체 배포** (Tag: `v5.x.x`) |
| **`support/v4`** | **v4 (JDK 8 / Legacy)** | 구형 사이트 유지보수. (수명 종료 시까지 유지) | **자체 배포** (Tag: `v4.x.x`) |

> **핵심 변경점:** 기존에는 모든 배포가 `main`을 거쳐갔지만, 이제는 `support/v5`와 `support/v4`가 **독자적인 릴리즈 라인(End-of-Line Product)** 역할을 수행합니다.

-----

### 1-7. 코드 동기화 워크플로우 (Cherry-pick Strategy)

구조가 바뀌었어도 "로그인 로직 버그" 같은 비즈니스 로직 수정은 v5, v6 모두 필요할 수 있습니다. 병합이 안 되므로 \*\*Cherry-pick(체리픽)\*\*을 체계적으로 사용해야 합니다.

#### 시나리오: 로그인 버그 수정 (공통 이슈)

1.  **우선 순위:** 최신 버전인 \*\*`develop` (v6)\*\*에서 먼저 수정합니다. (`fix/login-error`)
2.  **검증:** v6 환경에서 테스트를 완료합니다.
3.  **이식 (Backporting):**
    * IntelliJ에서 프로젝트를 `support/v5` 브랜치로 체크아웃합니다. (또는 별도 폴더에 v5 프로젝트를 클론해둠)
    * Git Log에서 v6의 커밋을 찾아 **Cherry-pick** 합니다.
    * **충돌 해결:** Java 문법이나 Spring 클래스명이 달라진 부분(예: `javax.*` -\> `jakarta.*`)만 수동으로 수정합니다. **이 과정이 자동 병합보다 훨씬 안전합니다.**
4.  **배포:** v5 태그(`v5.2.2`)를 따서 배포합니다.

-----

### 1-8. IntelliJ & Gradle 환경 구성 베스트 프랙티스

자바/스프링 버전이 다른 여러 브랜치를 오갈 때 IDE 설정이 꼬이는 것을 방지해야 합니다.

#### A. Gradle Toolchain 활용 (강력 추천)

개발자 PC에 여러 JDK가 설치되어 있어도, 프로젝트(브랜치)별로 올바른 JDK를 자동으로 잡도록 설정합니다.
`build.gradle`에 다음과 같이 명시하십시오.

```groovy
// v6 브랜치의 build.gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// support/v5 브랜치의 build.gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11) // 또는 8
    }
}
```

* **효과:** 브랜치를 체크아웃(`git checkout`) 할 때마다 IntelliJ가 자동으로 해당 버전에 맞는 JDK를 로드합니다.

#### B. IntelliJ 워크스페이스 분리

브랜치 전환(Checkout) 시 인덱싱 시간이 오래 걸리고 설정 충돌이 잦다면, 물리적으로 폴더를 나누는 것을 추천합니다.

* **Folder A:** `~/project-v6` (Branch: `develop` / `main`) -\> JDK 21 설정
* **Folder B:** `~/project-v5` (Branch: `support/v5`) -\> JDK 11 설정
* **운영:** IntelliJ 창을 2개 띄워놓고 작업합니다. Cherry-pick이 필요할 때만 Remote를 통해 가져오거나 Patch 파일을 이용합니다.

-----

### 1-9. 요약: 안정적 운영을 위한 조언

1.  **`develop`과 `support`를 섞지 마십시오.** 구조가 바뀐 시점에서 Merge는 재앙입니다. 서로 다른 프로젝트라고 생각하십시오.
2.  **기능 개발은 v6(`develop`) 위주로 진행하십시오.** v5는 "안정화 및 버그 수정" 모드로 전환하여 변경을 최소화해야 합니다.
3.  **신규 고객사는 무조건 v6로 유도하십시오.** v5 사이트가 늘어나는 것은 기술 부채가 늘어나는 것입니다.

---

## 2. 릴리즈 및 고객 버그 픽스를 위한 릴리즈·태그 활용 방안

### 2-1. 버전 규칙(예시 – SemVer 근사)

* 버전 형식: `주버전.부버전.패치버전`

    * 예) `5.2.3`
    * 주버전(5): 큰 구조 변경, 호환성 깨짐
    * 부버전(2): 기능 추가, 호환성 유지
    * 패치(3): 버그 수정 / 작은 개선, 호환성 유지

현재는 v5, v4만 있으므로:

* v5: `5.x.y`
* v4: `4.x.y`

---

### 2-2. 유지보수 브랜치와 태그의 역할 분담

1. **유지보수 브랜치 (`support/v5`, `support/v4`)**

    * 운영 중인 v5, v4 라인의 유지보수 작업 공간
    * 버그 픽스, 문서, 비기능 변경이 지속적으로 반영되는 곳(신규 기능 제외)
    * 각 라인에서 실제 배포 시점에 태그를 생성하여 추적성 확보

2. **태그(Tag)**

    * “실제로 고객에게 배포된 빌드”를 정확히 가리키는 스냅샷

    * 예:

        * 공식 GA(일반 배포용):

            * `v5.2.3`
            * `v4.8.1`
        * v5의 롤업 패치(버그모음 패치):

            * `v5.2.3-p1` (patch 1)
            * `v5.2.3-p2`
        * 특정 고객 전용 빌드가 필요한 경우 (선택):

            * `v5.2.3-CUSTA.1`
            * `v5.2.3-CUSTB.1`

    * 항상 **annotated tag** 사용을 권장:

      ```bash
      git tag -a v5.2.3 -m "v5.2.3 release: scheduler 옵션 추가, 로그 레벨 개선 등"
      git push origin v5.2.3
      ```

---

### 2-3. 릴리즈 및 버그 픽스 작업 절차(예시)

#### (1) v5 버그 픽스 공통 케이스

1. 이슈 등록 (예: Jira 이슈 키: IQ-245)
2. 브랜치 생성

   ```bash
   git checkout support/v5
   git pull --ff-only
   git checkout -b bugfix/v5-IQ-245-fix-null-pointer
   ```
3. 수정 & 커밋

   ```bash
   git commit -am "IQ-245: Fix NPE when job comment is null"
   ```
4. 원격 푸시 & 코드리뷰 후 `support/v5`에 머지

   ```bash
   git checkout support/v5
   git merge --no-ff bugfix/v5-IQ-245-fix-null-pointer
   git push origin support/v5
   ```
5. QA 완료 후, 실제 릴리즈 버전 확정

   ```bash
   git tag -a v5.2.4 -m "v5.2.4: IQ-245 NPE fix 포함"
   git push origin v5.2.4
   ```

#### (2) v5·v4 모두에 적용해야 하는 버그 픽스

1. v5에서 먼저 수정
2. `support/v4`로 **cherry-pick**

   ```bash
   # v5 브랜치에서 커밋 해시 확인
   git log --oneline support/v5

   # v4 브랜치로 이동 후 cherry-pick
   git checkout support/v4
   git pull --ff-only
   git cherry-pick <v5커밋해시>
   git push origin support/v4
   ```
3. v4도 릴리즈 태그 생성

   ```bash
   git tag -a v4.9.0 -m "v4.9.0 release: IQ-245 fix 백포트"
   git push origin v4.9.0
   ```

#### (3) 특정 고객 전용 패치가 필요한 경우

1. 원칙:

    * 가능하면 공통 코드(`support/v5`)에서 해결하고,
      고객사에는 해당 공통 버전 태그를 기반으로 배포

2. 그래도 고객 전용 코드가 필요한 경우:

   ```bash
   git checkout support/v5
   git pull --ff-only

   # 고객 전용 브랜치 생성(최초 한 번)
   git checkout -b customer/CUSTA/v5
   git push origin customer/CUSTA/v5
   ```

3. 고객 전용 수정 커밋 후, 고객용 태그

   ```bash
   git checkout customer/CUSTA/v5
   # 수정 작업 후
   git commit -am "CUSTA: 특수 로깅 포맷 적용"

   git tag -a v5.2.4-CUSTA.1 -m "CUSTA용 커스텀 로깅 포함 빌드"
   git push origin customer/CUSTA/v5
   git push origin v5.2.4-CUSTA.1
   ```

4. 나중에 공통화 가능한 변경은 `customer/CUSTA/v5` → `support/v5`에 반영할지 검토

---

## 3. 온보딩용 형상관리 가이드 .md 초안

바로 저장해서 쓰실 수 있도록 마크다운 파일 형태로 드리겠습니다.
파일명 예시: `docs/git-workflow.md` 또는 `GIT_VERSION_CONTROL_GUIDE.md`

````markdown
# ETL 솔루션 형상관리(Git) 가이드

본 문서는 당사 ETL 솔루션(v5, v4)의 소스코드 형상관리를 위한 Git 운영 원칙을 정리한 것입니다.  
신규 입사자 및 외주 개발자는 반드시 본 문서를 숙지한 후 작업을 진행합니다.

---

## 1. 저장소 구조 개요

### 1.1 주요 브랜치

- `support/v5`
  - v5 제품 라인의 **공식 유지보수 브랜치**(JDK 11 / Boot 2)
  - v5 관련 버그 수정, 문서, 비기능 변경은 이 브랜치 기준으로 수행(신규 기능 제외)
- `support/v4`
  - v4 제품 라인의 공식 유지보수 브랜치(JDK 8 / Legacy)
- `develop`
  - 차세대 솔루션(v6, JDK 21 / Boot 3) 개발 통합 브랜치
- `main`
  - v6 정식 출시 라인(태그: v6.x.y), 신규 사이트 구축 기준
- `master`
  - 2017년 기준 레거시 코드 스냅샷
  - 현재 개발 및 유지보수에는 사용하지 않음 (역사 보관용)

### 1.2 작업 브랜치 유형

브랜치명 규칙:

- 기능 개발(Feature)
  - `feature/<제품버전>-<이슈키>-<요약>`
  - 예: `feature/v5-IQ-123-add-scheduler-option`
- 버그 수정(Bugfix)
  - `bugfix/<제품버전>-<이슈키>-<요약>`
  - 예: `bugfix/v5-IQ-245-fix-null-pointer`
- 긴급 패치(Hotfix)
  - `hotfix/<제품버전>-<이슈키>-<요약>`
  - 예: `hotfix/v5-IQ-300-fix-startup-error`
- 고객 전용 브랜치(필요 시에만)
  - `customer/<고객코드>/<제품버전>`
  - 예: `customer/CUSTA/v5`

---

## 2. 버전 및 태그 정책

### 2.1 버전 번호 규칙

- 형식: `주버전.부버전.패치`
  - 예: `5.2.3`
- v5: `5.x.y`
- v4: `4.x.y`

### 2.2 태그(Tag) 사용 원칙

- 태그는 **실제 고객에게 배포한 빌드**를 정확히 가리킵니다.
- 항상 **annotated tag**를 사용합니다.

#### 태그 유형

1. 공식 릴리즈(공통 배포용)
   - 예: `v5.2.3`, `v4.8.1`
2. 롤업 패치(여러 버그를 모아 낸 패치)
   - 예: `v5.2.3-p1`, `v5.2.3-p2`
3. 고객 전용 빌드(필요 시)
   - 예: `v5.2.3-CUSTA.1`, `v5.2.3-CUSTB.1`

#### 태그 생성 예시

```bash
# v5 유지보수 브랜치에서
git checkout support/v5
git pull --ff-only

# 태그 생성
git tag -a v5.2.3 -m "v5.2.3 release: IQ-123, IQ-130 포함"
git push origin v5.2.3
````

---

## 3. 일상적인 작업 플로우

### 3.1 v5 버그 수정 예시

1. 이슈 확인 (예: Jira 이슈 `IQ-245`)
2. 브랜치 생성

```bash
git checkout support/v5
git pull --ff-only

git checkout -b bugfix/v5-IQ-245-fix-null-pointer
```

3. 코드 수정 및 커밋

```bash
# 필요한 파일 수정 후
git status
git add <수정파일들>
git commit -m "IQ-245: Fix NPE when job comment is null"
git push origin bugfix/v5-IQ-245-fix-null-pointer
```

4. Merge Request(MR) 생성

    * 검토자 지정 후 코드 리뷰 진행
5. 리뷰 완료 후 `support/v5`에 머지

```bash
git checkout support/v5
git pull --ff-only
git merge --no-ff bugfix/v5-IQ-245-fix-null-pointer
git push origin support/v5
```

6. QA 완료 및 릴리즈 태그 생성

```bash
git checkout support/v5
git pull --ff-only
git tag -a v5.2.4 -m "v5.2.4: IQ-245 NPE fix 포함"
git push origin v5.2.4
```

---

### 3.2 v5 → v4로 버그 수정 백포트(공통 버그)

1. v5에서 버그를 먼저 수정 (위 3.1 절차)
2. v4 브랜치로 Cherry-pick

```bash
# v5 커밋 해시 확인
git log --oneline support/v5

# v4 브랜치에서 cherry-pick
git checkout support/v4
git pull --ff-only
git cherry-pick <v5커밋해시>
git push origin support/v4
```

3. v4 릴리즈 태그 생성

```bash
git tag -a v4.9.0 -m "v4.9.0: IQ-245 fix 백포트"
git push origin v4.9.0
```

---

### 3.3 특정 고객 전용 패치 예시

고객 전용 코드가 불가피한 경우에만 사용합니다.
가능하면 공통 코드(`support/v5`)에서 해결하는 것을 우선 원칙으로 합니다.

1. 고객 전용 브랜치 생성 (최초 1회)

```bash
git checkout support/v5
git pull --ff-only

git checkout -b customer/CUSTA/v5
git push origin customer/CUSTA/v5
```

2. 고객 전용 수정

```bash
git checkout customer/CUSTA/v5
# 코드 수정
git commit -am "CUSTA: 특수 로깅 포맷 적용"
git push origin customer/CUSTA/v5
```

3. 고객 전용 태그 생성

```bash
git tag -a v5.2.4-CUSTA.1 -m "CUSTA용 로깅 포맷 커스터마이징 포함"
git push origin v5.2.4-CUSTA.1
```

4. 공통화 가능한 변경사항은 정기적으로 `support/v5`로 역머지할지 검토합니다.

---

## 4. 커밋 메시지 규칙

* 기본 형식:

    * `<이슈키>: <간결한 요약>`
    * 예:

        * `IQ-123: Add retry option for scheduler`
        * `IQ-245: Fix NPE when job comment is null`
* 이슈가 없는 경우에도 최대한 내부 이슈 키, 고객 코드 등을 포함

    * 예: `CUSTA: Disable debug logs on production`

---

## 5. 기본 Git 명령어 예시

### 5.1 원격 브랜치/태그 동기화

```bash
# 모든 브랜치/태그 최신화
git fetch --all --prune
```

### 5.2 로컬 작업 상태 확인

```bash
git status
git log --oneline --graph --decorate --all
```

### 5.3 브랜치 삭제 (작업 종료 후)

```bash
# 로컬 브랜치 삭제
git branch -d bugfix/v5-IQ-245-fix-null-pointer

# 원격 브랜치 삭제
git push origin --delete bugfix/v5-IQ-245-fix-null-pointer
```

---

## 6. 운영 원칙 요약

1. **공통 코드 우선**:

    * 가능한 한 모든 수정은 `support/v5`, `support/v4` 기준으로 구현
2. **고객 전용 브랜치는 예외적으로만 사용**:

    * 필요 시 `customer/<고객코드>/<제품버전>` 형식 사용
3. **태그는 실제 배포 버전만**:

    * 태그 = 고객에게 전달된 빌드
4. **모든 변경은 이슈와 연결**:

    * 커밋 메시지에 이슈 키 포함
5. **master는 레거시, 현재는 사용하지 않음**:

    * 실 작업 브랜치는 `support/v5`, `support/v4` 중심

본 문서는 필요 시 운영 과정에서 지속적으로 업데이트합니다.

```

---

원하시면 이후 단계로는:

- 실제 현재 `v5`, `v4`, `master` 상태를 기준으로  
  “지금 당장 무엇을 rename / 보호(protect)할지”,  
  “기존 릴리즈 커밋에 어떤 식으로 태깅을 소급 적용할지(히스토리 정리)”도 시나리오별로 정리해 드릴 수 있습니다.
```
