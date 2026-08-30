package com.ofds.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment() {}

    public Payment(Order order, PaymentMethod method, double amount) {
        this.order = order;
        this.method = method;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public void makePayment()  { this.paymentStatus = PaymentStatus.COMPLETED; }
    public void refundPayment(){ this.paymentStatus = PaymentStatus.FAILED; }
    public boolean verifyPayment() { return this.paymentStatus == PaymentStatus.COMPLETED; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}
