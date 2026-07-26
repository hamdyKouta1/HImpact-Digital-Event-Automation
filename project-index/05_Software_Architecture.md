# 05 - Software Architecture

**Document ID:** PI-05

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the overall software architecture of the HImpact Digital Event Automation Platform.

The architecture is designed to be:

- Modular
- Scalable
- Cloud Portable
- API First
- AI Friendly
- Cost Optimized

The MVP prioritizes low operational cost while allowing seamless migration to enterprise infrastructure in future releases.

---

# Architectural Principles

- Mobile First
- API First
- Stateless Backend
- Configuration over Hardcoding
- Security by Design
- Cloud Native Ready
- Event Driven (Future)
- Modular Services
- Offline First Uploads

---

# High-Level Architecture

```
                    Internet
                        ¦
                        ?
                GitHub Pages (Frontend)
                        ¦
                        ?
                 HTTPS REST API
                        ¦
                        ?
                Backend Application
                        ¦
        +---------------+----------------+
        ¦               ¦                ¦
        ?               ?                ?
 Authentication   Business Logic   Notification Service
        ¦               ¦                ¦
        ¦               ¦        Email / WhatsApp / Push
        ¦               ¦
        ?               ?
 Google OAuth      Storage Service
                        ¦
             +---------------------+
             ?                     ?
      Google Drive           Local Cache
                                    ¦
                                    ?
                          Offline Upload Queue
```

---

# Architecture Layers

## Presentation Layer

Responsibilities

- Progressive Web App (PWA)
- Responsive UI
- Camera Access
- Gallery
- Guest Portal
- Admin Dashboard

Technology (Target)

- React
- TypeScript
- Tailwind CSS
- Vite
- PWA Support

---

## API Layer

Responsibilities

- Authentication
- Authorization
- Validation
- Request Routing
- API Versioning
- Rate Limiting

All communication uses REST over HTTPS.

---

## Business Layer

Contains all business logic.

Core Services

- Event Service
- Guest Service
- Invitation Service
- RSVP Service
- Gallery Service
- Upload Service
- Notification Service
- Payment Service
- Storage Service
- Theme Service
- Analytics Service

Business logic must never depend directly on third-party providers.

---

## Data Layer

Responsible for persistence.

Repositories

- Event Repository
- Guest Repository
- User Repository
- Media Repository
- Notification Repository
- Package Repository

Database access is isolated from business logic.

---

## External Integrations

Google OAuth

Purpose

User Authentication

---

Google Drive API

Purpose

Media Storage

---

WhatsApp Provider

Purpose

Guest Reminders

Future providers may be replaced without modifying business logic.

---

# Core Modules

```
Authentication
¦
+-- Users
+-- Roles
+-- Sessions

Event
¦
+-- Event
+-- Venue
+-- Theme
+-- Gallery

Guest
¦
+-- RSVP
+-- Invitation
+-- Comments

Media
¦
+-- Upload
+-- Queue
+-- Storage

Notification
¦
+-- Email
+-- WhatsApp
+-- Push

Administration
¦
+-- Packages
+-- Pricing
+-- Themes
+-- Dashboard
```

---

# Request Flow

```
Browser

?

Authentication

?

API

?

Business Service

?

Repository

?

Database

?

Response

?

Browser
```

---

# Offline Upload Flow

```
Capture Photo

?

Compress

?

Save Locally

?

Queue Upload

?

Internet Available?

+-- No
¦
+-- Wait

?

Yes

?

Upload

?

Google Drive

?

Update Gallery
```

---

# Security Architecture

Authentication

- Google OAuth

Authorization

- Role Based Access Control (RBAC)

Transport

- HTTPS Only

Secrets

- Environment Variables
- Never hardcoded

Validation

- Client Side
- Server Side

---

# Scalability Strategy

Current

- Single Backend Instance
- Single Database
- Google Drive Storage

Future

- Load Balancer
- Multiple Backend Instances
- CDN
- Object Storage
- Redis Cache
- Message Queue

The architecture must support horizontal scaling with minimal changes.

---

# Deployment Architecture

Frontend

GitHub Pages

?

Backend

Cloud VPS / Raspberry Pi (Development)

?

Database

PostgreSQL

?

Storage

Google Drive

Future deployments may migrate to cloud infrastructure without architectural redesign.

---

# Design Patterns

The platform adopts:

- Layered Architecture
- Repository Pattern
- Service Layer
- Dependency Injection
- Strategy Pattern
- Factory Pattern
- Adapter Pattern
- Provider Pattern

Future

- CQRS
- Event Sourcing (if required)

---

# Technology Targets

| Layer | Technology |
|--------|------------|
| Frontend | React + TypeScript |
| UI | Tailwind CSS |
| Backend | Spring Boot |
| Language | Java 21 |
| Database | PostgreSQL |
| Authentication | Google OAuth |
| Storage | Google Drive |
| Hosting | GitHub Pages + VPS |
| Version Control | Git |
| CI/CD | GitHub Actions |

---

# Architectural Decisions

- Backend remains stateless.
- Frontend consumes REST APIs only.
- Storage providers are interchangeable.
- Notification providers are interchangeable.
- Payment providers are interchangeable.
- All limits are configurable.
- Event logic remains generic and reusable.

---

# References

- PI-02 Decision Log
- PI-03 Functional Requirements
- PI-04 Non-Functional Requirements
- PI-06 Database Design
- PI-07 API Specification
- PI-10 Deployment & DevOps

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|---------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Software Architecture |
