package com.ofds.repository;

import com.ofds.model.Payment;
import com.ofds.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderOrderId(Long orderId);
    List<Payment> findByPaymentStatus(PaymentStatus status);
}
