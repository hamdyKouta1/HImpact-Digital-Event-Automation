# 20 - Assumptions and Constraints

**Document ID:** PI-20

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the assumptions, constraints, dependencies, and limitations considered during the design and development of the HImpact Digital Event Automation Platform.

These assumptions provide context for architectural decisions and help ensure alignment among developers, stakeholders, and AI development assistants.

---

# Project Assumptions

## Business Assumptions

### A-001

The MVP will initially target wedding events.

Future versions will support:

- Corporate Events
- Birthdays
- Graduations
- Conferences
- Private Events

---

### A-002

The founder's wedding on **11 September 2026** will serve as the first production deployment and customer validation.

---

### A-003

Customers are willing to authenticate using Google accounts.

---

### A-004

Customers value a premium digital experience over a large number of features.

---

### A-005

The initial customer base will be relatively small, allowing the platform to prioritize quality over scale.

---

# Technical Assumptions

### A-101

Guests primarily use modern mobile browsers.

Supported browsers include:

- Chrome
- Edge
- Safari
- Firefox

---

### A-102

Most guests have access to smartphones capable of taking photos and accessing Progressive Web Apps.

---

### A-103

Google OAuth services remain available during event operation.

---

### A-104

Google Drive APIs remain compatible with the implemented integration.

---

### A-105

Internet connectivity may be intermittent at event venues.

Therefore:

- Offline upload queue
- Local storage
- Automatic synchronization

are mandatory platform capabilities.

---

# Infrastructure Assumptions

### A-201

Development infrastructure will initially use:

- Raspberry Pi
- Docker
- PostgreSQL
- GitHub Actions

---

### A-202

Production hosting will migrate to a cloud VPS as customer demand increases.

---

### A-203

GitHub Pages is sufficient for hosting the frontend during the MVP stage.

---

### A-204

Each customer event will use a dedicated Google account to provide approximately 15 GB of storage.

This minimizes operational costs while giving customers ownership of their media.

---

# Operational Assumptions

### A-301

Customers may request custom themes or personalized invitation designs.

The platform therefore provides an option to contact the owner for advanced customization and support.

---

### A-302

Most customer support requests will occur before or during the event.

Rapid response procedures are required for live events.

---

### A-303

Documentation is maintained alongside code and forms part of the development workflow.

---

# Constraints

## Budget Constraints

The MVP should operate within a very low monthly infrastructure budget.

Target

= $15/month

Preferred services

- GitHub Pages
- GitHub Actions
- PostgreSQL
- Docker
- Google Drive
- Raspberry Pi

---

## Time Constraints

The MVP must be completed in time for the founder's wedding deployment.

Deadline

**11 September 2026**

Priority

Production readiness over feature quantity.

---

## Technology Constraints

Approved technologies

Frontend

- React
- TypeScript
- Vite

Backend

- Java 21
- Spring Boot

Database

- PostgreSQL

Authentication

- Google OAuth
- JWT

Storage

- Google Drive

Changes to the approved stack require architectural review.

---

## Team Constraints

Current development team

- Founder / Product Owner
- AI Development Assistants

Future contributors must follow the documented standards and architecture.

---

## Performance Constraints

Target metrics

| Metric | Target |
|----------|---------|
| Initial Load | <3 sec |
| API Response | <500 ms |
| Upload Success | >98% |
| Availability | =99% |

---

## Security Constraints

The platform must:

- Use HTTPS
- Protect customer data
- Never expose credentials
- Validate all server-side inputs
- Follow least-privilege principles

---

## Documentation Constraints

All significant changes must update the corresponding Project Index documents before completion.

Documentation is considered part of the deliverable.

---

# External Dependencies

The platform depends on:

- Google OAuth
- Google Drive API
- GitHub
- GitHub Actions
- Java Ecosystem
- PostgreSQL
- Docker

Future integrations may include:

- Payment Gateway
- WhatsApp Business API
- SMS Provider
- Cloud Object Storage

---

# Known Limitations (MVP)

The initial release does **not** include:

- Native Android application
- Native iOS application
- Online payment gateway
- AI assistant
- Face recognition
- QR guest check-in
- Multi-language support
- White-label platform
- Multi-region deployment

These capabilities are planned for future releases.

---

# Review Process

This document shall be reviewed whenever:

- Business assumptions change
- Technology stack changes
- Budget changes
- Infrastructure changes
- Major architectural decisions are introduced

---

# References

- PI-00 Project Vision
- PI-01 Product Strategy
- PI-05 Software Architecture
- PI-10 Deployment & DevOps
- PI-12 Project Roadmap
- PI-19 Project Principles

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|-------------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Assumptions and Constraints |
