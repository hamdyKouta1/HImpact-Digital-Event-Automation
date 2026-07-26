# 14 - Product Backlog

**Document ID:** PI-14

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the prioritized product backlog for the HImpact Digital Event Automation Platform.

The backlog follows Agile principles and will evolve throughout development.

Priority Levels

- P0 = Critical (Must Have)
- P1 = High
- P2 = Medium
- P3 = Low
- P4 = Future

---

# Epic 1 — Platform Foundation

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-001 | Initialize Spring Boot project | P0 | Planned |
| PB-002 | Initialize React + Vite project | P0 | Planned |
| PB-003 | Configure PostgreSQL | P0 | Planned |
| PB-004 | Configure Docker Compose | P0 | Planned |
| PB-005 | Configure GitHub Actions CI | P0 | Planned |
| PB-006 | Configure environments | P0 | Planned |

---

# Epic 2 — Authentication

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-020 | Google OAuth login | P0 | Planned |
| PB-021 | JWT authentication | P0 | Planned |
| PB-022 | Phone verification | P0 | Planned |
| PB-023 | Refresh token | P1 | Planned |
| PB-024 | Role management | P1 | Planned |

---

# Epic 3 — Event Management

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-040 | Create event | P0 | Planned |
| PB-041 | Edit event | P0 | Planned |
| PB-042 | Publish event | P0 | Planned |
| PB-043 | Archive event | P1 | Planned |
| PB-044 | Delete draft event | P1 | Planned |
| PB-045 | Duplicate event | P3 | Future |

---

# Epic 4 — Guest Management

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-060 | Add guest | P0 | Planned |
| PB-061 | Import guest list (CSV/Excel) | P0 | Planned |
| PB-062 | Search guests | P1 | Planned |
| PB-063 | Export guests | P1 | Planned |
| PB-064 | Guest statistics | P2 | Planned |

---

# Epic 5 — Invitations & RSVP

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-080 | Generate invitation link | P0 | Planned |
| PB-081 | Personalized invitation page | P0 | Planned |
| PB-082 | RSVP response | P0 | Planned |
| PB-083 | Countdown timer | P1 | Planned |
| PB-084 | Google Maps location | P1 | Planned |
| PB-085 | Congratulations messages | P1 | Planned |

---

# Epic 6 — Media Platform

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-100 | Upload photos | P0 | Planned |
| PB-101 | Camera integration | P0 | Planned |
| PB-102 | Offline upload queue | P0 | Planned |
| PB-103 | Automatic synchronization | P0 | Planned |
| PB-104 | Google Drive integration | P0 | Planned |
| PB-105 | Gallery | P0 | Planned |
| PB-106 | Download gallery | P1 | Planned |
| PB-107 | Featured photos | P2 | Future |

---

# Epic 7 — Notifications

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-120 | Email reminders | P0 | Planned |
| PB-121 | WhatsApp reminders | P0 | Planned |
| PB-122 | Browser push notifications | P1 | Planned |
| PB-123 | Notification preferences | P1 | Planned |
| PB-124 | "Don't show again" option | P1 | Planned |

---

# Epic 8 — Payments

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-140 | Manual payment submission | P0 | Planned |
| PB-141 | Admin payment approval | P0 | Planned |
| PB-142 | Payment status tracking | P1 | Planned |
| PB-143 | Payment history | P2 | Planned |
| PB-144 | Online payment gateway | P3 | Future |

---

# Epic 9 — Administration

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-160 | Admin dashboard | P0 | Planned |
| PB-161 | Package management | P0 | Planned |
| PB-162 | Theme management | P0 | Planned |
| PB-163 | Pricing management | P1 | Planned |
| PB-164 | Analytics dashboard | P1 | Planned |

---

# Epic 10 — Future Features

| ID | User Story | Priority | Status |
|----|------------|----------|--------|
| PB-180 | Corporate events | P4 | Future |
| PB-181 | AI assistant | P4 | Future |
| PB-182 | QR guest check-in | P4 | Future |
| PB-183 | Face recognition | P4 | Future |
| PB-184 | White-label platform | P4 | Future |
| PB-185 | Native mobile apps | P4 | Future |
| PB-186 | Multi-language support | P4 | Future |

---

# MVP Definition

The MVP is complete when the following are delivered:

- Authentication
- Event Management
- Guest Management
- RSVP
- Invitations
- Offline Photo Upload
- Google Drive Integration
- Gallery
- Notifications
- Manual Payment Workflow
- Admin Dashboard

These items are mandatory before the founder's wedding deployment.

---

# Sprint Plan

## Sprint 1

- Project setup
- CI/CD
- Database
- Authentication

## Sprint 2

- Event Management
- Guest Management

## Sprint 3

- Invitations
- RSVP

## Sprint 4

- Media Platform
- Offline Upload Queue

## Sprint 5

- Google Drive Integration
- Gallery

## Sprint 6

- Admin Dashboard
- Payments
- Notifications

## Sprint 7

- Testing
- Bug Fixes
- Production Deployment

---

# Backlog Prioritization Rules

1. Complete all P0 items before P1.
2. Production stability has higher priority than new features.
3. Security fixes override feature work.
4. Performance improvements override cosmetic enhancements.
5. Documentation must be updated before closing a backlog item.

---

# Definition of Ready

A backlog item is ready when:

- Business requirements are clear.
- Acceptance criteria are defined.
- UI/UX is approved.
- API impact is understood.
- Database changes are identified.

---

# Definition of Done

A backlog item is complete when:

- Development finished.
- Code reviewed.
- Tests passed.
- Documentation updated.
- Successfully deployed.
- Product Owner approved.

---

# References

- PI-03 Functional Requirements
- PI-05 Software Architecture
- PI-11 Testing Strategy
- PI-12 Project Roadmap
- PI-15 AI Development Guide

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|---------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Product Backlog |
