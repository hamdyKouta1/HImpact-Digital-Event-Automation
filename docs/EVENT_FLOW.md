# Domain Event Pipeline & Asynchronous Execution Flow

## 1. Event-Driven Architecture Overview

The system utilizes Spring Framework's **in-memory event bus** (`ApplicationEventPublisher` and `@EventListener` / `@TransactionalEventListener`) to achieve clean separation of concerns and decouple database transactions from asynchronous side effects.

### Core Guarantees
1. **Transaction Integrity**: Primary HTTP request threads execute state-altering operations inside `@Transactional` database boundaries.
2. **Post-Commit Side Effects**: Listeners use `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` to guarantee that notifications, audit logging, analytics tracking, and storage sync execute **only after the database transaction has successfully committed**.
3. **Asynchronous Non-Blocking Execution**: Listeners are annotated with `@Async("taskExecutor")`, delegating execution to a managed thread pool (`AsyncConfig.java`) so that background operations never delay client HTTP responses.

---

## 2. Event Dispatch Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Admin User
    participant Controller as AdminController
    participant Service as PaymentService
    participant DB as PostgreSQL Database
    participant Bus as ApplicationEventPublisher
    participant Listener as PackageActivationListener
    participant ActService as PackageActivationService
    participant NotifListener as NotificationListener

    Admin->>Controller: POST /api/v1/admin/payments/{id}/review (approved=true)
    Controller->>Service: reviewPayment(paymentId, true)
    Service->>DB: UPDATE payments SET payment_state = 'APPROVED'
    Service->>Bus: publishEvent(PaymentApprovedEvent)
    Note over Bus: Event buffered in Spring Transaction Synchronization context
    Service->>DB: Commit DB Transaction
    DB-->>Service: Transaction Committed Successfully

    par Asynchronous AFTER_COMMIT Listeners (TaskExecutor Threads)
        Bus->>Listener: handlePaymentApproved(PaymentApprovedEvent)
        Listener->>ActService: activatePackageForEvent(eventId, packageId)
        ActService->>DB: UPDATE events SET guest_limit = ..., storage_limit_mb = ...
    and
        Bus->>NotifListener: handleEventPublished / Payment Notification
        NotifListener->>NotifListener: Send email receipt & activation notification
    end

    Service-->>Controller: PaymentResponse (State: APPROVED)
    Controller-->>Admin: 200 OK
```

---

## 3. Domain Event Catalog

| Domain Event Record | Triggering Service Method | Payload Attributes | Registered Listeners | Action Taken |
|---|---|---|---|---|
| `EventCreatedEvent` | `EventService.createEvent` | `eventId`, `ownerId`, `title`, `eventType`, `timestamp` | `AnalyticsListener`, `AuditListener` | Records platform event creation metrics and populates security audit logs. |
| `EventPublishedEvent` | `EventService.publishEvent` | `eventId`, `ownerId`, `slug`, `timestamp` | `StorageListener`, `AuditListener`, `NotificationListener` | Provisions Google Drive storage folder, audits status change, queues launch notifications. |
| `GuestAddedEvent` | `GuestService.addGuest` | `guestId`, `eventId`, `guestName`, `invitationCode` | `NotificationListener` | Prepares invitation link and queues SMS/Email invitation dispatch. |
| `InvitationViewedEvent` | `InvitationService.getInvitationBySlug` | `invitationId`, `guestId`, `timestamp` | `AnalyticsListener` | Updates real-time view counters and analytics conversion metrics. |
| `RSVPSubmittedEvent` | `RsvpService.submitRsvp` | `rsvpId`, `eventId`, `guestId`, `attendanceStatus` | `NotificationListener` | Triggers push/email notification to event owner reporting new guest response. |
| `CommentAddedEvent` | `CommentService.addComment` | `commentId`, `eventId`, `guestId` | `AnalyticsListener` | Updates event activity timeline and guest interaction counts. |
| `MediaUploadedEvent` | `MediaService.uploadMedia` | `mediaFileId`, `eventId`, `guestId`, `filename`, `fileSize` | `StorageListener`, `AnalyticsListener`, `AuditListener`, `NotificationListener` | Initiates Google Drive background cloud sync, updates storage metrics, notifies owner. |
| `PaymentApprovedEvent` | `PaymentService.reviewPayment` | `paymentId`, `eventId`, `packageId`, `adminUserId` | `PackageActivationListener` | Executes `PackageActivationService` to upgrade event quotas and limits automatically. |

---

## 4. Listener Thread Pool & Execution Policy (`AsyncConfig.java`)

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("HImpact-Async-");
        executor.initialize();
        return executor;
    }
}
```

- **Core Pool Size**: 5 concurrent worker threads.
- **Max Pool Size**: 20 worker threads for peak notification/upload bursts.
- **Queue Capacity**: 100 queued events before triggering task rejection policy.
- **Thread Prefix**: `HImpact-Async-` for logging clarity in MDC.
