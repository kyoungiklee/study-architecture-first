# Git 워크플로우: 브랜치 생성 → 푸시 → Pull Request → 이슈 해결 가이드

이 문서는 `feature/dockerize-membership` 브랜치를 예시로, 로컬에서 작업한 변경사항을 GitHub 원격 저장소로 푸시하고 Pull Request(PR)를 생성하는 전형적인 절차와, 자주 발생하는 문제 및 해결책을 정리합니다.

- 대상 저장소(원격): `origin https://github.com/kyoungiklee/study-architecture-first.git`
- 로컬 프로젝트 루트: `C:\project\study\study-architecture-first`
- 권장 터미널: Windows PowerShell 또는 Git Bash

> 한글 로그/출력 인코딩 문제는 `docs/GitBash-Korean-Encoding.md`를 참고하세요.

---

## 1) 저장소 준비 및 원격(origin) 확인

```powershell
cd C:\project\study\study-architecture-first
git status
git remote -v
```

출력 예시:

```
origin  https://github.com/kyoungiklee/study-architecture-first.git (fetch)
origin  https://github.com/kyoungiklee/study-architecture-first.git (push)
```

만약 원격이 비어있다면 다음으로 추가합니다.

```powershell
git remote add origin https://github.com/kyoungiklee/study-architecture-first.git
```

---

## 2) 브랜치 생성/체크아웃

작업용 브랜치를 새로 만들고 전환합니다(예: `feature/dockerize-membership`).

```powershell
git switch -c feature/dockerize-membership
# 구버전 Git:
# git checkout -b feature/dockerize-membership
```

이미 로컬에 존재하는 경우:

```powershell
git switch feature/dockerize-membership
```

---

## 3) 변경사항 스테이징 및 커밋

변경 파일 확인 후 스테이지하고 커밋합니다.

```powershell
git status
git add .
git commit -m "Dockerize membership-service: add Dockerfile/compose and configs"
```

사용자 정보 미설정 오류가 발생하면:

```powershell
git config user.name "Your Name"
git config user.email "you@example.com"
# 전역 설정을 원하면 --global 옵션 사용
```

---

## 4) 브랜치 푸시(업스트림 설정)

처음 푸시할 때 업스트림을 함께 설정합니다.

```powershell
git push -u origin feature/dockerize-membership
```

이후부터는 동일 브랜치에서 `git push`만 해도 됩니다.

---

## 5) GitHub에서 Pull Request(PR) 생성

원격에 브랜치가 올라가면 GitHub 저장소에서 `Compare & pull request` 버튼이 표시됩니다. 수동으로 생성하려면 아래 비교 URL을 이용할 수 있습니다.

- 비교 URL(예시):
  - https://github.com/kyoungiklee/study-architecture-first/compare/main...feature/dockerize-membership

PR 생성 시 확인 사항:
- base: `main`(혹은 프로젝트 기본 브랜치)
- compare: `feature/dockerize-membership`
- 제목/본문에 변경 요약과 테스트/검증 방법 기재

---

## 6) 자주 발생하는 이슈와 해결책

1. 업스트림 미설정 오류
   - 증상: `fatal: The current branch ... has no upstream branch.`
   - 해결: `git push -u origin feature/dockerize-membership`

2. 로컬과 원격이 서로 달라진(diverged) 경우
   - 증상: `rejected`(non-fast-forward) 또는 pull 요구
   - 해결:
     ```powershell
     git fetch origin
     git pull --rebase origin feature/dockerize-membership
     # 충돌 해결 후
     git add <충돌 해결 파일들>
     git rebase --continue
     git push
     ```

3. 인증/권한 문제(2FA, 조직 정책)
   - 증상: 사용자/비밀번호 인증 실패
   - 해결: GitHub Personal Access Token(PAT) 사용, 또는 Git Credential Manager 로그인. 조직 보안 정책(SSO, SAML) 승인 필요 시 관리자 가이드에 따름.

4. `.gitignore` 누락으로 불필요 파일 추적
   - 해결: `.gitignore` 보완 → 캐시 제거 → 재추가/커밋
     ```powershell
     git rm -r --cached .
     git add .
     git commit -m "chore: update .gitignore"
     ```

5. 대용량/바이너리 파일로 인한 푸시 실패
   - 해결: Git LFS 사용
     ```powershell
     git lfs install
     git lfs track "*.zip"
     git add .gitattributes
     git commit -m "chore(lfs): track binary assets"
     git push
     ```

6. 보호된 브랜치 규칙으로 직접 푸시 불가
   - 증상: `protected branch` 정책으로 `main`에 직접 푸시 거부
   - 해결: 기능 브랜치로 PR 생성 후 리뷰/승인/머지

7. 리모트 기본 브랜치가 `main`이 아닐 때 비교 기준 오류
   - 해결: 리포지토리 기본 브랜치를 확인 후 PR의 `base`를 올바르게 선택

8. 라인 엔딩/인코딩 이슈로 불필요한 변경 폭증
   - 해결: `.gitattributes`로 `text eol=lf` 또는 `crlf` 정책 명시. Windows 환경 한글 출력 문제는 `docs/GitBash-Korean-Encoding.md` 참고.

9. 강제 푸시가 필요한 경우의 주의
   - 원칙적으로 `--force-with-lease`만 사용하고, 공유 브랜치에는 지양
     ```powershell
     git push --force-with-lease
     ```

---

## 7) 요약(Quick Start)

```powershell
cd C:\project\study\study-architecture-first
git switch -c feature/dockerize-membership
git add .
git commit -m "Dockerize membership-service: add Dockerfile/compose and configs"
git push -u origin feature/dockerize-membership
# 브라우저에서 PR 생성: https://github.com/kyoungiklee/study-architecture-first/compare/main...feature/dockerize-membership
```

필요 시 이후에는 해당 브랜치에서 `git push`만 실행하면 됩니다.
