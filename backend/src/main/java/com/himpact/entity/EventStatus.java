package com.himpact.entity;

/**
 * Event lifecycle state.
 *
 * DRAFT      — Event created, being configured by Owner.
 * PUBLISHED  — Event active and live; guests can view invitations & upload media.
 * ARCHIVED   — Event completed; read-only gallery access.
 * SUSPENDED  — Suspended by Admin due to payment or security issue.
 *
 * See: project-index/03_Functional_Requirements.md — FR-02 Event Management
 */
public enum EventStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED,
    SUSPENDED
}
