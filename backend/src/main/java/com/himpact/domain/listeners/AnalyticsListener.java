package com.himpact.domain.listeners;

import com.himpact.domain.events.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Async domain event listener for platform analytics & activity timelines.
 * Uses @TransactionalEventListener(phase = AFTER_COMMIT) to record metrics after DB commit.
 */
@Slf4j
@Component
public class AnalyticsListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventCreated(EventCreatedEvent event) {
        log.info("[AFTER_COMMIT AnalyticsListener] Recorded event creation: [{}] type: [{}]", event.title(), event.eventType());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvitationViewed(InvitationViewedEvent event) {
        log.info("[AFTER_COMMIT AnalyticsListener] Recorded invitation view for guest [{}]", event.guestId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMediaUploaded(MediaUploadedEvent event) {
        log.info("[AFTER_COMMIT AnalyticsListener] Recorded media upload for event [{}] size: {} bytes", event.eventId(), event.fileSize());
    }
}
