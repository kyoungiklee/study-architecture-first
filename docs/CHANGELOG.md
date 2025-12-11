변경내용 요약 (2025-12-12)

금일 변경은 문서 정리 및 온보딩 가이드 확충에 집중되었습니다.

1) Git Workflow 온보딩 문서 신설
- 위치: docs/ONBOARDING-Git-Workflow.md (신규)
- 주요 내용:
  - 소규모 조직에 적합한 경량 Git Flow 제안(main/develop/support/* 및 feature/bugfix/hotfix 분기 전략)
  - 릴리즈/태깅 정책(v6: main 태깅, v5/v4: support/* 브랜치 태깅)
  - MR 전략(Squash 권장, cherry-pick 활용), 브랜치 네이밍 규칙, 이슈 연계 규칙
  - 운영 라인(support/v5, support/v4)과 차세대 라인(develop)의 변경 전파 원칙

2) 헥사고날 아키텍처 온보딩 문서 보강
- 위치: docs/ONBOARDING-Hexagonal-Architecture.md (수정)
- 보강 내용:
  - 도메인/어댑터/포트 간 역할 구분과 매핑 규칙 보완
  - 컨트롤러-애플리케이션 서비스-영속 어댑터 간 의존성 방향성과 테스트 레이어 가이드 정리 강화
  - 예제 코드/섹션 구조 정돈 및 표현 개선

3) 기타
- 본 커밋에서는 소스 코드 로직 변경 없음(문서 업데이트 중심)


변경내용 요약 (2025-12-11)

금일 작업은 멤버십 영속 어댑터 빌드 오류 수정과 아키텍처 온보딩 문서 강화에 초점을 맞췄습니다. 주요 변경은 다음과 같습니다.

1) 빌드 오류 수정: MembershipPersistenceAdapter ↔ MembershipEntity
- 문제: `MembershipPersistenceAdapter`에서 `MembershipEntity.builder()`를 사용했지만, 엔티티에 빌더가 없어 컴파일 실패
- 조치:
  - `MembershipEntity`에 Lombok `@Builder` 추가
  - 어댑터의 `crateMembership` 구현을 기본 생성자 + setter 방식으로 안전하게 수정
  - Repository 테스트/검색 유틸에서의 빌더 사용과 호환성 유지
- 결과: 모듈 빌드 성공 확인

2) 온보딩 문서 강화 (Hexagonal 아키텍처 가이드)
- 위치: `docs/ONBOARDING-Hexagonal-Architecture.md`
- 추가/보강 내용:
  - 도메인 모델 vs 영속성 모델 분리 원칙 정리 및 매핑 규칙
  - DTO vs Domain Model 역할과 분리 이유, 권장 매핑 흐름, 안티패턴
  - 읽기/쓰기 서비스 분리(경량 CQRS) 가이드
  - 컨트롤러 CRUD 매핑 가이드 + OpenAPI(Swagger UI) 접속 경로와 테스트 방법
  - 테스트 작성 가이드라인 및 커버리지 기준(피라미드, 레이어별 전략, 실행/리포트 예시)

3) 검증
- `gradlew build` 성공


변경내용 요약 (2025-11-19)

이번 릴리스에서는 검색 전략을 QueryDSL로 표준화하고, 서비스 레이어 단위 테스트를 보강했으며, 공통 모듈의 빌드 실패 원인을 제거했습니다. 주요 변경은 다음과 같습니다.

1) QueryDSL 도입 및 표준화
- membership-service/build.gradle에 QueryDSL(Jakarta) 추가
  - com.querydsl:querydsl-jpa:5.1.0:jakarta
  - com.querydsl:querydsl-apt:5.1.0:jakarta
  - annotationProcessor: jakarta.annotation-api, jakarta.persistence-api
- Repository 커스텀 경로 추가
  - MembershipRepositoryCustom, MembershipRepositoryImpl 추가
  - 문자열(name/email/address)은 containsIgnoreCase, 불리언(isCorp/isValid)은 정확 일치, null 파라미터는 필터 제외
- Adapter 경로 전환
  - MembershipPersistenceAdapter#search(...)가 QueryDSL 기반 searchUsingQuerydsl(...) 사용
- 호환성 유지: 기존 @Query 기반 search(…)와 Example 기반 searchByExample(…)는 제거하지 않고 유지

2) 서비스 레이어(Unit) 테스트 추가 및 통과
- 추가 테스트
  - membership-service/src/test/java/.../application/service/MembershipCommandServiceTest.java
  - membership-service/src/test/java/.../application/service/MembershipQueryServiceTest.java
- 검증 범위: Command/Query 서비스의 매핑, 포트 호출 위임, 파라미터 전달 검증
- 결과: 모든 테스트 통과(일부 환경에서 Mockito inline self-attach 경고는 있으나 기능 영향 없음)

3) 공통 모듈 빌드 실패 원인 제거
- 원인: common 모듈에 Spring Boot 플러그인이 적용되어 bootJar 실행 시 mainClass를 찾지 못해 실패
- 조치: common/build.gradle에 bootJar 비활성화, jar 활성화 설정 추가
  - tasks.named('bootJar') { enabled = false }
  - tasks.named('jar') { enabled = true }
- 결과: ./gradlew clean build 성공

4) 미사용 포트 제거
- CQRS 분리 이후 사용되지 않는 MembershipPort 인터페이스 내용 제거(컴파일 산출물에서 제외)

5) 기타
- MembershipRepository에 Example 기반 searchByExample(…) 메서드 추가(간단 검색용)
- 멀티모듈 빌드/테스트 전반 정상 동작 확인


변경내용 요약 (2025-11-18)

이 문서는 최근 작업으로 적용된 변경사항을 간단히 요약합니다. 상세 설정/가이드는 각 문서 링크를 참고하세요.

1) 한글 깨짐(인코딩) 개선
- 목적: Windows/Git Bash 등에서 Gradle 테스트/로그 출력 시 한글이 깨지는 문제를 해소
- 변경 사항
  - 루트 gradle.properties 추가
    - org.gradle.jvmargs: -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
    - org.gradle.console=plain (일부 터미널에서 ANSI 처리로 인한 표시 문제 완화)
  - membership-service/build.gradle 테스트 태스크 보강
    - test.systemProperty('file.encoding','UTF-8')
    - test.jvmArgs('-Dfile.encoding=UTF-8','-Dsun.jnu.encoding=UTF-8')
- 기대 효과: Gradle 테스트 실행 시 표준 출력/표준 에러의 한글이 안정적으로 표시

2) Git Bash 한글 깨짐 가이드 문서 추가
- docs/GitBash-Korean-Encoding.md 신설
  - 원인: 터미널 UTF-8 vs JVM CP949 인코딩 불일치
  - 점검 방법: locale, JAVA file.encoding 확인
  - 해결책: JAVA_TOOL_OPTIONS/GRADLE_OPTS 설정, Git Bash 로케일/폰트 점검, PowerShell chcp 65001 등

3) 로케일 전환 가이드 문서 추가
- docs/Locale-Setup.md 신설
  - Git Bash, Ubuntu/WSL, RHEL 계열, macOS, Docker에서 ko_KR.UTF-8 또는 en_US.UTF-8 설정 절차 정리
  - 문서 간 교차 링크: GitBash-Korean-Encoding.md에서 본 문서로 연결

4) 컨트롤러 클래스명 오탈자 수정
- MembershipContoller → MembershipController로 파일명을 정정 (adapter/in/web)
- 관련 테스트 클래스는 유지되나, 사용 경로는 정상 동작 확인

5) 빌드/테스트 상태
- Gradle 빌드 성공 확인
- membership-service 모듈의 MockMvc 기반 컨트롤러 테스트 등 실행 시 한글 출력이 정상적으로 표시되는지 확인

참고 링크
- Git Bash 한글 인코딩 가이드: docs/GitBash-Korean-Encoding.md
- 로케일 설정 가이드: docs/Locale-Setup.md
