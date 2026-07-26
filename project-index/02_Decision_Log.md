# 02 - Decision Log

**Document ID:** PI-02

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document records all approved business, product, architectural, and technical decisions made during the planning phase of the HImpact Digital Event Automation Platform.

Every significant decision must be documented to maintain consistency, traceability, and architectural integrity throughout the project's lifecycle.

---

# Decision Process

Each decision follows this lifecycle:

```
Proposal
    ?
Discussion
    ?
Evaluation
    ?
Approval
    ?
Implementation
    ?
Review (if required)
```

A decision should only be modified through a documented review and approval process.

---

# Approved Decisions

## DEC-001 — Product Vision

**Status:** Approved

HImpact is a **Digital Event Automation Platform**, not simply an online invitation website.

The platform automates the complete event lifecycle while preserving event memories.

---

## DEC-002 — MVP Scope

**Status:** Approved

The MVP supports **Wedding Events** only.

The architecture must remain generic so future event types can be introduced without redesigning the core system.

---

## DEC-003 — Event Engine

**Status:** Approved

The backend is event-agnostic.

Business logic must use generic services such as:

- EventService
- InvitationService
- NotificationService
- GalleryService

Avoid event-specific implementations such as:

- WeddingService
- BirthdayService

---

## DEC-004 — Platform Type

**Status:** Approved

The MVP will be delivered as a responsive Progressive Web Application (PWA).

No native Android or iOS applications will be developed during Version 1.0.

---

## DEC-005 — Payment Strategy

**Status:** Approved

Hybrid payment architecture.

Version 1.0 supports:

- InstaPay
- Vodafone Cash

Future versions will integrate:

- Paymob
- Stripe
- Fawry

Payments must be abstracted through a PaymentProvider interface.

---

## DEC-006 — Pricing Model

**Status:** Approved

Hybrid pricing.

Customers choose a predefined package with optional upgrades.

Pricing is configurable and never hardcoded.

---

## DEC-007 — Storage Strategy

**Status:** Approved

Hybrid storage model.

Supported options:

- Customer connects an existing Google Drive.
- Dedicated Google account for the event.

All storage providers must implement the StorageProvider interface.

---

## DEC-008 — Customer Onboarding

**Status:** Approved

Hybrid onboarding.

Primary flow:

- Self-service wizard.

Secondary flow:

- Concierge setup via HImpact support.

---

## DEC-009 — Theme Strategy

**Status:** Approved

Professional predefined themes with optional customization.

Premium custom themes are available as an additional service.

---

## DEC-010 — Offline Upload

**Status:** Approved

Guests can continue taking photos without internet access.

Media is stored locally and synchronized automatically once connectivity returns.

---

## DEC-011 — Notifications

**Status:** Approved

Notification channels:

- Email
- WhatsApp
- Browser Push Notifications

Users may dismiss notifications permanently using a "Don't show again" option where applicable.

---

## DEC-012 — Media Ownership

**Status:** Approved

Event owners retain ownership of all uploaded media.

HImpact only facilitates secure storage and access.

---

## DEC-013 — Authentication

**Status:** Approved

Guest authentication requires:

- Google Sign-In
- Mobile phone verification

Authentication must balance simplicity with accountability.

---

## DEC-014 — Deployment Strategy

**Status:** Approved

Version 1.0 prioritizes free or low-cost infrastructure.

Infrastructure decisions should minimize operational expenses while maintaining acceptable performance and reliability.

---

## DEC-015 — Performance Target

**Status:** Approved

Target user experience:

- Performance: 7/10 or higher.
- Smooth mobile experience.
- Responsive interface.
- Stable operation during live events.

---

## DEC-016 — Founder Validation

**Status:** Approved

The first production deployment will support the founder's wedding.

This event serves as the primary real-world validation of the MVP before broader commercial release.

---

# Future Decisions

The following topics will be documented in future revisions:

- AI Assistant
- Coupon Engine
- Referral Program
- Subscription Model
- White-label Platform
- Enterprise Multi-tenancy
- Internationalization
- Analytics Platform
- Marketplace Integration

---

# Decision Governance

All future architectural or business changes must:

1. Be documented.
2. Reference affected Project Index documents.
3. Include implementation impact.
4. Record approval date.
5. Maintain backward compatibility where practical.

---

# References

- PI-00 Project Vision
- PI-01 Product Strategy
- PI-05 Software Architecture
- PI-10 Deployment & DevOps
- PI-19 Project Principles
- PI-20 Assumptions & Constraints

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|--------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Decision Log |
