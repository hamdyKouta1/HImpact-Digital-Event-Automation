package com.himpact.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record SubmitPaymentRequest(
        @NotNull(message = "Event ID is required")
        UUID eventId,

        UUID packageId,

        @NotBlank(message = "Payment method is required")
        String paymentMethod, // INSTAPAY, VODAFONE_CASH

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "Payment reference / transaction ID is required")
        String paymentReference,

        String receiptImageUrl
) {}
