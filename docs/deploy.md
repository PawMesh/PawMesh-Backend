# PawMesh Backend — EC2 배포 가이드 (Docker Compose)

앱(Spring Boot) + MySQL 을 하나의 `docker-compose.yml` 로 EC2 에 올립니다.

## 구성 파일
| 파일 | 역할 |
|------|------|
| `Dockerfile` | 멀티스테이지 빌드(gradle 빌드 → JRE 실행 이미지) |
| `docker-compose.yml` | **로컬 개발용** — 소스 빌드(`build: .`) + MySQL |
| `docker-compose.deploy.yml` | **EC2 운영용** — GHCR 이미지 pull + MySQL (빌드 없음) |
| `.env.example` | 환경변수 템플릿 (복사해 `.env` 생성) |
| `application.yml` / `application-prod.yml` | 공통 / 운영 프로필 설정 |
| `.github/workflows/ci.yml` | PR/푸시 시 gradle 빌드 검증 |
| `.github/workflows/cd.yml` | develop 푸시 시 GHCR 빌드/푸시 → EC2 자동 배포 |

## 배포 방식: CI/CD 자동 배포 (기본)
develop 브랜치에 push 되면 GitHub Actions 가 이미지를 빌드해 GHCR 에 올리고,
EC2 에 SSH 로 접속해 새 이미지를 받아 재기동합니다. **EC2 는 빌드하지 않습니다.**

```
push develop ──► CI(build) ──► CD: 이미지 빌드 → GHCR push ──► EC2 SSH: pull & up -d
```

### GitHub 레포 Secrets (Settings → Secrets and variables → Actions)
| Secret | 설명 |
|--------|------|
| `EC2_HOST` | EC2 퍼블릭 IP 또는 DNS |
| `EC2_USER` | 접속 계정 (Ubuntu: `ubuntu`, Amazon Linux: `ec2-user`) |
| `EC2_SSH_KEY` | EC2 접속용 **private key(.pem) 전체 내용** |
| `EC2_SSH_PORT` | (선택) SSH 포트, 미설정 시 22 |

> GHCR 인증은 워크플로우의 `GITHUB_TOKEN` 을 그대로 사용하므로 별도 토큰 secret 이 필요 없습니다.

### EC2 초기 1회 세팅 (자동 배포 전 준비)
```bash
# 1) Docker / compose 설치 (아래 "사전 준비" 참고)
# 2) 배포 디렉터리 + .env 준비 (CD 가 이 경로를 사용: ~/pawmesh-backend)
mkdir -p ~/pawmesh-backend && cd ~/pawmesh-backend
vi .env            # .env.example 내용을 참고해 MYSQL_* 값 입력
```
이후 develop 에 push 하면 자동 배포됩니다. (compose 파일은 CD 가 매번 전송)

## 사전 준비 (EC2)
1. EC2 보안 그룹 인바운드 오픈
   - `8080` (앱) — 필요 시 ALB/Nginx 뒤에 두는 것을 권장
   - `3306` 은 외부 공개하지 않는 것을 권장 (compose 내부 통신만 사용)
2. Docker / Docker Compose 설치
   ```bash
   sudo yum install -y docker || sudo apt-get install -y docker.io
   sudo systemctl enable --now docker
   sudo usermod -aG docker $USER   # 재로그인 후 sudo 없이 docker 사용
   ```
3. (프리티어 t2.micro 등 1GB RAM) 빌드 중 메모리 부족 방지용 swap
   ```bash
   sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile
   sudo mkswap /swapfile && sudo swapon /swapfile
   echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
   ```

## 수동 배포 (대안 / 초기 부트스트랩용)
CI/CD 없이 EC2 에서 직접 빌드·실행하고 싶을 때 사용합니다. (프리티어는 빌드 메모리 주의)
```bash
git clone <repo-url> && cd PawMesh-Backend

# 1) 환경변수 설정
cp .env.example .env
vi .env            # 비밀번호 등 실제 값 입력

# 2) 빌드 + 실행 (로컬 빌드용 compose)
docker compose --env-file .env up -d --build

# 3) 상태 확인
docker compose ps
docker compose logs -f app
```

## 동작 확인
- Health : `http://<EC2-PUBLIC-IP>:8080/actuator/health`
- Swagger: `http://<EC2-PUBLIC-IP>:8080/swagger-ui.html`

## 자주 쓰는 명령
```bash
docker compose down           # 중지 (DB 데이터 유지)
docker compose down -v        # 중지 + 볼륨 삭제 (DB 데이터 삭제)
docker compose up -d --build  # 코드 변경 후 재배포
docker compose logs -f app    # 앱 로그
```

## 기존 MySQL 데이터 재사용
이미 EC2 에 떠 있던 MySQL 볼륨의 데이터를 그대로 쓰려면,
`docker volume ls` 로 기존 볼륨명을 확인한 뒤 `docker-compose.yml` 의
`volumes:` 를 `external` 로 지정하세요. (파일 하단 주석 참고)

## 참고 / 주의
- `ddl-auto: update` 는 MVP 편의용입니다. 운영 안정화 후 `validate` + Flyway/Liquibase 도입을 권장합니다.
- DB 접속 정보는 이미지에 들어가지 않고 컨테이너 환경변수로만 주입됩니다. `.env` 는 절대 커밋하지 마세요(`.gitignore` 처리됨).
