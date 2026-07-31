package com.himpact.service;

import com.himpact.dto.guest.AddGuestRequest;
import com.himpact.dto.guest.GuestResponse;
import com.himpact.entity.Event;
import com.himpact.entity.Guest;
import com.himpact.entity.GuestStatus;
import com.himpact.repository.EventRepository;
import com.himpact.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GuestService Unit Tests")
class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GuestService guestService;

    private Event event;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        event = Event.builder()
                .id(eventId)
                .title("Test Event")
                .slug("test-event")
                .build();
    }

    @Test
    @DisplayName("should add guest successfully and generate unique invitation code")
    void shouldAddGuest() {
        AddGuestRequest request = new AddGuestRequest("Ahmed Mohamed", "+201012345678", "ahmed@example.com", 30);

        when(eventRepository.findByIdAndIsDeletedFalse(eventId)).thenReturn(Optional.of(event));
        when(guestRepository.existsByInvitationCode(any())).thenReturn(false);
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> {
            Guest g = inv.getArgument(0);
            g.setId(UUID.randomUUID());
            return g;
        });

        GuestResponse response = guestService.addGuest(eventId, request, UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo("Ahmed Mohamed");
        assertThat(response.invitationCode()).hasSize(8);
        assertThat(response.invitationUrl()).contains("test-event?code=");
        assertThat(response.status()).isEqualTo(GuestStatus.INVITED);

        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
