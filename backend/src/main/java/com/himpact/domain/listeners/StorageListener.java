package com.himpact.domain.listeners;

import com.himpact.domain.events.EventPublishedEvent;
import com.himpact.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Async domain event listener for storage folder provisioning.
 * Uses @TransactionalEventListener(phase = AFTER_COMMIT).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StorageListener {

    @Qualifier("googleDriveStorageProvider")
    private final StorageProvider storageProvider;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEventPublished(EventPublishedEvent event) {
        log.info("[AFTER_COMMIT StorageListener] Provisioning storage directory for published event [{}] via provider [{}]",
                event.slug(), storageProvider.getProviderName());
    }
}
