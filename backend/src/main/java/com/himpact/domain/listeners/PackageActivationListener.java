package com.himpact.domain.listeners;

import com.himpact.domain.events.PaymentApprovedEvent;
import com.himpact.service.PackageActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Async domain event listener for package activation.
 * Listens to PaymentApprovedEvent and triggers PackageActivationService AFTER_COMMIT.
 *
 * See: PO Sprint 5 Workstream B Architecture Requirement (Decoupled Activation)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PackageActivationListener {

    private final PackageActivationService packageActivationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        log.info("[AFTER_COMMIT PackageActivationListener] Activating package [{}] for event [{}]",
                event.packageId(), event.eventId());
        packageActivationService.activatePackageForEvent(event.eventId(), event.packageId());
    }
}
