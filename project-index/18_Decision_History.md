# 18 - Decision History

**Document ID:** PI-18

**Version:** 1.0.0

**Status:** Living Document

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document records every significant architectural, technical, business, and product decision made throughout the lifecycle of the HImpact Digital Event Automation Platform.

Unlike the Decision Log, which captures active architectural decisions, this document serves as the historical record of why decisions were made, when they were made, and their outcomes.

---

# Decision Categories

- Business
- Product
- Architecture
- Infrastructure
- Database
- Security
- DevOps
- UI/UX
- AI Development
- Operations

---

# Decision Format

Each decision should include:

- Decision ID
- Date
- Category
- Decision
- Reason
- Alternatives Considered
- Outcome
- Status

---

# Decision History

---

## DEC-001

Date

2026-07-24

Category

Business

Decision

Build a Digital Event Automation Platform instead of a simple wedding gallery application.

Reason

The broader platform supports multiple event types and long-term business growth while allowing the wedding solution to be delivered as the first product.

Alternatives

- Wedding gallery only
- Photo sharing application
- Generic event management

Outcome

Approved

Status

Implemented

---

## DEC-002

Category

Storage

Decision

Use a dedicated Google account for each customer event.

Reason

Provides approximately 15 GB of free storage per event while isolating customer data and minimizing operational costs.

Password credentials are securely shared with the event owners (Bride & Groom), allowing them to retain ownership of their event media.

Alternatives

- Shared Google Drive
- Amazon S3
- Azure Blob Storage
- Cloudflare R2

Outcome

Approved

Status

Implemented

---

## DEC-003

Category

Infrastructure

Decision

Use Raspberry Pi as the primary development and local infrastructure server.

Reason

Reduces infrastructure cost while providing an always-on environment for development, testing, Docker services, and PostgreSQL.

Production deployments will migrate to a cloud VPS as customer demand increases.

Alternatives

- Dedicated VPS from day one
- Local laptop only

Outcome

Approved

Status

Implemented

---

## DEC-004

Category

Frontend

Decision

Use React + TypeScript + Vite.

Reason

Excellent performance, large ecosystem, strong community support, and compatibility with Progressive Web App development.

Alternatives

- Angular
- Vue
- Flutter Web

Outcome

Approved

Status

Implemented

---

## DEC-005

Category

Backend

Decision

Use Java 21 with Spring Boot.

Reason

Enterprise-grade architecture, strong security ecosystem, maintainability, and alignment with the team's technical expertise.

Alternatives

- Node.js
- .NET
- Django

Outcome

Approved

Status

Implemented

---

## DEC-006

Category

Database

Decision

Use PostgreSQL.

Reason

Open-source, reliable, scalable, and fully compatible with Spring Boot and Flyway.

Alternatives

- MySQL
- MariaDB
- MongoDB

Outcome

Approved

Status

Implemented

---

## DEC-007

Category

Authentication

Decision

Authenticate users using Google OAuth with phone verification.

Reason

Simplifies onboarding while ensuring each guest is uniquely identifiable.

Alternatives

- Email/password
- OTP only
- Social login without verification

Outcome

Approved

Status

Implemented

---

## DEC-008

Category

Media Upload

Decision

Implement an offline upload queue with automatic synchronization.

Reason

Wedding venues may have unstable or unavailable internet connectivity. Guests should be able to continue capturing memories without interruption.

Alternatives

- Online-only uploads

Outcome

Approved

Status

Implemented

---

## DEC-009

Category

Product

Decision

Offer premium customization services in addition to self-service themes.

Reason

Some customers require personalized invitations, branding, or technical assistance. The platform should provide an option to contact the owner for advanced customization.

Alternatives

- Self-service themes only

Outcome

Approved

Status

Implemented

---

## DEC-010

Category

Documentation

Decision

Adopt a Documentation First development process using the Project Index.

Reason

Ensures AI assistants and developers work from a single source of truth before implementation begins.

Alternatives

- Code-first development
- Minimal documentation

Outcome

Approved

Status

Implemented

---

## DEC-011

Category

Deployment

Decision

Host the frontend on GitHub Pages and manage deployments using GitHub Actions.

Reason

Provides a free, secure, and automated deployment pipeline for the MVP while remaining scalable.

Alternatives

- Netlify
- Vercel
- Self-hosted web server

Outcome

Approved

Status

Implemented

---

## DEC-012

Category

Business Validation

Decision

Use the founder's wedding on **11 September 2026** as the MVP production validation event.

Reason

A real production event provides meaningful feedback, validates business assumptions, and accelerates product maturity.

Alternatives

- Internal demo only
- Closed beta with external users

Outcome

Approved

Status

Planned

---

# Decision Review Process

When a significant decision is proposed:

1. Document the proposal.
2. Evaluate alternatives.
3. Assess technical and business impact.
4. Record the final decision.
5. Update related Project Index documents.
6. Notify contributors if implementation is affected.

---

# Decision Status Values

- Proposed
- Under Review
- Approved
- Implemented
- Deprecated
- Rejected

---

# Governance Rules

- Every major architectural or business decision must be recorded.
- Existing decisions must not be overwritten; instead, append a new decision referencing the previous one if changes are required.
- Related documentation must be updated after approval.
- Decision IDs are immutable.

---

# References

- PI-01 Product Strategy
- PI-02 Decision Log
- PI-05 Software Architecture
- PI-10 Deployment & DevOps
- PI-12 Project Roadmap
- PI-19 Project Principles

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|---------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Decision History |
