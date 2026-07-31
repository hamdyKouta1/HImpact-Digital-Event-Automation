package com.himpact.service;

import com.himpact.domain.events.PaymentApprovedEvent;
import com.himpact.dto.PageResponse;
import com.himpact.dto.payment.ApprovePaymentRequest;
import com.himpact.dto.payment.PaymentResponse;
import com.himpact.dto.payment.SubmitPaymentRequest;
import com.himpact.entity.*;
import com.himpact.exception.BusinessRuleException;
import com.himpact.exception.ResourceNotFoundException;
import com.himpact.repository.EventRepository;
import com.himpact.repository.PackageRepository;
import com.himpact.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Business logic service for Payment Management.
 * Implements explicit Payment State Machine (SUBMITTED -> UNDER_REVIEW -> APPROVED -> ACTIVATED / REJECTED).
 * Emits PaymentApprovedEvent for decoupled package activation.
 *
 * See: PO Sprint 5 Workstream B Payment State Machine
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;
    private final PackageRepository packageRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Submit manual payment proof for an event.
     */
    @Transactional
    public PaymentResponse submitPayment(SubmitPaymentRequest request, UUID submittedByUserId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", request.eventId()));

        PackageEntity pkg = null;
        if (request.packageId() != null) {
            pkg = packageRepository.findById(request.packageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Package", "id", request.packageId()));
        }

        Payment payment = Payment.builder()
                .event(event)
                .packageEntity(pkg)
                .paymentMethod(request.paymentMethod())
                .amount(request.amount())
                .paymentReference(request.paymentReference())
                .receiptImageUrl(request.receiptImageUrl())
                .paymentState(PaymentState.SUBMITTED)
                .createdBy(submittedByUserId)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment proof submitted [{}] for event [{}]", saved.getId(), event.getId());
        return mapToResponse(saved);
    }

    /**
     * Admin payment review and approval / rejection.
     * Advances payment state machine: SUBMITTED -> UNDER_REVIEW -> APPROVED / REJECTED.
     */
    @Transactional
    public PaymentResponse approveOrRejectPayment(UUID paymentId, ApprovePaymentRequest request, UUID adminUserId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.getPaymentState() == PaymentState.APPROVED || payment.getPaymentState() == PaymentState.ACTIVATED) {
            throw new BusinessRuleException("Payment is already approved.");
        }

        payment.setPaymentState(PaymentState.UNDER_REVIEW);

        if (Boolean.TRUE.equals(request.approved())) {
            payment.setPaymentState(PaymentState.APPROVED);
            payment.setApprovedBy(adminUserId);
            payment.setApprovedAt(Instant.now());

            Payment updated = paymentRepository.save(payment);
            log.info("Payment [{}] APPROVED by admin [{}]", paymentId, adminUserId);

            // Emit domain event for decoupled package activation (PO Requirement B)
            UUID packageId = updated.getPackageEntity() != null ? updated.getPackageEntity().getId() : null;
            eventPublisher.publishEvent(new PaymentApprovedEvent(
                    updated.getId(), updated.getEvent().getId(), packageId, adminUserId));

            // Transition to ACTIVATED
            updated.setPaymentState(PaymentState.ACTIVATED);
            return mapToResponse(paymentRepository.save(updated));
        } else {
            payment.setPaymentState(PaymentState.REJECTED);
            payment.setRejectionReason(request.rejectionReason());
            Payment updated = paymentRepository.save(payment);
            log.info("Payment [{}] REJECTED by admin [{}]", paymentId, adminUserId);
            return mapToResponse(updated);
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPendingPayments(Pageable pageable) {
        return PageResponse.from(paymentRepository.findByPaymentStateAndIsDeletedFalse(PaymentState.SUBMITTED, pageable)
                .map(this::mapToResponse));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(UUID ownerId) {
        return paymentRepository.findByEventOwnerIdAndIsDeletedFalse(ownerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponse mapToResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getEvent().getId(),
                p.getEvent().getTitle(),
                p.getPackageEntity() != null ? p.getPackageEntity().getId() : null,
                p.getPackageEntity() != null ? p.getPackageEntity().getPackageName() : null,
                p.getPaymentMethod(),
                p.getAmount(),
                p.getCurrency(),
                p.getPaymentReference(),
                p.getReceiptImageUrl(),
                p.getPaymentState(),
                p.getRejectionReason(),
                p.getCreatedAt()
        );
    }
}
