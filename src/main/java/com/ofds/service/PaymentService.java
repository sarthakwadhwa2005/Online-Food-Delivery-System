package com.ofds.service;

import com.ofds.model.*;
import com.ofds.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order " + orderId));
    }

    public Payment refundPayment(Long orderId) {
        Payment payment = getPaymentByOrder(orderId);
        payment.refundPayment();
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() { return paymentRepository.findAll(); }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    public double getTotalRevenue() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.COMPLETED)
                .stream().mapToDouble(Payment::getAmount).sum();
    }
}
