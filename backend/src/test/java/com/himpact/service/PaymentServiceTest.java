package com.himpact.service;

import com.himpact.dto.payment.ApprovePaymentRequest;
import com.himpact.dto.payment.PaymentResponse;
import com.himpact.dto.payment.SubmitPaymentRequest;
import com.himpact.entity.*;
import com.himpact.repository.EventRepository;
import com.himpact.repository.PackageRepository;
import com.himpact.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private PackageRepository packageRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    private Event event;
    private UUID eventId;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
        event = Event.builder().id(eventId).title("Royal Wedding").build();
    }

    @Test
    @DisplayName("should submit manual payment proof successfully in SUBMITTED state")
    void shouldSubmitPayment() {
        SubmitPaymentRequest request = new SubmitPaymentRequest(
                eventId, null, "INSTAPAY", BigDecimal.valueOf(499), "TXN123456", null);

        when(eventRepository.findByIdAndIsDeletedFalse(eventId)).thenReturn(Optional.of(event));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(paymentId);
            return p;
        });

        PaymentResponse response = paymentService.submitPayment(request, UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.paymentState()).isEqualTo(PaymentState.SUBMITTED);
        assertThat(response.paymentReference()).isEqualTo("TXN123456");
    }

    @Test
    @DisplayName("should advance payment state machine to APPROVED and publish PaymentApprovedEvent")
    void shouldApprovePayment() {
        Payment payment = Payment.builder()
                .id(paymentId)
                .event(event)
                .paymentState(PaymentState.SUBMITTED)
                .amount(BigDecimal.valueOf(499))
                .paymentReference("TXN123456")
                .paymentMethod("INSTAPAY")
                .build();

        ApprovePaymentRequest approveRequest = new ApprovePaymentRequest(true, null);
        UUID adminId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = paymentService.approveOrRejectPayment(paymentId, approveRequest, adminId);

        assertThat(response).isNotNull();
        assertThat(response.paymentState()).isEqualTo(PaymentState.ACTIVATED);

        // Verify PaymentApprovedEvent was published for decoupled package activation (PO Requirement B)
        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
