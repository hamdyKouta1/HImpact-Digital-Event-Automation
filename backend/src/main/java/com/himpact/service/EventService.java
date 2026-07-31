package com.himpact.service;

import com.himpact.dto.event.CreateEventRequest;
import com.himpact.dto.event.EventResponse;
import com.himpact.dto.event.EventSummaryResponse;
import com.himpact.dto.event.UpdateEventRequest;
import com.himpact.entity.*;
import com.himpact.domain.events.EventCreatedEvent;
import com.himpact.domain.events.EventPublishedEvent;
import com.himpact.exception.BusinessRuleException;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.EventRepository;
import com.himpact.repository.PackageRepository;
import com.himpact.repository.ThemeRepository;
import com.himpact.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.text.Normalizer;
import java.util.Locale;

/**
 * Business logic service for Event Management.
 * Emits Spring Application Events for loose module coupling.
 *
 * See: project-index/03_Functional_Requirements.md — FR-02 Event Management
 * See: project-index/05_Software_Architecture.md — Business Layer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PackageRepository packageRepository;
    private final ThemeRepository themeRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new event in DRAFT status for the authenticated user.
     */
    @Transactional
    public EventResponse createEvent(UUID ownerId, CreateEventRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        // Assign default package if not specified
        PackageEntity packageEntity = null;
        if (request.packageId() != null) {
            packageEntity = packageRepository.findById(request.packageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Package", "id", request.packageId()));
        } else {
            List<PackageEntity> defaults = packageRepository.findByActiveTrueOrderByDisplayOrderAsc();
            if (!defaults.isEmpty()) {
                packageEntity = defaults.getFirst();
            }
        }

        // Assign default theme if not specified
        ThemeEntity themeEntity = null;
        if (request.themeId() != null) {
            themeEntity = themeRepository.findById(request.themeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", request.themeId()));
        } else {
            List<ThemeEntity> defaults = themeRepository.findByActiveTrue();
            if (!defaults.isEmpty()) {
                themeEntity = defaults.getFirst();
            }
        }

        String slug = generateUniqueSlug(request.title());

        Event event = Event.builder()
                .owner(owner)
                .title(request.title())
                .eventType(request.eventType())
                .brideName(request.brideName())
                .groomName(request.groomName())
                .description(request.description())
                .venueName(request.venueName())
                .venueAddress(request.venueAddress())
                .googleMapsUrl(request.googleMapsUrl())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .coverImage(request.coverImage())
                .packageEntity(packageEntity)
                .theme(themeEntity)
                .status(EventStatus.DRAFT)
                .slug(slug)
                .createdBy(ownerId)
                .build();

        Event saved = eventRepository.save(event);
        log.info("Created event [{}] for owner [{}]", saved.getId(), owner.getEmail());

        // Update user role to OWNER if currently GUEST
        if (owner.getRole() == UserRole.GUEST) {
            owner.setRole(UserRole.OWNER);
            userRepository.save(owner);
        }

        // Emit domain event for loose coupling
        eventPublisher.publishEvent(new EventCreatedEvent(
                saved.getId(), owner.getId(), saved.getTitle(), saved.getEventType().name()));

        return mapToResponse(saved);
    }

    /**
     * Get event details by ID.
     */
    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID eventId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        return mapToResponse(event);
    }

    /**
     * Get event details by public slug.
     */
    @Transactional(readOnly = true)
    public EventResponse getEventBySlug(String slug) {
        Event event = eventRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "slug", slug));
        return mapToResponse(event);
    }

    /**
     * Get all active events owned by a specific user.
     */
    @Transactional(readOnly = true)
    public List<EventSummaryResponse> getMyEvents(UUID ownerId) {
        return eventRepository.findByOwnerIdAndIsDeletedFalse(ownerId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    /**
     * Update event details.
     */
    @Transactional
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest request) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (event.getStatus() == EventStatus.ARCHIVED) {
            throw new BusinessRuleException("Cannot update an archived event.");
        }

        if (request.title() != null) event.setTitle(request.title());
        if (request.eventType() != null) event.setEventType(request.eventType());
        if (request.brideName() != null) event.setBrideName(request.brideName());
        if (request.groomName() != null) event.setGroomName(request.groomName());
        if (request.description() != null) event.setDescription(request.description());
        if (request.venueName() != null) event.setVenueName(request.venueName());
        if (request.venueAddress() != null) event.setVenueAddress(request.venueAddress());
        if (request.googleMapsUrl() != null) event.setGoogleMapsUrl(request.googleMapsUrl());
        if (request.eventDate() != null) event.setEventDate(request.eventDate());
        if (request.startTime() != null) event.setStartTime(request.startTime());
        if (request.endTime() != null) event.setEndTime(request.endTime());
        if (request.coverImage() != null) event.setCoverImage(request.coverImage());

        if (request.themeId() != null) {
            ThemeEntity theme = themeRepository.findById(request.themeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", request.themeId()));
            event.setTheme(theme);
        }

        Event updated = eventRepository.save(event);
        log.info("Updated event [{}]", eventId);
        return mapToResponse(updated);
    }

    /**
     * Publish an event. Transitions status from DRAFT -> PUBLISHED.
     */
    @Transactional
    public EventResponse publishEvent(UUID eventId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (event.getStatus() == EventStatus.PUBLISHED) {
            return mapToResponse(event);
        }

        event.setStatus(EventStatus.PUBLISHED);
        Event published = eventRepository.save(event);
        log.info("Published event [{}]", eventId);

        // Emit domain event
        eventPublisher.publishEvent(new EventPublishedEvent(
                published.getId(), published.getOwner().getId(), published.getTitle(), published.getSlug()));

        return mapToResponse(published);
    }

    /**
     * Soft-delete an event.
     */
    @Transactional
    public void deleteEvent(UUID eventId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (event.getStatus() == EventStatus.PUBLISHED) {
            throw new BusinessRuleException("Published events cannot be deleted. Archive the event instead.");
        }

        event.setDeleted(true);
        eventRepository.save(event);
        log.info("Soft-deleted event [{}]", eventId);
    }

    // ── Private Mapping & Slug Utilities ──────────────────────────────────────

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getOwner().getId(),
                event.getOwner().getFullName(),
                event.getTitle(),
                event.getEventType(),
                event.getBrideName(),
                event.getGroomName(),
                event.getDescription(),
                event.getVenueName(),
                event.getVenueAddress(),
                event.getGoogleMapsUrl(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCoverImage(),
                event.getPackageEntity() != null ? event.getPackageEntity().getId() : null,
                event.getPackageEntity() != null ? event.getPackageEntity().getPackageName() : null,
                event.getTheme() != null ? event.getTheme().getId() : null,
                event.getTheme() != null ? event.getTheme().getThemeName() : null,
                event.getStatus(),
                event.getSlug(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }

    private EventSummaryResponse mapToSummary(Event event) {
        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getEventType(),
                event.getEventDate(),
                event.getVenueName(),
                event.getCoverImage(),
                event.getStatus(),
                event.getSlug(),
                0, // Guest count populated dynamically
                0  // Upload count populated dynamically
        );
    }

    private String generateUniqueSlug(String title) {
        String base = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (base.isBlank()) {
            base = "event";
        }

        String slug = base;
        int count = 2;
        while (eventRepository.existsBySlug(slug)) {
            slug = base + "-" + count++;
        }
        return slug;
    }
}
