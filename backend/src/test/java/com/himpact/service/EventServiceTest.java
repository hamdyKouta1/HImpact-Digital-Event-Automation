package com.himpact.service;

import com.himpact.dto.event.CreateEventRequest;
import com.himpact.dto.event.EventResponse;
import com.himpact.entity.*;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.EventRepository;
import com.himpact.repository.PackageRepository;
import com.himpact.repository.ThemeRepository;
import com.himpact.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EventService eventService;

    private User owner;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        owner = User.builder()
                .id(ownerId)
                .email("owner@himpact.app")
                .fullName("Test Owner")
                .role(UserRole.GUEST)
                .build();
    }

    @Test
    @DisplayName("should create event successfully and emit EventCreatedEvent")
    void shouldCreateEvent() {
        CreateEventRequest request = new CreateEventRequest(
                "My Royal Wedding",
                EventType.WEDDING,
                "Bride",
                "Groom",
                "Description",
                "Grand Hotel",
                "Address",
                "https://maps.google.com",
                LocalDate.now().plusDays(30),
                null,
                null,
                null,
                null,
                null
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(eventRepository.existsBySlug(any())).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        EventResponse response = eventService.createEvent(ownerId, request);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("My Royal Wedding");
        assertThat(response.status()).isEqualTo(EventStatus.DRAFT);
        assertThat(response.slug()).isEqualTo("my-royal-wedding");

        verify(eventPublisher, times(1)).publishEvent(any());
        assertThat(owner.getRole()).isEqualTo(UserRole.OWNER); // User promoted to OWNER
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException if user does not exist")
    void shouldThrowIfUserNotFound() {
        CreateEventRequest request = new CreateEventRequest(
                "Title", EventType.WEDDING, null, null, null, "Venue", null, null,
                LocalDate.now().plusDays(10), null, null, null, null, null
        );

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.createEvent(ownerId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
