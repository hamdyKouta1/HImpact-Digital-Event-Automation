package com.himpact.entity;

/**
 * Explicit Payment Lifecycle States.
 *
 * SUBMITTED     — Payment proof submitted by Event Owner (InstaPay / Vodafone Cash).
 * UNDER_REVIEW  — Payment under review by HImpact Finance/Admin team.
 * APPROVED      — Payment verified & approved by Admin.
 * ACTIVATED     — Event package activated & extended.
 * REJECTED      — Payment rejected due to invalid reference or receipt.
 *
 * See: PO Sprint 5 Workstream B Payment State Machine Requirement
 */
public enum PaymentState {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    ACTIVATED,
    REJECTED
}
