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
