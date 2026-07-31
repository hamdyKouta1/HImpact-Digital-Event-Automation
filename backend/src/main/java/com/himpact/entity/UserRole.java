package com.himpact.entity;

/**
 * Role-Based Access Control (RBAC) System Roles.
 * Expanded to support fine-grained administration per PO Workstream C Requirements.
 *
 * See: project-index/03_Functional_Requirements.md — Role & Permissions
 * See: PO Sprint 5 Workstream C Admin Security
 */
public enum UserRole {
    SUPER_ADMIN, // Full platform access & global configuration
    ADMIN,       // Platform management, user support & payments
    SUPPORT,     // Customer support & event troubleshooting
    FINANCE,     // Manual payment review, approvals & revenue analytics
    OWNER,       // Customer owning a paid digital event
    GUEST        // Invited participant in an event
}
