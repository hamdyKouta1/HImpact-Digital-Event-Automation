# 16 - Operations Manual

**Document ID:** PI-16

**Version:** 1.0.0

**Status:** Approved

**Owner:** HImpact

**Last Updated:** 2026-07-26

---

# Purpose

This document defines the operational procedures required to deploy, maintain, monitor, support, and recover the HImpact Digital Event Automation Platform.

It serves as the primary handbook for developers, administrators, and support personnel.

---

# Operational Objectives

- Ensure platform availability
- Maintain system performance
- Protect customer data
- Minimize downtime
- Standardize operational procedures
- Enable rapid incident response

---

# System Overview

The platform consists of:

- React Frontend
- Spring Boot Backend
- PostgreSQL Database
- Google Drive Storage
- GitHub Repository
- GitHub Actions CI/CD
- Raspberry Pi Development Server

---

# Operational Roles

## Project Owner

Responsibilities

- Product decisions
- Release approval
- Customer communication
- Roadmap management

---

## System Administrator

Responsibilities

- Server management
- Deployment
- Database maintenance
- Backup verification
- Security updates

---

## Developer

Responsibilities

- Feature development
- Bug fixing
- Documentation updates
- Performance improvements

---

## Support

Responsibilities

- Customer onboarding
- Theme customization requests
- Payment verification
- Technical assistance

---

# Daily Operations Checklist

Verify

- Backend is running
- Database is online
- API health endpoint responds
- Google Drive connectivity
- Storage usage
- Failed uploads
- Notification queue
- Application logs

---

# Weekly Maintenance

Tasks

- Review system logs
- Verify database backups
- Clean temporary files
- Update dependencies
- Review GitHub Actions
- Validate SSL certificates
- Review security alerts

---

# Monthly Maintenance

Tasks

- Review storage consumption
- Archive completed events
- Review user feedback
- Database optimization
- Security audit
- Performance review
- Cost analysis

---

# Event Lifecycle

```
Create Event

?

Payment Approved

?

Configure Theme

?

Import Guests

?

Publish Invitation

?

Guest Registration

?

Event Day

?

Media Upload

?

Gallery Delivery

?

Archive Event
```

---

# Production Deployment

Deployment Steps

1. Merge approved code into `main`
2. Execute GitHub Actions pipeline
3. Verify deployment
4. Run smoke tests
5. Confirm health endpoint
6. Notify stakeholders

Deployment must not proceed if automated tests fail.

---

# Monitoring

Monitor continuously

- CPU
- Memory
- Disk Usage
- Database Health
- API Response Time
- Authentication Errors
- Upload Queue
- Failed Uploads
- Google Drive Status

---

# Health Check Endpoints

Application

```
/actuator/health
```

Metrics

```
/actuator/metrics
```

Information

```
/actuator/info
```

---

# Backup Procedure

Database

- Daily backup
- 7-day retention

Repository

- GitHub repository

Media

- Stored in customer Google Drive

Configuration

- Version controlled

Backup verification must be performed weekly.

---

# Restore Procedure

1. Restore PostgreSQL backup
2. Deploy backend
3. Restore environment variables
4. Verify Google Drive integration
5. Execute smoke tests
6. Reopen platform

---

# Incident Management

Severity Levels

## Critical

Examples

- Platform unavailable
- Database failure
- Authentication failure

Response Time

Immediate

---

## High

Examples

- Upload failures
- Payment issues
- Notification failure

Response Time

Within 2 hours

---

## Medium

Examples

- Minor feature malfunction
- UI issue
- Performance degradation

Response Time

Within 1 business day

---

## Low

Examples

- Cosmetic issue
- Documentation correction

Response Time

Next planned release

---

# Customer Support Workflow

```
Customer Request

?

Ticket Created

?

Issue Classification

?

Resolution

?

Customer Confirmation

?

Ticket Closed
```

Support Categories

- Theme customization
- Invitation changes
- Storage issues
- Upload assistance
- Payment questions
- Feature requests

---

# Event Day Operations

Before Event

- Verify deployment
- Verify storage availability
- Confirm notification schedule
- Confirm database backup
- Validate upload functionality

During Event

- Monitor API health
- Monitor upload queue
- Monitor storage usage
- Watch application logs
- Respond to incidents immediately

After Event

- Verify gallery completeness
- Confirm uploads finished
- Notify customer
- Archive logs
- Prepare event summary

---

# Security Operations

Monthly

- Rotate credentials if required
- Review access permissions
- Update dependencies
- Verify HTTPS certificates
- Review audit logs

Never

- Share credentials
- Store secrets in Git
- Disable authentication
- Expose customer data

---

# Operational KPIs

| KPI | Target |
|------|--------|
| Platform Availability | =99% |
| API Response Time | <500 ms |
| Upload Success Rate | >98% |
| Failed Deployments | <2% |
| Backup Success | 100% |
| Critical Incident Resolution | <2 Hours |

---

# Documentation Maintenance

The Operations Manual shall be updated whenever:

- Infrastructure changes
- Deployment process changes
- Operational procedures change
- New monitoring tools are introduced
- Disaster recovery procedures change

---

# Future Enhancements

- Automated health dashboards
- Self-healing deployments
- AI-powered incident detection
- Predictive monitoring
- Automated customer notifications
- Multi-region operations

---

# References

- PI-05 Software Architecture
- PI-10 Deployment & DevOps
- PI-11 Testing Strategy
- PI-13 Risk Register
- PI-17 Release Notes

---

# Revision History

| Version | Date | Author | Description |
|----------|------------|----------------------|--------------------------|
| 1.0.0 | 2026-07-26 | Hamdy Mohamed Kouta | Initial Operations Manual |
