package com.himpact.dto.payment;

import com.himpact.entity.PaymentState;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID eventId,
        String eventTitle,
        UUID packageId,
        String packageName,
        String paymentMethod,
        BigDecimal amount,
        String currency,
        String paymentReference,
        String receiptImageUrl,
        PaymentState paymentState,
        String rejectionReason,
        Instant createdAt
) {}
