# 15 - AI Development Guide

**Document ID:** PI-15

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the engineering standards, AI-assisted development workflow, coding conventions, repository structure, and development principles for the HImpact Digital Event Automation Platform.

The objective is to ensure that every contributor—human or AI—builds the system consistently, predictably, and with production quality.

---

# Project Philosophy

The project follows a **Documentation First** approach.

Every implementation must follow this order:

```
Requirement
      ?
Architecture
      ?
Database
      ?
API
      ?
Backend
      ?
Frontend
      ?
Testing
      ?
Deployment
```

No feature should be implemented before its documentation exists.

---

# AI Development Principles

AI assistants must:

- Read the Project Index before coding.
- Follow existing architecture.
- Never invent undocumented features.
- Keep code modular.
- Prefer reusable components.
- Keep implementations simple.
- Write production-ready code.

AI must **never** change business rules without updating the corresponding Project Index document.

---

# Source of Truth

The following documents are authoritative.

Priority order:

1. Project Vision
2. Decision Log
3. Functional Requirements
4. Software Architecture
5. Database Design
6. API Specification

If documentation conflicts, the Decision Log takes precedence until updated.

---

# Approved Technology Stack

Frontend

- React
- TypeScript
- Vite
- Tailwind CSS
- React Router
- React Query
- Axios

Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven

Database

- PostgreSQL
- Flyway

Infrastructure

- Docker
- GitHub Actions
- GitHub Pages
- Ubuntu VPS

Storage

- Google Drive API

Authentication

- Google OAuth
- JWT

---

# Repository Structure

```
backend/
frontend/
infrastructure/
project-index/
docs/
diagrams/
.github/
```

No business logic should exist outside the approved project structure.

---

# Coding Standards

General

- Small functions.
- Single Responsibility Principle.
- Constructor injection.
- Dependency inversion.
- No duplicated logic.
- No hardcoded configuration.

Naming

Classes

```
EventService
GuestRepository
NotificationController
```

Methods

```
createEvent()
publishEvent()
approvePayment()
uploadPhoto()
```

Variables

```
eventId
guestCount
uploadStatus
```

Constants

```
MAX_UPLOAD_SIZE
DEFAULT_THEME
```

---

# Backend Standards

Packages

```
controller
service
repository
entity
dto
mapper
config
security
exception
util
```

Controllers must contain no business logic.

Services contain business rules.

Repositories only access the database.

---

# Frontend Standards

Folders

```
pages/
components/
layouts/
hooks/
services/
contexts/
types/
assets/
utils/
```

Reusable UI components must be shared.

No API calls inside UI components.

---

# Git Workflow

Branches

```
main
develop
feature/*
hotfix/*
release/*
```

Every feature:

```
Create Branch

?

Develop

?

Commit

?

Pull Request

?

Review

?

Merge
```

---

# Commit Convention

```
feat(auth): add Google login

fix(upload): resolve offline queue bug

docs(api): update upload endpoint

refactor(storage): simplify provider implementation

test(rsvp): add integration tests
```

---

# AI Prompt Workflow

Before requesting code from an AI assistant:

1. Reference the relevant Project Index documents.
2. Define the scope.
3. State constraints.
4. Request production-ready code.
5. Require tests.
6. Require documentation updates if needed.

---

# Code Quality Rules

Every Pull Request must:

- Compile successfully.
- Pass automated tests.
- Follow architecture.
- Include documentation updates.
- Avoid unnecessary dependencies.

---

# Security Rules

Never:

- Commit secrets.
- Hardcode credentials.
- Disable authentication.
- Trust client-side validation.

Always:

- Validate server-side.
- Use HTTPS.
- Use environment variables.
- Sanitize user input.

---

# Documentation Rules

Every major change must update one or more documents in `project-index`.

Required updates include:

- New features
- API changes
- Database changes
- Architecture changes
- Business rule changes

Documentation is part of the Definition of Done.

---

# Development Workflow

```
Select Backlog Item

?

Read Related Documentation

?

Design Solution

?

Implement Backend

?

Implement Frontend

?

Write Tests

?

Update Documentation

?

Commit

?

Pull Request

?

Merge
```

---

# AI Usage Guidelines

Recommended AI Tasks

- Boilerplate generation
- CRUD implementation
- Unit tests
- Integration tests
- Documentation drafting
- Refactoring suggestions
- Code review assistance

AI must **not** make product decisions independently.

---

# Definition of Done

A feature is complete when:

- Business requirements implemented.
- Tests passed.
- Documentation updated.
- Code reviewed.
- No critical issues remain.
- Successfully deployed to the development environment.

---

# Future Enhancements

- AI code review pipeline
- AI-generated test cases
- AI architecture validation
- Automated documentation synchronization
- AI-powered backlog estimation

---

# References

- PI-02 Decision Log
- PI-03 Functional Requirements
- PI-05 Software Architecture
- PI-07 API Specification
- PI-10 Deployment & DevOps
- PI-14 Product Backlog

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|-----------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial AI Development Guide |
