# Git Bash에서 한글이 깨지는 원인과 해결 방법

이 문서는 Windows 환경에서 Git Bash(민TTY) 터미널로 프로젝트를 실행/빌드할 때 한글이 깨져 보이는 현상의 원인과 점검/해결 방법을 정리합니다.

## 핵심 원인
- "터미널 인코딩(대개 UTF-8)"과 "JVM 기본 인코딩(Windows에선 종종 MS949/CP949)"이 서로 달라서 발생합니다.
- 그 결과, Gradle/Java 프로세스가 CP949로 출력한 문자열을 Git Bash가 UTF-8로 해석하거나 반대로 해석하여 문자깨짐(모지바케)이 나타납니다.

현재 레포는 다음과 같이 Gradle 실행 시 JVM 인코딩을 UTF-8로 고정했습니다.
- 루트 gradle.properties: `-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8`
- membership-service 테스트 태스크: `systemProperty('file.encoding','UTF-8')`, `jvmArgs '-Dfile.encoding=UTF-8', '-Dsun.jnu.encoding=UTF-8'`

따라서 Gradle로 테스트/빌드를 실행할 때는 대부분 정상 출력됩니다. 다만, Gradle 외에 직접 `java` 명령을 실행하거나 Git Bash의 로케일 설정이 비정상일 경우 여전히 깨질 수 있습니다.

## 이렇게 확인해 보세요
Git Bash에서 아래를 순서대로 확인합니다.

1) 터미널 로케일(UTF-8 여부)
```
echo $LANG
locale
```
권장: `ko_KR.UTF-8` 또는 `en_US.UTF-8`

> 로케일이 `C` 또는 `C.UTF-8`로 나오고, `ko_KR.UTF-8`/`en_US.UTF-8`로 전환하고 싶다면 `docs/Locale-Setup.md`를 참고하세요. 환경(Windows Git Bash, Ubuntu/WSL, macOS, Docker)별 설정 방법을 정리했습니다.

2) JVM 기본 인코딩
```
./gradlew -Dorg.gradle.jvmargs="-XshowSettings:properties" --no-daemon --version | grep -i file.encoding || true
```
또는 임의의 Java 실행 시:
```
java -XshowSettings:properties -version 2>&1 | grep -i "file.encoding"
```
권장: `file.encoding = UTF-8`, `sun.jnu.encoding = UTF-8`

3) 간단 출력 테스트(한글)
```
printf "표준출력 테스트: 한글\n"
``` 
문자 깨짐이 없다면 Git Bash 측은 정상입니다. Java/Gradle 실행 시에만 깨지면 JVM 인코딩 설정 이슈일 가능성이 높습니다.

## 해결 방법

- Gradle/Java 공통(권장): 환경변수로 JVM 인코딩 강제
  - 일시적으로 현재 셸 세션에만 적용
    ```
    export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
    export GRADLE_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
    ```
  - 영구 적용(사용자 프로파일)
    - Git Bash의 `~/.bashrc` 또는 `~/.bash_profile`에 위 두 줄을 추가

- Git Bash 로케일을 UTF-8로
  - `~/.bashrc`에 다음 추가
    ```
    export LANG=ko_KR.UTF-8
    export LC_ALL=ko_KR.UTF-8
    ```
  - Git Bash(Options)에서 Text > Locale/Character set이 UTF-8인지 확인

- Windows 콘솔/PowerShell에서 실행할 경우
  - PowerShell/CMD에서 코드 페이지를 UTF-8로 변경: `chcp 65001`
  - Windows Terminal을 사용한다면 프로필의 문자 집합/폰트가 한글을 지원하는지 확인

- IDE 실행 시(예: IntelliJ IDEA)
  - Gradle 설정: Gradle JVM 옵션에 `-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8`
  - Run/Debug Configuration VM options에도 동일 옵션을 추가
  - Editor/File Encoding은 UTF-8로 설정

## 추가 팁
- git 출력/로그에서 깨짐이 있으면 Git Bash의 폰트가 한글을 지원하는지 확인하세요(예: Cascadia Mono, Consolas, D2Coding 등).
- Windows 설정 > 언어 및 지역 > 관리자 옵션의 "베타: 전역 언어 설정을 UTF-8로 사용"은 일부 구형 앱 호환성에 영향을 줄 수 있으니 신중히 사용하세요.

## 요약
- 원인: Git Bash(UTF-8)와 JVM 기본 인코딩(CP949)이 불일치
- 해결: JVM 인코딩을 UTF-8로 강제(JAVA_TOOL_OPTIONS/GRADLE_OPTS/gradle.properties), Git Bash 로케일을 UTF-8로 유지, 실행 환경(IDE/콘솔)별로 UTF-8 일관성 확보
