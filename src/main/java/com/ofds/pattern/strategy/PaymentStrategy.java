package com.ofds.pattern.strategy;

/**
 * DESIGN PATTERN: Strategy (Behavioral)
 * Defines a family of payment algorithms, encapsulates each one,
 * and makes them interchangeable without altering the client (PaymentService).
 */
public interface PaymentStrategy {
    boolean processPayment(double amount);
    String getMethodName();
}
