package com.himpact.service;

import com.himpact.dto.invitation.PublicInvitationResponse;
import com.himpact.entity.*;
import com.himpact.exception.AuthenticationException;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.*;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvitationService Unit Tests")
class InvitationServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private RsvpRepository rsvpRepository;
    @Mock
    private QrCodeService qrCodeService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InvitationService invitationService;

    private Event event;
    private Guest guest;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        event = Event.builder()
                .id(eventId)
                .title("Royal Wedding")
                .slug("royal-wedding")
                .status(EventStatus.PUBLISHED)
                .eventDate(LocalDate.now().plusDays(30))
                .build();

        guest = Guest.builder()
                .id(UUID.randomUUID())
                .event(event)
                .fullName("Sara Mohamed")
                .invitationCode("ABC12345")
                .status(GuestStatus.INVITED)
                .build();
    }

    @Test
    @DisplayName("should fetch public invitation with valid slug and code")
    void shouldFetchPublicInvitation() {
        when(eventRepository.findBySlugAndIsDeletedFalse("royal-wedding")).thenReturn(Optional.of(event));
        when(guestRepository.findByInvitationCodeAndIsDeletedFalse("ABC12345")).thenReturn(Optional.of(guest));
        when(invitationRepository.findByGuestId(guest.getId())).thenReturn(Optional.empty());
        when(qrCodeService.generateQrCodeBase64(any())).thenReturn("data:image/png;base64,stub");
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(i -> i.getArgument(0));

        PublicInvitationResponse response = invitationService.getPublicInvitation("royal-wedding", "ABC12345");

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("Royal Wedding");
        assertThat(response.guestName()).isEqualTo("Sara Mohamed");
        assertThat(response.qrCodeDataUrl()).isEqualTo("data:image/png;base64,stub");
    }

    @Test
    @DisplayName("should throw AuthenticationException if guest belongs to different event (Security Check 10)")
    void shouldThrowIfGuestBelongsToDifferentEvent() {
        Event otherEvent = Event.builder().id(UUID.randomUUID()).slug("other-event").build();
        guest.setEvent(otherEvent);

        when(eventRepository.findBySlugAndIsDeletedFalse("royal-wedding")).thenReturn(Optional.of(event));
        when(guestRepository.findByInvitationCodeAndIsDeletedFalse("ABC12345")).thenReturn(Optional.of(guest));

        assertThatThrownBy(() -> invitationService.getPublicInvitation("royal-wedding", "ABC12345"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid invitation link");
    }
}
