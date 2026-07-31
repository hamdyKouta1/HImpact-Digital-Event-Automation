package com.himpact.service;

import com.himpact.dto.media.MediaFileResponse;
import com.himpact.entity.*;
import com.himpact.exception.BusinessRuleException;
import com.himpact.repository.*;
import com.himpact.storage.StorageProvider;
import com.himpact.storage.UploadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MediaService Unit Tests")
class MediaServiceTest {

    @Mock
    private MediaFileRepository mediaFileRepository;
    @Mock
    private MediaSyncRepository mediaSyncRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private StorageProvider storageProvider;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MediaService mediaService;

    private Event event;
    private Guest guest;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        event = Event.builder().id(eventId).slug("royal-wedding").status(EventStatus.PUBLISHED).build();
        guest = Guest.builder()
                .id(UUID.randomUUID())
                .event(event)
                .invitationCode("ABC12345")
                .uploadLimit(5)
                .uploadedCount(0)
                .build();
    }

    @Test
    @DisplayName("should upload photo successfully and emit MediaUploadedEvent")
    void shouldUploadMediaSuccessfully() {
        MockMultipartFile file = new MockMultipartFile("file", "wedding.jpg", "image/jpeg", "fake-image".getBytes());
        UploadResult uploadResult = UploadResult.success("events/royal-wedding/guests/ABC12345/uuid_wedding.jpg", "uuid_wedding.jpg", "LOCAL", file.getSize(), "image/jpeg");

        when(eventRepository.findByIdAndIsDeletedFalse(eventId)).thenReturn(Optional.of(event));
        when(guestRepository.findByInvitationCodeAndIsDeletedFalse("ABC12345")).thenReturn(Optional.of(guest));
        when(storageProvider.upload(any(), any(), any(), any())).thenReturn(uploadResult);
        when(storageProvider.getProviderName()).thenReturn("LOCAL");
        when(mediaFileRepository.save(any(MediaFile.class))).thenAnswer(i -> {
            MediaFile m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        MediaFileResponse response = mediaService.uploadMedia(eventId, "ABC12345", file, "client-uuid-1");

        assertThat(response).isNotNull();
        assertThat(response.originalFilename()).isEqualTo("wedding.jpg");
        assertThat(guest.getUploadedCount()).isEqualTo(1);

        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @DisplayName("should throw BusinessRuleException when guest quota is exceeded")
    void shouldThrowWhenQuotaExceeded() {
        guest.setUploadedCount(5); // Equal to upload limit

        MockMultipartFile file = new MockMultipartFile("file", "wedding.jpg", "image/jpeg", "fake-image".getBytes());

        when(eventRepository.findByIdAndIsDeletedFalse(eventId)).thenReturn(Optional.of(event));
        when(guestRepository.findByInvitationCodeAndIsDeletedFalse("ABC12345")).thenReturn(Optional.of(guest));

        assertThatThrownBy(() -> mediaService.uploadMedia(eventId, "ABC12345", file, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Upload quota exceeded");

        verifyNoInteractions(storageProvider);
    }

    @Test
    @DisplayName("should perform rollback file cleanup if database save fails (Transaction Safety)")
    void shouldCleanupStorageFileIfDbSaveFails() {
        MockMultipartFile file = new MockMultipartFile("file", "wedding.jpg", "image/jpeg", "fake-image".getBytes());
        UploadResult uploadResult = UploadResult.success("events/royal-wedding/guests/ABC12345/uuid_wedding.jpg", "uuid_wedding.jpg", "LOCAL", file.getSize(), "image/jpeg");

        when(eventRepository.findByIdAndIsDeletedFalse(eventId)).thenReturn(Optional.of(event));
        when(guestRepository.findByInvitationCodeAndIsDeletedFalse("ABC12345")).thenReturn(Optional.of(guest));
        when(storageProvider.upload(any(), any(), any(), any())).thenReturn(uploadResult);
        when(storageProvider.getProviderName()).thenReturn("LOCAL");
        when(mediaFileRepository.save(any(MediaFile.class))).thenThrow(new RuntimeException("Database connection dead"));

        assertThatThrownBy(() -> mediaService.uploadMedia(eventId, "ABC12345", file, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database connection dead");

        // Verify storage file cleanup was executed (Rollback Cleanup)
        verify(storageProvider, times(1)).delete(uploadResult.storagePath());
    }
}
