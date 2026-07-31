package com.himpact.entity;

/**
 * Event types supported by the generic event engine.
 * MVP focuses on WEDDING while supporting future event types without architectural changes.
 *
 * See: project-index/00_Project_Vision.md — Product Definition
 * See: project-index/02_Decision_Log.md — DEC-003 Event Engine
 */
public enum EventType {
    WEDDING,
    ENGAGEMENT,
    BIRTHDAY,
    GRADUATION,
    CORPORATE,
    CONFERENCE,
    EXHIBITION,
    PRODUCT_LAUNCH,
    COMMUNITY,
    PRIVATE_CELEBRATION,
    OTHER
}
