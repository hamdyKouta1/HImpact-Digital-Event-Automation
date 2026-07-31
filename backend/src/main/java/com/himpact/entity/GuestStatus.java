package com.himpact.entity;

/**
 * Guest lifecycle status.
 *
 * INVITED     — Guest record created, invitation link generated.
 * REGISTERED  — Guest signed in and confirmed phone number.
 * ATTENDED    — Guest checked in on event day.
 * DECLINED    — Guest declined RSVP.
 * BLOCKED     — Guest blocked by event owner.
 *
 * See: project-index/03_Functional_Requirements.md — FR-04 Guest Management
 */
public enum GuestStatus {
    INVITED,
    REGISTERED,
    ATTENDED,
    DECLINED,
    BLOCKED
}
