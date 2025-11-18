로케일(locale) ko_KR.UTF-8 또는 en_US.UTF-8 설정 가이드

이 문서는 `locale` 출력이 C.UTF-8로 나오거나 LANG이 비어 있을 때, 권장 로케일인 ko_KR.UTF-8 또는 en_US.UTF-8로 전환하는 방법을 환경별로 간단히 정리합니다.

0) 먼저 확인
- 현재 로케일: `locale` (또는 `echo $LANG`)
- 설치된 로케일: `locale -a` (없으면 생성 필요)

1) Git Bash (Windows)
- 사용자 프로파일(~/.bashrc 또는 ~/.bash_profile)에 아래 중 하나 추가 후 새 창 오픈 또는 `source` 실행
  - 한국어: `export LANG=ko_KR.UTF-8; export LC_ALL=ko_KR.UTF-8`
  - 영어:   `export LANG=en_US.UTF-8; export LC_ALL=en_US.UTF-8`
- 비고: Git Bash에는 모든 로케일이 없을 수 있음. en_US.UTF-8이 가장 호환성이 좋음. C.UTF-8도 문제 없이 사용 가능.

2) Ubuntu/Debian/WSL
- 로케일 생성: `sudo locale-gen ko_KR.UTF-8` 및/또는 `sudo locale-gen en_US.UTF-8`
- 시스템 기본 설정(택1)
  - 한국어: `sudo update-locale LANG=ko_KR.UTF-8 LC_ALL=ko_KR.UTF-8`
  - 영어:   `sudo update-locale LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8`
- 새 세션에서 `locale`로 확인.

3) RHEL/CentOS/Fedora
- 시스템 설정(택1)
  - 한국어: `sudo localectl set-locale LANG=ko_KR.UTF-8 LC_ALL=ko_KR.UTF-8`
  - 영어:   `sudo localectl set-locale LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8`
- `localectl status`, `locale`로 확인.

4) macOS (Terminal, zsh/bash)
- 기본은 UTF-8. 특정 로케일 강제가 필요하면 `~/.zshrc` 또는 `~/.bash_profile`에 추가
  - `export LANG=en_US.UTF-8` (또는 ko_KR.UTF-8)
  - `export LC_ALL=en_US.UTF-8` (또는 ko_KR.UTF-8)
- 적용: `source ~/.zshrc` 등, 이후 `locale` 확인.

5) Docker
- Ubuntu/Debian 기반 Dockerfile 예시
  - `apt-get install -y locales && locale-gen en_US.UTF-8 ko_KR.UTF-8 && update-locale LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8`
  - `ENV LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8`
- Alpine은 `locale-gen`이 없어 보통 UTF-8 가정으로 충분. 국제화가 중요하면 glibc 기반 이미지 고려.

6) Gradle/Java와의 관계
- 이 레포는 JVM 인코딩을 UTF-8로 고정함: gradle.properties 및 테스트 태스크 설정 참고.
- 로케일은 메시지/정렬/날짜 형식 등에, JVM 인코딩은 문자 깨짐에 영향을 줌. 둘 다 UTF-8 일관 유지 권장.
