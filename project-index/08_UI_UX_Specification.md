# 08 - UI/UX Specification

**Document ID:** PI-08

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the complete user experience, interface standards, navigation structure, user journeys, screen hierarchy, and design principles for the HImpact Digital Event Automation Platform.

The platform follows a **Mobile-First**, **Progressive Web App (PWA)** approach while delivering a premium experience on tablets and desktop devices.

---

# UX Principles

- Mobile First
- One-Hand Friendly
- Maximum Three Clicks to Major Features
- Clean & Minimal Interface
- Premium Wedding Experience
- Fast Navigation
- Accessible Design
- Offline Friendly
- Responsive Layout
- Consistent Design Language

---

# Primary User Roles

## Event Owner

Responsibilities

- Create Event
- Customize Invitation
- Manage Guests
- View RSVP
- Monitor Uploads
- Download Gallery
- Configure Notifications

---

## Guest

Responsibilities

- Sign In
- Confirm Attendance
- Leave Congratulations
- Upload Photos
- View Gallery
- Receive Reminders

---

## Administrator

Responsibilities

- Approve Payments
- Manage Packages
- Manage Themes
- Manage Events
- Platform Analytics
- Customer Support

---

# Navigation Structure

```
Landing Page
      ¦
      ?
Invitation Link
      ¦
      ?
Google Sign-In
      ¦
      ?
Phone Verification
      ¦
      ?
Welcome Screen
      ¦
 +----+--------------------------+
 ?    ?          ?          ?
RSVP Gallery Upload Comment
      ¦
      ?
Thank You
```

Owner Portal

```
Dashboard
    ¦
 +--+--------------+
 ?  ?              ?
Guests Gallery Settings
 ¦      ¦           ¦
 ?      ?           ?
Analytics Payments Notifications
```

---

# Screen Inventory

## Public

- Landing Page
- Invitation Page
- Event Details
- Countdown
- Sign In
- Phone Verification

---

## Guest

- Home
- RSVP
- Upload Photos
- Camera
- Offline Upload Queue
- Gallery
- Congratulations
- Event Schedule
- Profile

---

## Owner

- Dashboard
- Event Settings
- Theme Selection
- Guest Management
- Invitation Management
- Gallery Management
- Notifications
- Payments
- Analytics

---

## Admin

- Dashboard
- Customers
- Events
- Packages
- Pricing
- Themes
- Storage
- Payments
- Reports
- System Settings

---

# Guest Journey

```
Receive Invitation

?

Open Link

?

Google Login

?

Verify Phone

?

Welcome Screen

?

Accept Invitation

?

Leave Congratulations

?

Receive Reminder

?

Take Photos

?

Offline Queue (if needed)

?

Photos Uploaded

?

Browse Gallery
```

---

# Event Owner Journey

```
Create Event

?

Choose Package

?

Complete Payment

?

Configure Event

?

Choose Theme

?

Import Guests

?

Publish Invitation

?

Monitor Event

?

Download Gallery
```

---

# Navigation Guidelines

Bottom Navigation (Guest)

- Home
- Upload
- Gallery
- Messages
- Profile

Side Navigation (Owner/Admin)

- Dashboard
- Events
- Guests
- Gallery
- Notifications
- Payments
- Analytics
- Settings

---

# UI Components

Primary Components

- Buttons
- Cards
- Navigation Bar
- Side Drawer
- Bottom Navigation
- Forms
- Inputs
- Dropdowns
- Toast Messages
- Dialogs
- Tables
- Charts
- Progress Indicators
- Upload Cards
- Gallery Grid

---

# Forms

All forms shall:

- Validate immediately
- Display inline errors
- Support mobile keyboards
- Auto-save where appropriate
- Prevent duplicate submissions

---

# Upload Experience

Workflow

```
Open Camera

?

Capture Photo

?

Preview

?

Compress

?

Upload

?

Success

OR

Offline Queue

?

Auto Sync
```

Features

- Progress Bar
- Retry Button
- Cancel Upload
- Background Upload
- Queue Counter

---

# Notification UX

Supported Channels

- Browser Push
- WhatsApp
- Email

Users may:

- Mute notifications
- Disable non-essential reminders
- Select "Don't show again" for optional prompts

---

# Empty States

Examples

- No Guests
- No Photos
- No Comments
- No Notifications
- No Analytics

Every empty state should include:

- Friendly illustration
- Helpful explanation
- Clear call-to-action

---

# Error Handling

Every error page shall include:

- Human-readable message
- Recovery action
- Retry button
- Contact Support option

---

# Responsive Breakpoints

| Device | Width |
|----------|-------|
| Mobile | <768px |
| Tablet | 768–1023px |
| Desktop | =1024px |

---

# Accessibility

The UI shall:

- Support keyboard navigation
- Meet WCAG AA contrast recommendations
- Use readable typography
- Provide visible focus states
- Include descriptive labels
- Support screen readers where practical

---

# Animation Guidelines

Use subtle animations only.

Recommended duration

150–300 ms

Allowed

- Fade
- Slide
- Scale
- Skeleton Loading
- Progress Animation

Avoid

- Flashing
- Excessive motion
- Long transitions

---

# UI Success Metrics

- Mobile usability > 95%
- Responsive on all supported devices
- Maximum 3 taps to major features
- Smooth scrolling
- Fast interactions
- Consistent visual language

---

# References

- PI-03 Functional Requirements
- PI-04 Non-Functional Requirements
- PI-05 Software Architecture
- PI-07 API Specification
- PI-09 Design System

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|----------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial UI/UX Specification |
