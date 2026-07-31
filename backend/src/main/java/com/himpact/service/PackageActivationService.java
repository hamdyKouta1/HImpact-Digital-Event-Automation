package com.himpact.service;

import com.himpact.entity.Event;
import com.himpact.entity.EventStatus;
import com.himpact.entity.PackageEntity;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.EventRepository;
import com.himpact.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for activating commercial packages on events.
 * Invoked by PackageActivationListener in response to PaymentApprovedEvent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PackageActivationService {

    private final EventRepository eventRepository;
    private final PackageRepository packageRepository;

    @Transactional
    public void activatePackageForEvent(UUID eventId, UUID packageId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (packageId != null) {
            PackageEntity pkg = packageRepository.findById(packageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Package", "id", packageId));
            event.setPackageEntity(pkg);
        }

        // Publish event if in draft
        if (event.getStatus() == EventStatus.DRAFT) {
            event.setStatus(EventStatus.PUBLISHED);
        }

        eventRepository.save(event);
        log.info("Activated package [{}] for event [{}]", packageId, eventId);
    }
}
