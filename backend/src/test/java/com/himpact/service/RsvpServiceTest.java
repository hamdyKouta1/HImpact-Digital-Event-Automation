package com.himpact.service;

import com.himpact.dto.rsvp.RsvpResponse;
import com.himpact.dto.rsvp.SubmitRsvpRequest;
import com.himpact.entity.*;
import com.himpact.repository.EventRepository;
import com.himpact.repository.GuestRepository;
import com.himpact.repository.RsvpRepository;
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
@DisplayName("RsvpService Unit Tests")
class RsvpServiceTest {

    @Mock
    private RsvpRepository rsvpRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RsvpService rsvpService;

    private Event event;
    private Guest guest;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        event = Event.builder().id(eventId).title("Royal Wedding").build();
        guest = Guest.builder().id(UUID.randomUUID()).event(event).invitationCode("ABC12345").fullName("Guest Name").build();
    }

    @Test
    @DisplayName("should submit RSVP response successfully and update guest status")
    void shouldSubmitRsvp() {
        SubmitRsvpRequest request = new SubmitRsvpRequest("ABC12345", AttendanceStatus.ACCEPTED, 2, "Vegetarian meal");

        when(eventRepository.findByIdAndIsDeletedFalse(eventId)).thenReturn(Optional.of(event));
        when(guestRepository.findByInvitationCodeAndIsDeletedFalse("ABC12345")).thenReturn(Optional.of(guest));
        when(rsvpRepository.findByGuestId(guest.getId())).thenReturn(Optional.empty());
        when(rsvpRepository.save(any(Rsvp.class))).thenAnswer(i -> i.getArgument(0));

        RsvpResponse response = rsvpService.submitRsvp(eventId, request);

        assertThat(response).isNotNull();
        assertThat(response.attendanceStatus()).isEqualTo(AttendanceStatus.ACCEPTED);
        assertThat(response.attendeeCount()).isEqualTo(2);
        assertThat(guest.getStatus()).isEqualTo(GuestStatus.REGISTERED);

        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
