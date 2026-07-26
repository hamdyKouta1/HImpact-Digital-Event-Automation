package com.himpact.entity;

/**
 * User roles for Role-Based Access Control (RBAC).
 *
 * ADMIN  — HImpact staff with full platform access.
 * OWNER  — Event owner / organizer.
 * GUEST  — Invited event participant.
 *
 * See: project-index/03_Functional_Requirements.md — User Roles
 * See: project-index/05_Software_Architecture.md — Security Architecture
 */
public enum UserRole {
    ADMIN,
    OWNER,
    GUEST
}
