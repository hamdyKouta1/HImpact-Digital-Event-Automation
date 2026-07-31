package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Payment JPA entity — tracks manual payments (InstaPay / Vodafone Cash).
 * Table: payments
 * See: project-index/06_Database_Design.md — payments entity
 * See: PO Sprint 5 Workstream B Payment State Machine
 */
@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payments_event_id", columnList = "event_id"),
                @Index(name = "idx_payments_state", columnList = "payment_state")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private PackageEntity packageEntity;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // INSTAPAY, VODAFONE_CASH

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "EGP";

    @Column(name = "payment_reference", nullable = false, length = 255)
    private String paymentReference;

    @Column(name = "receipt_image_url", length = 500)
    private String receiptImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state", nullable = false, length = 30)
    @Builder.Default
    private PaymentState paymentState = PaymentState.SUBMITTED;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;
}
