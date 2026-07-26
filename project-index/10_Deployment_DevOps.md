# 10 - Deployment & DevOps

**Document ID:** PI-10

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the deployment architecture, CI/CD pipeline, infrastructure, monitoring, backup strategy, and operational procedures for the HImpact Digital Event Automation Platform.

The MVP prioritizes low infrastructure cost while remaining cloud-ready and scalable.

---

# Deployment Goals

- Simple deployment
- Low monthly cost
- Automated releases
- Secure infrastructure
- Zero manual server configuration
- Cloud portability
- Fast rollback capability

---

# Deployment Architecture

```
                GitHub Repository
                       ¦
         +---------------------------+
         ?                           ?
 GitHub Actions               GitHub Pages
      (CI/CD)                  (Frontend)
         ¦
         ?
 Spring Boot Backend
         ¦
         ?
    PostgreSQL Database
         ¦
         ?
 Google Drive Storage
```

---

# Infrastructure

## Frontend

Technology

- React
- TypeScript
- Vite

Hosting

- GitHub Pages

Advantages

- Free
- CDN
- HTTPS
- Automatic deployment

---

## Backend

Technology

- Spring Boot
- Java 21

Development

- Raspberry Pi
- Local Docker

Production

- Ubuntu VPS

Future

- Docker Swarm
- Kubernetes

---

## Database

Technology

PostgreSQL

Development

Docker Container

Production

Managed PostgreSQL or VPS-hosted PostgreSQL

---

## Storage

Primary

Google Drive

Future

- Amazon S3
- Azure Blob
- Cloudflare R2
- OneDrive

---

# Raspberry Pi Usage

The Raspberry Pi will serve as the primary development infrastructure.

Uses

- Development server
- Local PostgreSQL
- Docker host
- Jenkins (optional)
- Local testing
- Backup server
- VPN endpoint (future)

It is **not recommended** for production deployment once customer traffic grows.

---

# CI/CD Pipeline

```
Developer Push

?

GitHub

?

GitHub Actions

?

Build

?

Unit Tests

?

Static Analysis

?

Build Docker Image

?

Deploy Backend

?

Deploy Frontend

?

Health Check

?

Production Ready
```

---

# Git Strategy

Main Branches

```
main
develop
feature/*
hotfix/*
release/*
```

Rules

- No direct commits to `main`
- Pull Requests required
- Code review before merge
- Protected branches

---

# Environment Configuration

Separate environments

Development

```
application-dev.yml
```

Testing

```
application-test.yml
```

Production

```
application-prod.yml
```

Sensitive configuration must be stored as environment variables or GitHub Secrets.

---

# Secrets Management

Never store secrets in Git.

Examples

- Database Password
- Google OAuth Client Secret
- JWT Secret
- Google Drive Credentials
- SMTP Password
- WhatsApp API Token

GitHub Secrets shall be used for CI/CD.

---

# Docker Strategy

Containers

- Backend
- PostgreSQL
- Nginx (future)
- Monitoring (future)

Development command

```bash
docker compose up -d
```

---

# Monitoring

Metrics

- CPU
- Memory
- Disk
- API Response Time
- Upload Success Rate
- Active Events
- Failed Uploads
- Authentication Failures

Future Tools

- Prometheus
- Grafana
- Loki

---

# Logging

Application Logs

- INFO
- WARN
- ERROR

Logs should include

- Timestamp
- Request ID
- User ID
- Event ID
- Service Name

Future

Centralized logging using ELK or Grafana Loki.

---

# Backup Strategy

Database

- Daily backup
- 7-day retention

Media

Stored in Google Drive.

Application

GitHub Repository.

Configuration

Version controlled.

---

# Disaster Recovery

Recovery priorities

1. Restore Backend
2. Restore Database
3. Restore Configuration
4. Validate Google Drive Access
5. Resume Operations

Target Recovery Time (RTO)

< 2 hours

Target Recovery Point (RPO)

< 24 hours

---

# Security

- HTTPS Everywhere
- JWT Authentication
- Google OAuth
- Firewall Enabled
- Automatic Security Updates
- Principle of Least Privilege
- Secure Environment Variables

---

# Performance Targets

| Metric | Target |
|----------|---------|
| API Response | <500 ms |
| Frontend Load | <3 sec |
| Availability | 99% |
| Upload Success | >98% |
| Build Time | <10 min |

---

# Estimated MVP Monthly Cost

| Service | Estimated Cost |
|----------|----------------|
| GitHub Pages | Free |
| GitHub Actions | Free Tier |
| Google Drive | Free (15 GB/Event) |
| Ubuntu VPS | ~$5-10/month |
| Domain Name | ~$1-2/month |
| Total | Target: = $15/month |

The design remains compatible with the agreed operational budget and supports future upgrades as customer demand increases.

---

# Future Improvements

- Docker Registry
- Kubernetes
- Blue/Green Deployment
- Canary Releases
- Auto Scaling
- CDN
- Redis Cache
- Message Queue
- Multi-Region Deployment

---

# References

- PI-05 Software Architecture
- PI-06 Database Design
- PI-07 API Specification
- PI-11 Testing Strategy
- PI-16 Operations Manual

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|-----------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Deployment & DevOps |
