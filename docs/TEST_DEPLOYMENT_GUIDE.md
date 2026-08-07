# Test & Deployment Guide

This guide provides step-by-step instructions for provisioning, configuring, running, and verifying the **HImpact Digital Event Automation** system across local development, Docker test environments, and production deployments.

---

## 1. Prerequisites & System Requirements

Before starting, ensure your system meets the following software and hardware requirements:

### Hardware Requirements
- **CPU**: Dual-Core 2.0 GHz or higher
- **RAM**: Minimum 8 GB RAM (16 GB recommended)
- **Disk Space**: Minimum 10 GB free disk space

### Required Software Tools
| Tool | Minimum Version | Verification Command |
|---|---|---|
| **Git** | `2.34+` | `git --version` |
| **Java JDK** | `17+` | `java -version` |
| **Maven** | `3.8+` | `mvn -v` |
| **Node.js** | `18.x` or `20.x` | `node -v` |
| **npm** | `9.x+` | `npm -v` |
| **Docker Desktop / Engine** | `24.x+` | `docker --version` |
| **Docker Compose** | `v2.20+` | `docker compose version` |
| **PostgreSQL Client (optional)** | `16.x` | `psql --version` |

---

## 2. Estimated Setup Time

- **Local Development Setup (Maven + Node)**: ~10 - 15 minutes
- **Full Docker Containerized Deployment**: ~5 - 8 minutes

---

## 3. Clone Repository

Clone the project repository to your local workspace:

```bash
git clone https://github.com/hamdyKouta1/HImpact-Digital-Event-Automation.git
cd HImpact-Digital-Event-Automation
```

### Expected Output
```text
Cloning into 'HImpact-Digital-Event-Automation'...
remote: Enumerating objects: 450, done.
remote: Counting objects: 100% (450/450), done.
remote: Compressing objects: 100% (310/310), done.
remote: Receiving objects: 100% (450/450), 2.45 MiB | 5.20 MiB/s, done.
Resolving deltas: 100% (180/180), done.
```

---

## 4. Environment Variables Configuration

Copy the example environment template `.env.example` to `.env` in the root directory:

```bash
cp .env.example .env
```

### Key Environment Variables

```ini
# ─── Backend Configuration ──────────────────────────────────────────────────
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/HIMPACT_DB
SPRING_DATASOURCE_USERNAME=himpact_dev
SPRING_DATASOURCE_PASSWORD=hamdy

# JWT Secret (Must be at least 256 bits / 32 characters)
JWT_SECRET=super-secret-key-32-chars-minimum-length-for-hmac-sha256-security
JWT_EXPIRATION_MS=3600000
JWT_REFRESH_EXPIRATION_MS=604800000

# Google OAuth2 Credentials
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Google Drive API Service Account Credentials (Optional JSON String or Path)
GOOGLE_DRIVE_APPLICATION_NAME=HImpact
GOOGLE_DRIVE_CREDENTIALS_PATH=/secrets/google-drive-credentials.json

# CORS Allowed Origins
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost

# Email (SMTP Credentials)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-gmail-app-password

# Firebase (Push Notifications / SMS OTP)
SMS_PROVIDER=firebase
FIREBASE_PROJECT_ID=himpact-app
FIREBASE_SERVER_KEY=your-firebase-server-key

# ─── Frontend Configuration ─────────────────────────────────────────────────
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com

# ─── Infrastructure Configuration ───────────────────────────────────────────
POSTGRES_DB=HIMPACT_DB
POSTGRES_USER=himpact_dev
POSTGRES_PASSWORD=hamdy
```

---

## 5. Third-Party Integrations Setup

### 5.1 Google Drive Service Account Credentials Setup
1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a project named `HImpact-Digital-Event-Automation`.
3. Enable the **Google Drive API**.
4. Navigate to **APIs & Services > Credentials** and create a **Service Account**.
5. Generate a private key in **JSON format** and download it.
6. Place the JSON file at `backend/src/main/resources/google-drive-credentials.json` (or set `GOOGLE_DRIVE_CREDENTIALS_PATH`).

> [!NOTE]
> If Google Drive credentials are omitted, the application will automatically fall back to local disk storage (`LocalStorageProvider`) without throwing runtime errors.

### 5.2 Google OAuth2 Client ID Setup
1. In Google Cloud Console, go to **Credentials > Create Credentials > OAuth client ID**.
2. Set application type to **Web Application**.
3. Add Authorized JavaScript origins: `http://localhost:5173` and `http://localhost`.
4. Copy the **Client ID** into `GOOGLE_CLIENT_ID` and `VITE_GOOGLE_CLIENT_ID`.

### 5.3 SMTP Email Setup (Gmail App Password)
1. Enable **2-Step Verification** on your Google Account.
2. Generate an **App Password** under Security Settings.
3. Set `SMTP_USERNAME` to your Gmail address and `SMTP_PASSWORD` to the 16-character App Password.

### 5.4 Firebase Configuration
1. Open [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Generate a Web App config and copy `FIREBASE_PROJECT_ID` and `FIREBASE_SERVER_KEY`.

---

## 6. Method 1: Local Development Startup (Maven + Vite + PostgreSQL)

### Step 1: Start PostgreSQL via Docker

```bash
docker run -d \
  --name himpact-postgres \
  -e POSTGRES_DB=HIMPACT_DB \
  -e POSTGRES_USER=himpact_dev \
  -e POSTGRES_PASSWORD=hamdy \
  -p 5432:5432 \
  postgres:16-alpine
```

#### Expected Output
```text
b9e38e12a4f5c6d7e8f90123456789abcdef0123456789abcdef0123456789a
```

### Step 2: Build & Start Spring Boot Backend

Navigate to the `backend` directory, run Flyway migrations, and launch Spring Boot:

```bash
cd backend
./mvnw clean spring-boot:run
```

*(On Windows PowerShell, use `.\mvnw.cmd clean spring-boot:run`)*

#### Expected Output
```text
[INFO] Scanning for projects...
[INFO] Building himpact-backend 1.0.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
2026-07-31T17:35:00.123+03:00  INFO --- [main] o.f.c.i.database.base.BaseDatabaseType   : Database: jdbc:postgresql://localhost:5432/himpact (PostgreSQL 16.2)
2026-07-31T17:35:00.245+03:00  INFO --- [main] o.f.core.internal.command.DbMigrate      : Current version of schema "public": 15
2026-07-31T17:35:00.246+03:00  INFO --- [main] o.f.core.internal.command.DbMigrate      : Schema "public" is up to date. No migration necessary.
2026-07-31T17:35:02.890+03:00  INFO --- [main] com.himpact.HimpactApplication           : Started HimpactApplication in 4.567 seconds (process running for 5.123)
```

### Step 3: Install Dependencies & Start React Frontend

In a separate terminal, navigate to the `frontend` directory:

```bash
cd frontend
npm install
npm run dev
```

#### Expected Output
```text
  VITE v5.2.8  ready in 320 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h + enter to show help
```

---

## 7. Method 2: Containerized Deployment via Docker Compose

To deploy the entire production stack (Nginx Proxy + Spring Boot + PostgreSQL) with a single command:

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

### Expected Output
```text
[+] Building 45.2s (24/24) FINISHED
 => [backend internal] load build definition from Dockerfile
 => [frontend internal] load build definition from Dockerfile
[+] Running 4/4
 ✔ Container himpact-prod-postgres  Healthy                                       0.5s
 ✔ Container himpact-prod-backend   Started                                       1.2s
 ✔ Container himpact-prod-frontend  Started                                       1.5s
 ✔ Container himpact-prod-proxy     Started                                       1.8s
```

### Verify Container Status

```bash
docker compose -f docker-compose.prod.yml ps
```

#### Expected Output
```text
NAME                    IMAGE                COMMAND                  SERVICE    CREATED          STATUS                    PORTS
himpact-prod-backend    himpact-backend      "java -jar /app/app.…"   backend    1 minute ago     Up 1 minute               8080/tcp
himpact-prod-frontend   himpact-frontend     "/docker-entrypoint.…"   frontend   1 minute ago     Up 1 minute               80/tcp
himpact-prod-postgres   postgres:16-alpine   "docker-entrypoint.s…"   postgres   1 minute ago     Up 1 minute (healthy)     5432/tcp
himpact-prod-proxy      nginx:1.27-alpine    "/docker-entrypoint.…"   proxy      1 minute ago     Up 1 minute               0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp
```

---

## 8. Verification & Health Monitoring

### 8.1 OpenAPI / Swagger UI
Open your browser and navigate to:
- **Swagger Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> ![Swagger UI Placeholder](https://via.placeholder.com/800x400?text=Swagger+UI+OpenAPI+Interface)

### 8.2 Spring Boot Actuator Health Endpoint

Verify database connectivity and disk space health:

```bash
curl http://localhost:8080/actuator/health
```

#### Expected Output
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 128456789012,
        "threshold": 10485760,
        "path": "C:\\Users\\Hamdy\\OneDrive\\Documents\\Projects\\HImpact-Digital-Event-Automation\\backend\\.",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    },
    "livenessState": {
      "status": "UP"
    },
    "readinessState": {
      "status": "UP"
    }
  }
}
```

### 8.3 Micrometer Prometheus Metrics Endpoint

```bash
curl http://localhost:8080/actuator/prometheus
```

#### Expected Output
```text
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Survivor Space",} 4194304.0
http_server_requests_seconds_count{application="himpact-backend",exception="None",method="GET",outcome="SUCCESS",status="200",uri="/actuator/health",} 1.0
```

---

## 9. Troubleshooting & Common Errors

### Error 1: `FlywayException: Validate failed: Migrations have failed validation`
- **Cause**: Database table schema was manually modified outside of Flyway migration scripts.
- **Fix**: Reset database or run `mvn flyway:repair`.
```bash
cd backend
./mvnw flyway:repair -Dflyway.url=jdbc:postgresql://localhost:5432/HIMPACT_DB -Dflyway.user=himpact_dev -Dflyway.password=hamdy
```

### Error 2: `PSQLException: Connection to localhost:5432 refused`
- **Cause**: PostgreSQL service is not running or port 5432 is blocked.
- **Fix**: Verify container status with `docker ps` and restart PostgreSQL:
```bash
docker restart himpact-postgres
```

### Error 3: `JWT_SECRET warning: Using default fallback JWT secret`
- **Cause**: Environment variable `JWT_SECRET` is not exported in shell.
- **Fix**: Ensure your `.env` file contains `JWT_SECRET` with at least 32 characters and run `export $(cat .env | xargs)`.

---

## 10. Environment Reset & Teardown

To clean up all docker containers, networks, and persistent database volumes:

```bash
docker compose -f docker-compose.prod.yml down -v
```

### Expected Output
```text
[+] Running 5/5
 ✔ Container himpact-prod-proxy     Removed                                       0.2s
 ✔ Container himpact-prod-frontend  Removed                                       0.1s
 ✔ Container himpact-prod-backend   Removed                                       0.2s
 ✔ Container himpact-prod-postgres  Removed                                       0.1s
 ✔ Volume himpact_postgres_prod_data Removed                                     0.1s
 ✔ Volume himpact_uploads_prod_data  Removed                                     0.1s
```
