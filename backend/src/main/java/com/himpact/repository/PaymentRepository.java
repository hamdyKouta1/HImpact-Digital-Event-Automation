package com.himpact.repository;

import com.himpact.entity.Payment;
import com.himpact.entity.PaymentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByEventIdAndIsDeletedFalse(UUID eventId, Pageable pageable);
    List<Payment> findByEventOwnerIdAndIsDeletedFalse(UUID ownerId);
    Page<Payment> findByPaymentStateAndIsDeletedFalse(PaymentState paymentState, Pageable pageable);
    long countByPaymentStateAndIsDeletedFalse(PaymentState paymentState);
}
