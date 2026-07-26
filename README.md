# HImpact Digital Event Automation Platform

> **Automate the Event. Preserve the Memories.**

HImpact is a Digital Event Automation Platform that simplifies event planning, automates guest engagement, centralises media collection, and creates a seamless digital experience before, during, and after every event.

---

## MVP Focus

Wedding events — with a generic architecture ready for future event types.

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React + TypeScript + Vite + TailwindCSS |
| Backend | Java 21 + Spring Boot 3 + Maven |
| Database | PostgreSQL 16 + Flyway |
| Auth | Google OAuth2 + JWT |
| Storage | Google Drive API |
| Hosting | GitHub Pages (Frontend) + Ubuntu VPS (Backend) |
| CI/CD | GitHub Actions |
| Containers | Docker + Docker Compose |

## Repository Structure

```
HImpact-Digital-Event-Automation/
├── backend/          # Spring Boot application
├── frontend/         # React + Vite application
├── infrastructure/   # Docker Compose + deployment config
├── project-index/    # Single Source of Truth documentation
├── diagrams/         # Architecture and flow diagrams
├── docs/             # Supplemental documentation
├── assets/           # Brand assets and media
├── scripts/          # Utility and deployment scripts
└── .github/          # GitHub Actions workflows
```

## Quick Start (Development)

### Prerequisites
- Java 21
- Node.js 20+
- Docker & Docker Compose
- Maven 3.9+

### Start the full stack

```bash
cd infrastructure
docker compose up -d
```

### Run backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run frontend

```bash
cd frontend
npm install
npm run dev
```

## Documentation

All project documentation lives in [`project-index/`](./project-index/README.md).

## License

Copyright © 2026 HImpact. All rights reserved.