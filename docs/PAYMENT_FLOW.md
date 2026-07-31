# Payment Processing & Package Activation Flow

## 1. Monetization & Payment Architecture

The platform supports digital event package monetization (Free, Standard, VIP). Event owners can upgrade their event tier by submitting payment proof (Instapay / Vodafone Cash transaction receipts).

### Key Architectural Requirements
- **Decoupled Package Activation**: Payment approval and package activation are strictly decoupled. When an admin approves a payment, `PaymentService` emits a `PaymentApprovedEvent`.
- **Async Event Listener Execution**: `PackageActivationListener` receives `PaymentApprovedEvent` `AFTER_COMMIT` and invokes `PackageActivationService` to upgrade event guest limits, upload limits, and storage quotas automatically.
- **State Machine Integrity**: Payments transition through strict states (`SUBMITTED` -> `UNDER_REVIEW` -> `APPROVED` / `REJECTED` -> `ACTIVATED`).

---

## 2. Payment State Transition Diagram

```mermaid
stateDiagram-v2
    [*] --> SUBMITTED : Owner Submits Receipt
    SUBMITTED --> UNDER_REVIEW : Admin Opens Review Modal
    UNDER_REVIEW --> REJECTED : Admin Rejects (Reason Provided)
    UNDER_REVIEW --> APPROVED : Admin Approves Receipt
    APPROVED --> ACTIVATED : PaymentApprovedEvent Triggers PackageActivationService
    REJECTED --> [*]
    ACTIVATED --> [*]
```

---

## 3. End-to-End Payment & Activation Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Owner as Event Owner
    actor Admin as System Admin
    participant PCtrl as PaymentController
    participant ACtrl as AdminController
    participant PService as PaymentService
    participant DB as PostgreSQL DB
    participant Bus as ApplicationEventPublisher
    participant Listener as PackageActivationListener
    participant ActService as PackageActivationService
    participant NotifListener as NotificationListener

    Owner->>PCtrl: POST /api/v1/payments (eventId, packageId, amount, receiptImage)
    PCtrl->>PService: submitPayment(...)
    PService->>DB: Save Payment (State: SUBMITTED)
    DB-->>PService: Saved Payment
    PService-->>PCtrl: PaymentResponse (SUBMITTED)
    PCtrl-->>Owner: 201 Created

    Note over Admin, ACtrl: Admin Review Workflow
    Admin->>ACtrl: GET /api/v1/admin/payments/pending
    ACtrl-->>Admin: List of Pending Payments
    Admin->>ACtrl: POST /api/v1/admin/payments/{id}/review (approved=true)
    ACtrl->>PService: reviewPayment(paymentId, approved=true)
    
    PService->>DB: UPDATE payments SET payment_state = 'APPROVED', approved_by = ...
    PService->>Bus: publishEvent(PaymentApprovedEvent)
    PService->>DB: UPDATE payments SET payment_state = 'ACTIVATED'
    PService->>DB: Transaction Commit

    par Async AFTER_COMMIT Package Activation
        Bus->>Listener: handlePaymentApproved(PaymentApprovedEvent)
        Listener->>ActService: activatePackageForEvent(eventId, packageId)
        ActService->>DB: UPDATE events SET guest_limit = package.guestLimit, storage_limit_mb = package.storageLimitMb
        ActService-->>Listener: Package Activated Successfully
    and Async AFTER_COMMIT Notifications
        Bus->>NotifListener: handlePaymentApproved(...)
        NotifListener->>NotifListener: Send email notification to Event Owner
    end

    PService-->>ACtrl: PaymentResponse (ACTIVATED)
    ACtrl-->>Admin: 200 OK
```

---

## 4. Package Quotas & Feature Tiers

| Package Tier | Guest Limit | Storage Limit (MB) | Media Upload Quota / Guest | Theme Options |
|---|---|---|---|---|
| **Free** | 50 | 500 MB | 5 photos | Default Theme |
| **Standard** | 200 | 5,000 MB (5 GB) | 20 photos / 2 videos | Standard Themes |
| **VIP** | 1,000 | 50,000 MB (50 GB) | Unlimited | Premium Customized Themes |
