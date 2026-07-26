# 07 - API Specification

**Document ID:** PI-07

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the REST API contract for the HImpact Digital Event Automation Platform.

All client applications shall communicate exclusively through HTTPS REST APIs.

The API is designed to be:

- RESTful
- Stateless
- Versioned
- Secure
- AI Friendly
- OpenAPI Compatible

---

# API Standards

Base URL

```
https://api.himpact.app/api/v1
```

Development

```
http://localhost:8080/api/v1
```

Content Type

```
application/json
```

Authentication

```
Bearer JWT Token
```

Date Format

```
ISO-8601 UTC
```

---

# HTTP Status Codes

| Code | Meaning |
|------|----------|
|200|Success|
|201|Created|
|204|No Content|
|400|Bad Request|
|401|Unauthorized|
|403|Forbidden|
|404|Not Found|
|409|Conflict|
|422|Validation Error|
|429|Too Many Requests|
|500|Internal Server Error|

---

# Authentication APIs

## Google Login

POST

```
/auth/google
```

Response

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "expiresIn":3600
}
```

---

## Verify Mobile Number

POST

```
/auth/verify-phone
```

---

## Refresh Token

POST

```
/auth/refresh
```

---

## Logout

POST

```
/auth/logout
```

---

# Event APIs

## Create Event

POST

```
/events
```

---

## Update Event

PUT

```
/events/{eventId}
```

---

## Get Event

GET

```
/events/{eventId}
```

---

## Delete Event

DELETE

```
/events/{eventId}
```

Soft delete only.

---

## Publish Event

POST

```
/events/{eventId}/publish
```

---

# Guest APIs

## Add Guest

POST

```
/events/{eventId}/guests
```

---

## Import Guests

POST

```
/events/{eventId}/guests/import
```

Supports:

- CSV
- Excel

---

## List Guests

GET

```
/events/{eventId}/guests
```

Supports:

- Pagination
- Search
- Sorting
- Filtering

---

## Update Guest

PUT

```
/guests/{guestId}
```

---

## Remove Guest

DELETE

```
/guests/{guestId}
```

---

# RSVP APIs

## Submit RSVP

POST

```
/events/{eventId}/rsvp
```

---

## Update RSVP

PUT

```
/rsvp/{id}
```

---

## RSVP Statistics

GET

```
/events/{eventId}/rsvp/statistics
```

---

# Invitation APIs

## Generate Invitation

POST

```
/events/{eventId}/invitation
```

---

## Invitation Details

GET

```
/invite/{code}
```

Public endpoint.

---

# Gallery APIs

## Upload Media

POST

```
/events/{eventId}/media
```

Multipart Upload

Supports:

- JPG
- PNG
- HEIC
- WEBP

---

## List Media

GET

```
/events/{eventId}/media
```

---

## Download Media

GET

```
/media/{id}
```

---

## Delete Media

DELETE

```
/media/{id}
```

---

# Comment APIs

## Add Congratulations

POST

```
/events/{eventId}/comments
```

---

## List Comments

GET

```
/events/{eventId}/comments
```

---

# Notification APIs

## Send Reminder

POST

```
/notifications/send
```

---

## Schedule Reminder

POST

```
/notifications/schedule
```

---

## Notification History

GET

```
/notifications/history
```

---

# Payment APIs

## Create Payment

POST

```
/payments
```

---

## Submit Payment

POST

```
/payments/{id}/submit
```

---

## Approve Payment

POST

```
/payments/{id}/approve
```

Admin only.

---

# Theme APIs

GET

```
/themes
```

---

GET

```
/themes/{id}
```

---

POST

```
/themes
```

Admin only.

---

# Storage APIs

## Connect Google Drive

POST

```
/storage/google/connect
```

---

## Storage Status

GET

```
/storage/status
```

---

## Storage Usage

GET

```
/storage/usage
```

---

# Dashboard APIs

## Owner Dashboard

GET

```
/dashboard/owner
```

---

## Admin Dashboard

GET

```
/dashboard/admin
```

Admin only.

---

# Analytics APIs

GET

```
/analytics/events/{eventId}
```

---

GET

```
/analytics/platform
```

Admin only.

---

# API Response Format

Success

```json
{
  "success": true,
  "message": "Operation completed successfully.",
  "data": {}
}
```

Error

```json
{
  "success": false,
  "errorCode": "EVENT_NOT_FOUND",
  "message": "Requested event does not exist."
}
```

---

# Security

- HTTPS only
- JWT Authentication
- Google OAuth
- Role-Based Access Control
- Input Validation
- Rate Limiting
- CORS Protection
- CSRF Protection (where applicable)

---

# API Versioning

Current

```
/api/v1
```

Future

```
/api/v2
```

Breaking changes must only be introduced in a new API version.

---

# OpenAPI

The backend shall automatically generate Swagger/OpenAPI documentation.

Endpoint

```
/swagger-ui
```

OpenAPI JSON

```
/v3/api-docs
```

---

# Future APIs

- AI Assistant
- Coupons
- Referral Program
- QR Check-In
- Live Timeline
- Face Recognition
- Push Notification Management
- White-Label Management

---

# References

- PI-03 Functional Requirements
- PI-05 Software Architecture
- PI-06 Database Design
- PI-08 UI/UX Specification
- PI-10 Deployment & DevOps

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|-------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial API Specification |
