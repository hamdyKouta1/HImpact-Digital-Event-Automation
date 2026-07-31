package com.himpact.service;

import com.himpact.entity.Notification;
import com.himpact.notification.provider.NotificationProvider;
import com.himpact.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationProvider emailProvider;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        when(emailProvider.getProviderType()).thenReturn("EMAIL");
        notificationService = new NotificationService(notificationRepository, Map.of("emailNotificationProvider", emailProvider));
    }

    @Test
    @DisplayName("should orchestrate email notification dispatch using EmailNotificationProvider")
    void shouldDispatchEmailNotification() {
        when(emailProvider.send(any())).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(UUID.randomUUID());
            return n;
        });

        Notification result = notificationService.dispatchNotification("EMAIL", "guest@example.com", "Invitation", "Your invitation link");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SENT");

        verify(emailProvider, times(1)).send(any());
    }
}
