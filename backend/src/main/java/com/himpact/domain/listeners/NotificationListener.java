package com.himpact.domain.listeners;

import com.himpact.domain.events.EventPublishedEvent;
import com.himpact.domain.events.GuestAddedEvent;
import com.himpact.domain.events.MediaUploadedEvent;
import com.himpact.domain.events.RSVPSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Async domain event listener for notifications.
 * Uses @TransactionalEventListener(phase = AFTER_COMMIT) to guarantee side-effects
 * execute ONLY after successful database transaction commit per PO Requirement.
 */
@Slf4j
@Component
public class NotificationListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventPublished(EventPublishedEvent event) {
        log.info("[AFTER_COMMIT NotificationListener] Queueing publication notifications for event [{}]", event.eventId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGuestAdded(GuestAddedEvent event) {
        log.info("[AFTER_COMMIT NotificationListener] Queueing invitation link for guest [{}]", event.guestName());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRSVPSubmitted(RSVPSubmittedEvent event) {
        log.info("[AFTER_COMMIT NotificationListener] Notifying owner of RSVP by guest [{}] -> status: [{}]",
                event.guestId(), event.attendanceStatus());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMediaUploaded(MediaUploadedEvent event) {
        log.info("[AFTER_COMMIT NotificationListener] Notifying event owner of new photo upload [{}] by guest [{}]",
                event.originalFilename(), event.guestId());
    }
}
