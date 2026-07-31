package com.himpact.dto.payment;

import jakarta.validation.constraints.NotNull;

public record ApprovePaymentRequest(
        @NotNull(message = "Approval status is required")
        Boolean approved,

        String rejectionReason
) {}
