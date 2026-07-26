# 13 - Risk Register

**Document ID:** PI-13

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document identifies the business, technical, operational, financial, and security risks associated with the HImpact Digital Event Automation Platform.

It defines mitigation strategies, contingency plans, ownership, and monitoring requirements to reduce the likelihood and impact of project risks.

---

# Risk Management Process

```
Identify
    ?
Assess
    ?
Mitigate
    ?
Monitor
    ?
Review
```

Risk reviews shall occur:

- Weekly during development
- Before every production release
- Immediately after any major incident

---

# Risk Rating Matrix

## Probability

| Level | Description |
|--------|-------------|
| Low | Unlikely |
| Medium | Possible |
| High | Likely |

---

## Impact

| Level | Description |
|--------|-------------|
| Low | Minor inconvenience |
| Medium | Feature degradation |
| High | Business-critical failure |

---

# Risk Register

| ID | Risk | Probability | Impact | Mitigation | Owner |
|----|------|------------|---------|------------|-------|
| R-001 | Internet unavailable during event | High | High | Offline upload queue with automatic synchronization | Engineering |
| R-002 | Google Drive quota exceeded | Medium | High | Event-specific Google accounts, storage monitoring, upgrade option | Operations |
| R-003 | Backend server failure | Medium | High | Automated backups, rapid redeployment, health monitoring | DevOps |
| R-004 | Database corruption | Low | High | Daily backups, migration testing, transactional integrity | DevOps |
| R-005 | Authentication failure | Low | High | Google OAuth fallback handling and monitoring | Backend |
| R-006 | High photo upload traffic | High | Medium | Image compression, upload queue, scalable backend | Backend |
| R-007 | Security breach | Low | High | HTTPS, JWT, RBAC, input validation, security reviews | Security |
| R-008 | Data loss | Low | High | Google Drive storage, database backups, recovery plan | DevOps |
| R-009 | Budget overrun | Medium | Medium | Use free tiers and open-source technologies | Project Owner |
| R-010 | Production bug during wedding | Medium | High | UAT, rehearsal event, rollback plan | Engineering |
| R-011 | Notification provider failure | Medium | Medium | Multi-channel notifications (Email, WhatsApp, Push) | Backend |
| R-012 | Browser incompatibility | Low | Medium | Cross-browser testing | QA |
| R-013 | Founder timeline delay | Medium | High | MVP prioritization and weekly milestones | Project Owner |
| R-014 | Raspberry Pi hardware failure | Low | Medium | Repository backups and cloud deployment option | DevOps |
| R-015 | Third-party API changes | Medium | Medium | Provider abstraction layer and version monitoring | Engineering |

---

# Highest Priority Risks

## R-001 Internet Failure During Event

Impact

Guests cannot immediately upload photos.

Mitigation

- Offline upload queue
- Local browser storage
- Automatic synchronization
- Retry mechanism

Residual Risk

Low

---

## R-002 Storage Exhaustion

Impact

Guests cannot upload media.

Mitigation

- Dedicated Google account per event
- Storage usage monitoring
- Upgrade path for larger events

Residual Risk

Low

---

## R-003 Live Production Failure

Impact

Entire platform unavailable during the event.

Mitigation

- Health monitoring
- Backup deployment
- Rollback procedure
- Final production rehearsal

Residual Risk

Medium

---

# Business Risks

- Limited marketing budget
- Slow customer adoption
- Pricing validation uncertainty
- Competition from existing event platforms

Mitigation

- Founder event validation
- Referral strategy
- Incremental feature releases

---

# Technical Risks

- API provider changes
- Infrastructure failures
- Database performance issues
- Upload synchronization conflicts
- Browser compatibility issues

Mitigation

- Modular architecture
- Automated testing
- Monitoring
- CI/CD pipeline

---

# Operational Risks

- Incorrect payment verification
- Customer support overload
- Misconfigured event settings
- Manual operational errors

Mitigation

- Admin dashboard
- Audit logging
- Configuration validation
- Operational documentation

---

# Security Risks

- Unauthorized access
- JWT compromise
- Brute-force attacks
- File upload abuse
- Data exposure

Mitigation

- HTTPS
- JWT expiration
- Role-based authorization
- File validation
- Rate limiting
- Security headers

---

# Disaster Recovery

Recovery Priorities

1. Backend
2. Database
3. Authentication
4. Google Drive connectivity
5. Notification services

Recovery Targets

| Metric | Target |
|---------|--------|
| RTO | < 2 Hours |
| RPO | < 24 Hours |

---

# Risk Monitoring

The following metrics should be monitored continuously:

- API availability
- Upload success rate
- Storage utilization
- Authentication failures
- Notification delivery
- System response time
- Database health
- Backup status

---

# Risk Review Schedule

| Activity | Frequency |
|-----------|-----------|
| Development Review | Weekly |
| Sprint Review | End of Sprint |
| Production Readiness Review | Before Release |
| Security Review | Quarterly |
| Disaster Recovery Test | Every 6 Months |

---

# References

- PI-04 Non-Functional Requirements
- PI-05 Software Architecture
- PI-10 Deployment & DevOps
- PI-11 Testing Strategy
- PI-12 Project Roadmap
- PI-16 Operations Manual

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|---------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Risk Register |
