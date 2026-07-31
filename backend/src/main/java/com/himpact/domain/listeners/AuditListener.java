package com.himpact.domain.listeners;

import com.himpact.domain.events.EventCreatedEvent;
import com.himpact.domain.events.EventPublishedEvent;
import com.himpact.domain.events.MediaUploadedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Async domain event listener for security and activity audit logging.
 * Uses @TransactionalEventListener(phase = AFTER_COMMIT).
 */
@Slf4j
@Component
public class AuditListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventCreated(EventCreatedEvent event) {
        log.info("[AFTER_COMMIT AuditListener] AUDIT: Event created [{}] by user [{}] at {}",
                event.eventId(), event.ownerId(), event.timestamp());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventPublished(EventPublishedEvent event) {
        log.info("[AFTER_COMMIT AuditListener] AUDIT: Event published [{}] by user [{}] at {}",
                event.eventId(), event.ownerId(), event.timestamp());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMediaUploaded(MediaUploadedEvent event) {
        log.info("[AFTER_COMMIT AuditListener] AUDIT: Media uploaded [{}] file: [{}] for event [{}]",
                event.mediaFileId(), event.originalFilename(), event.eventId());
    }
}
