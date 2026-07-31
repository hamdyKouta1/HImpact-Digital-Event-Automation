package com.himpact.controller;

import com.himpact.dto.PageResponse;
import com.himpact.dto.payment.ApprovePaymentRequest;
import com.himpact.dto.payment.PaymentResponse;
import com.himpact.dto.payment.SubmitPaymentRequest;
import com.himpact.security.HimpactUserPrincipal;
import com.himpact.service.PaymentService;
import com.himpact.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Payment Platform Controller.
 * Base path: /api/v1/payments
 * See: project-index/07_API_Specification.md — Payment APIs
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Platform", description = "Manual payment submission, state machine approval, and audit trail")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Submit Payment Proof", description = "Submits manual payment receipt reference (InstaPay / Vodafone Cash).")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> submitPayment(
            @AuthenticationPrincipal HimpactUserPrincipal principal,
            @Valid @RequestBody SubmitPaymentRequest request
    ) {
        PaymentResponse response = paymentService.submitPayment(request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment proof submitted successfully.", response));
    }

    @Operation(summary = "Get My Payments", description = "Returns payment history for the authenticated owner.")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyPayments(
            @AuthenticationPrincipal HimpactUserPrincipal principal
    ) {
        List<PaymentResponse> payments = paymentService.getMyPayments(principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved.", payments));
    }

    @Operation(summary = "Approve or Reject Payment", description = "Admin endpoint advancing payment state machine.")
    @PostMapping("/{paymentId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> approveOrRejectPayment(
            @PathVariable UUID paymentId,
            @AuthenticationPrincipal HimpactUserPrincipal principal,
            @Valid @RequestBody ApprovePaymentRequest request
    ) {
        PaymentResponse response = paymentService.approveOrRejectPayment(paymentId, request, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Payment status updated.", response));
    }

    @Operation(summary = "List Pending Payments", description = "Admin endpoint for viewing pending manual payments queue.")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getPendingPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        PageResponse<PaymentResponse> response = paymentService.getPendingPayments(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending payments queue retrieved.", response));
    }
}
