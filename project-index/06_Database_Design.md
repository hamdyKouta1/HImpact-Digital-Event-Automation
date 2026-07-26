# 06 - Database Design

**Document ID:** PI-06

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the logical database architecture for the HImpact Digital Event Automation Platform.

The schema is normalized, scalable, and event-agnostic to support future event types without structural redesign.

---

# Database Technology

| Property | Value |
|----------|-------|
| Database | PostgreSQL |
| Version | 16+ |
| ORM | Spring Data JPA |
| Naming Convention | snake_case |
| Primary Key | UUID |
| Time Zone | UTC |
| Soft Delete | Supported |
| Audit Columns | Enabled |

---

# Design Principles

- UUID as primary key.
- Foreign key integrity.
- Soft delete where appropriate.
- Audit all business entities.
- No hardcoded package limits.
- Generic event model.
- Optimized for reporting and analytics.

---

# Core Entity Relationship Diagram

```text
User
 ¦
 +--------------+
 ¦              ¦
 ?              ?
Event        AdminUser
 ¦
 +-------------------------------------+
 ?              ?                      ?
Guest       Invitation             Gallery
 ¦              ¦                      ¦
 ?              ?                      ?
RSVP      Notification          MediaFile
 ¦                                     ¦
 ?                                     ?
Comment                           UploadQueue

Event
 ¦
 +---------------+
 ?               ?
Package       Theme

Package
 ¦
 ?
Payment

StorageProvider
 ¦
 ?
GoogleDriveFolder
```

---

# Common Columns

Every business table shall contain:

```text
id (UUID)
created_at
updated_at
created_by
updated_by
is_deleted
version
```

---

# Entity Definitions

## users

Purpose

Stores authenticated platform users.

Columns

- id
- google_id
- full_name
- email
- mobile_number
- profile_picture
- role
- last_login
- status

---

## events

Purpose

Represents a single event.

Columns

- id
- owner_id
- title
- event_type
- bride_name
- groom_name
- description
- venue_name
- venue_address
- google_maps_url
- event_date
- start_time
- end_time
- cover_image
- package_id
- theme_id
- status

---

## guests

Purpose

Stores invited guests.

Columns

- id
- event_id
- full_name
- mobile
- email
- invitation_code
- invitation_url
- upload_limit
- uploaded_count
- storage_used_mb
- status

---

## invitations

Columns

- id
- event_id
- guest_id
- short_url
- qr_code
- sent_at
- opened_at
- viewed_count

---

## rsvp

Columns

- id
- guest_id
- attendance_status
- attendee_count
- response_time
- notes

Attendance Status

- Pending
- Accepted
- Declined
- Maybe

---

## comments

Digital congratulations.

Columns

- id
- guest_id
- event_id
- message
- created_at

---

## media_files

Stores uploaded media.

Columns

- id
- event_id
- guest_id
- original_filename
- storage_filename
- mime_type
- file_size
- image_width
- image_height
- storage_provider
- storage_path
- upload_status
- uploaded_at

---

## upload_queue

Supports offline synchronization.

Columns

- id
- guest_id
- local_identifier
- retry_count
- status
- synchronized_at

---

## notifications

Columns

- id
- event_id
- guest_id
- notification_type
- delivery_channel
- scheduled_at
- delivered_at
- status

---

## packages

Configurable commercial plans.

Columns

- id
- package_name
- max_guests
- max_uploads
- storage_limit_gb
- price
- active

---

## payments

Columns

- id
- event_id
- payment_method
- amount
- currency
- payment_reference
- payment_status
- approved_by
- approved_at

---

## themes

Columns

- id
- theme_name
- primary_color
- secondary_color
- preview_image
- premium

---

## storage_providers

Columns

- id
- provider_name
- provider_type
- root_folder_id
- access_token
- refresh_token
- quota

Supported Providers

- Google Drive

Future

- OneDrive
- Dropbox
- Amazon S3

---

# Relationships

```text
User (1)
   ¦
   +----< Event (N)

Event (1)
   +----< Guest (N)
   +----< Gallery (N)
   +----< Theme (1)
   +----< Package (1)
   +----< Payment (N)
   +----< Notification (N)

Guest (1)
   +----< RSVP (1)
   +----< Comment (N)
   +----< Invitation (1)
   +----< MediaFile (N)
   +----< UploadQueue (N)
```

---

# Index Strategy

Indexes shall exist for:

- email
- mobile_number
- google_id
- invitation_code
- event_date
- upload_status
- payment_status
- notification_status

Composite indexes

- event_id + guest_id
- guest_id + upload_status
- event_id + event_date

---

# Database Constraints

- Email uniqueness.
- Google ID uniqueness.
- Invitation code uniqueness.
- Foreign key enforcement.
- Cascade delete prohibited for business data.
- Soft delete preferred.

---

# Estimated Initial Scale

| Entity | Estimated Volume |
|----------|-----------------|
| Events | 10,000 |
| Guests | 2,000,000 |
| Media Files | 50,000,000 |
| Notifications | 5,000,000 |
| Comments | 1,000,000 |

The schema should support future horizontal scaling without redesign.

---

# Future Enhancements

- Multi-tenant architecture
- Event categories
- AI tagging
- Face recognition
- Search engine
- Analytics warehouse
- Read replicas
- Partitioned media tables

---

# References

- PI-03 Functional Requirements
- PI-04 Non-Functional Requirements
- PI-05 Software Architecture
- PI-07 API Specification
- PI-10 Deployment & DevOps

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|-----------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Database Design |
