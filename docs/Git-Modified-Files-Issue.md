### Git에서 다수의 파일이 수정된 것으로 표시되는 이슈 정리

#### 개요 (현상)
로컬에서 별도의 코드 변경을 하지 않았는데도 `git status`에 매우 많은 파일이 `modified` 상태로 표시되거나, 줄바꿈(EOL)·권한·인코딩 등 비내용 변경으로 인해 변경 내역이 쌓이는 문제가 발생할 수 있습니다. 이 문서는 해당 이슈의 대표적 원인과 진단/해결/검증 절차를 정리합니다. 환경은 Windows(파워셸) 중심으로 설명하되, WSL/리눅스도 함께 고려합니다.

---

#### 주요 원인
- 줄바꿈(EOL) 차이: Windows의 CRLF(`\r\n`) vs Unix의 LF(`\n`)
  - `core.autocrlf` 설정 또는 `.gitattributes` 부재/오설정으로 인해 대량의 EOL 재작성 발생
  - 예: 저장소에는 LF로 저장되어 있는데, 체크아웃 시 CRLF로 변환되며 diff가 발생

- 파일 권한(file mode) 변경 감지
  - 리눅스/WSL에서 `chmod +x` 등 실행권한 비트 차이로 `git`이 변경을 감지
  - Windows에서 `core.filemode`가 true인 경우 권한 비트를 잘못 추적

- 파일 인코딩/BOM(Byte Order Mark)
  - UTF-8 BOM 유무 차이로 변경으로 인식될 수 있음
  - 에디터 저장 설정(예: VS Code, IntelliJ)의 자동 BOM 삽입/제거

- 공백/탭/트레일링 스페이스
  - 에디터의 자동 정리(Trim trailing whitespace, Convert tabs to spaces)로 대량 변경 발생

- 대·소문자 파일명 변경 (Windows의 대소문자 비구분 파일시스템)
  - `core.ignorecase`와 OS 특성으로 인해 파일명 대소문자 교체가 의도치 않은 변경으로 표시

---

#### 현재 저장소 점검 체크리스트 (진단)
아래 명령으로 현재 설정과 변경 원인을 파악합니다.

1) Git 기본 설정 확인
```
git config --show-origin --get core.autocrlf
git config --show-origin --get core.filemode
git config --show-origin --get core.ignorecase
git config --show-origin --get core.safecrlf
```

2) 줄바꿈(normalization) 영향 확인
```
git diff --name-only
git diff -- .gitattributes
```
`.gitattributes`가 없다면 추가 필요, 있다면 규칙이 적절한지 확인합니다.

3) 권한 변경 감지 여부
```
git diff --summary
```
`mode change` 항목이 보이면 권한 비트가 원인일 수 있습니다.

4) 대량 변경 파일 빠른 확인
```
git ls-files -m | Measure-Object
git ls-files -m | Select-Object -First 20
```

5) 에디터 저장 설정 확인
- IDE/에디터에서 다음 항목 확인: EOL, BOM, Trim trailing whitespace, Insert final newline, Tabs vs Spaces

6) 한글/콘솔 인코딩 이슈 참고
- `docs/GitBash-Korean-Encoding.md` 문서를 참고하여 콘솔 인코딩 문제를 분리 진단합니다.

---

#### 권장 설정 (정책)
이 저장소에서는 `.gitattributes`를 통해 줄바꿈 정책을 명시적으로 관리하고, 로컬 Git 전역 설정은 보수적으로 설정하는 것을 권장합니다.

- `.gitattributes` 예시(권장)
```
# 모든 텍스트 파일은 LF로 정규화하여 저장소에 보관
* text=auto eol=lf

# 셸/유닉스 스크립트는 LF 유지
*.sh text eol=lf

# Windows 배치 스크립트는 CRLF 유지(필요 시)
*.bat text eol=crlf
*.cmd text eol=crlf

# 코드/마크다운/프로퍼티 등은 LF 고정
*.java text eol=lf
*.kt   text eol=lf
*.md   text eol=lf
*.yml  text eol=lf
*.yaml text eol=lf
*.properties text eol=lf

# 바이너리는 줄바꿈 변환 금지
*.png binary
*.jpg binary
*.jar binary
*.zip binary
```

- Git 로컬 설정(Windows에서 권장)
```
# EOL 변환은 저장소 정책(.gitattributes)에 맡기고, 전역 자동 변환은 끔
git config --global core.autocrlf false

# 권한 비트 추적 비활성화(Windows 파일시스템에서 권장)
git config --global core.filemode false

# 안전한 줄바꿈 검사(충돌 방지용). 필요 시 warn 또는 false로 조정
git config --global core.safecrlf warn
```

주의: 이미 전역에 `core.autocrlf true`가 설정되어 있다면, 위 정책으로 전환 시 한번의 재정규화(renormalize)가 필요합니다.

---

#### 해결 절차
아래 순서로 진행하면 대부분의 “대량 수정 표시” 이슈를 해결할 수 있습니다.

1) `.gitattributes` 준비/정비
- 위 권장 예시를 기반으로 저장소 루트에 `.gitattributes`를 생성/수정하고 커밋합니다.

2) 로컬 Git 설정 점검
```
git config --global core.autocrlf false
git config core.autocrlf false
git config --global core.filemode false
git config core.filemode false
git config --global core.safecrlf warn
```

3) 줄바꿈 재정규화(renormalize)
- 방법 A: 안전하고 일반적인 방식
```
git add --renormalize .
git commit -m "chore: normalize line endings via .gitattributes"
```

- 방법 B: 캐시 비우고 재추적(변경이 매우 클 때)
```
git rm --cached -r .
git reset --hard
# 또는 필요 시, 다시 추적
git add .
git commit -m "chore: re-track files after .gitattributes normalization"
```

4) 권한 변경(모드 변경) 무시 설정
- Windows/WSL 혼용 환경에서 권장
```
git config core.filemode false
```
리눅스 서버에서 실행권한이 필요한 파일은 `.gitattributes`/CI 단계에서 권한 부여 스크립트로 관리하는 방식을 고려합니다.

5) 대소문자 파일명 교정(필요 시)
```
# 예: MyFile.java → myfile.java 로만 바꾸는 경우
git mv MyFile.java temp.__rename__
git mv temp.__rename__ myfile.java
git commit -m "chore: fix filename case"
```

6) BOM/인코딩 정리
- IDE에서 프로젝트 전체 인코딩을 UTF-8(Without BOM 권장)로 맞추고 저장
- 특정 파일에 BOM이 필요한 경우에만 예외적으로 허용

---

#### 검증 방법 (체크리스트)
- `git status`에 불필요한 대량 변경이 사라졌는가?
- `git diff`에서 내용 변경 없이 EOL/권한/BOM만 바뀌는 파일이 줄었는가?
- 새로 생성되는 파일들이 정책대로 EOL을 따르는가? (예: `.yml`이 LF로 저장되는지)
- CI나 다른 개발자 환경에서 동일 문제가 재발하지 않는가?

검증 명령 예시
```
git status
git diff --name-only
git diff --summary | Select-String "mode change"
```

---

#### 롤백/주의사항
- `.gitattributes` 도입 직후에는 한 번의 대규모 변경이 발생할 수 있습니다. PR로 분리하고 메시지에 “EOL 정규화”임을 명확히 남기세요.
- 팀 합의 없이 전역 `core.autocrlf`를 임의로 변경하면, 다른 개발자 환경에서 재발할 수 있습니다. 저장소 정책 우선(.gitattributes) → 전역 설정 보조 순으로 유지하세요.
- 이미 발행된 브랜치에서 대량 정규화를 진행할 때는 rebase 충돌 가능성이 있으므로, 가급적 빠른 시점에 공통 브랜치에 적용하는 것이 좋습니다.

---

#### 부록: 빠른 해결 가이드(Windows, 이 저장소 기준)
1) `.gitattributes` 확인/추가 후 커밋
2) 전역 설정 조정
```
git config --global core.autocrlf false
git config --global core.filemode false
git config --global core.safecrlf warn
```
3) 정규화 적용
```
git add --renormalize .
git commit -m "chore: normalize line endings via .gitattributes"
```
4) 상태 확인
```
git status
```

추가로, 콘솔 한글 깨짐 등은 `docs/GitBash-Korean-Encoding.md`를 참고하세요.
