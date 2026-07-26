# 03 - Functional Requirements Specification (FRS)

**Document ID:** PI-03

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the functional requirements of the HImpact Digital Event Automation Platform.

It specifies what the system must do from a business and user perspective without describing implementation details.

---

# Stakeholders

## Event Owner

The customer creating and managing an event.

Examples:

- Groom
- Bride
- Wedding Planner

---

## Guest

An invited participant who interacts with the event.

Examples:

- Family
- Friends
- Colleagues

---

## Administrator

HImpact staff responsible for platform operation.

Responsibilities:

- Customer support
- Payment approval
- Event monitoring
- Package management
- Theme management
- System configuration

---

# Functional Modules

| ID | Module |
|----|--------|
| FR-01 | Authentication |
| FR-02 | Event Management |
| FR-03 | Invitation Management |
| FR-04 | Guest Management |
| FR-05 | RSVP |
| FR-06 | Notification Engine |
| FR-07 | Gallery |
| FR-08 | Media Upload |
| FR-09 | Storage |
| FR-10 | Payments |
| FR-11 | Theme Management |
| FR-12 | Administration |
| FR-13 | Analytics |

---

# FR-01 Authentication

The system shall:

- Support Google Sign-In.
- Verify guest mobile numbers.
- Maintain secure user sessions.
- Support logout.
- Prevent unauthorized access.

---

# FR-02 Event Management

The Event Owner shall be able to:

- Create an event.
- Edit event information.
- Publish an event.
- Archive an event.
- Duplicate an event (future).
- Delete an event before publication.

Event information includes:

- Couple names
- Event title
- Date
- Time
- Venue
- Google Maps location
- Cover image
- Description

---

# FR-03 Invitation Management

The system shall:

- Generate a unique invitation URL.
- Support personalized invitations.
- Display countdown timer.
- Display event details.
- Display dress code (optional).
- Display schedule.
- Display location map.

---

# FR-04 Guest Management

The Event Owner shall be able to:

- Import guest list.
- Add guests manually.
- Remove guests.
- Search guests.
- Export guest list.
- Track attendance status.

Each guest record shall include:

- Name
- Mobile number
- Email (optional)
- Invitation status
- RSVP status
- Upload count

---

# FR-05 RSVP

Guests shall be able to:

- Accept invitation.
- Decline invitation.
- Mark attendance later.
- Update RSVP before the deadline.

The system shall display RSVP statistics to the Event Owner.

---

# FR-06 Notification Engine

The platform shall support:

- Email notifications.
- WhatsApp reminders.
- Browser push notifications.

Default reminder schedule:

- Three days before the event.
- One day before the event.
- Event day.

Optional reminders may be configured by the Event Owner.

Guests may disable non-essential notifications.

---

# FR-07 Gallery

Guests shall be able to:

- View gallery.
- Filter photos.
- Search their uploads.

Event Owners shall be able to:

- Download gallery.
- Share gallery.
- Delete media.
- Highlight featured photos.

---

# FR-08 Media Upload

Guests shall be able to:

- Capture photos directly from the browser.
- Upload existing media.
- Upload multiple files.
- Retry failed uploads.

The platform shall:

- Display upload progress.
- Compress images when appropriate.
- Validate file size.
- Validate supported formats.

Offline upload queue shall synchronize automatically when connectivity returns.

---

# FR-09 Storage

The platform shall support:

- Google Drive integration.
- Customer-owned storage.
- Dedicated event storage.
- Automatic folder creation.

Folder structure:

HImpact

+-- Event

+-- Guest A

+-- Guest B

+-- Guest C

Storage limits shall be configurable.

---

# FR-10 Payments

The platform shall support:

Manual payment methods:

- InstaPay
- Vodafone Cash

Workflow:

Order

?

Awaiting Payment

?

Payment Submitted

?

Under Review

?

Approved

?

Active

Future payment providers shall be integrated without changing business logic.

---

# FR-11 Theme Management

Customers shall be able to:

- Choose a predefined theme.
- Change colors.
- Change cover image.
- Update welcome message.

Premium customers may request custom themes.

---

# FR-12 Administration

Administrators shall be able to:

- Approve payments.
- Suspend events.
- Update pricing.
- Manage themes.
- Manage packages.
- View dashboards.
- Manage notifications.
- Review uploads.

---

# FR-13 Analytics

The platform shall provide:

- Guest count.
- RSVP statistics.
- Upload statistics.
- Storage usage.
- Notification delivery.
- Event activity timeline.

Future versions shall include AI insights.

---

# User Roles

| Feature | Admin | Owner | Guest |
|----------|:----:|:----:|:----:|
| Create Event | ? | ? | ? |
| Manage Event | ? | ? | ? |
| RSVP | ? | ? | ? |
| Upload Photos | ? | ? | ? |
| Download Gallery | ? | ? | ? |
| Manage Payments | ? | ? | ? |
| View Analytics | ? | ? | ? |

---

# Acceptance Criteria

The MVP shall be considered functionally complete when:

- Event creation is operational.
- Guests can authenticate.
- Invitations function correctly.
- RSVP workflow is complete.
- Notifications are delivered.
- Photo uploads work online and offline.
- Google Drive integration is operational.
- Manual payment workflow is complete.
- Admin dashboard supports daily operations.

---

# References

- PI-00 Project Vision
- PI-01 Product Strategy
- PI-02 Decision Log
- PI-04 Non-Functional Requirements
- PI-05 Software Architecture

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|-------------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Functional Requirements Specification |
