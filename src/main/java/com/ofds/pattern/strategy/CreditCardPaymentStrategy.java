package com.ofds.pattern.strategy;

import java.util.logging.Logger;

public class CreditCardPaymentStrategy implements PaymentStrategy {
    private static final Logger log = Logger.getLogger(CreditCardPaymentStrategy.class.getName());
    @Override
    public boolean processPayment(double amount) {
        log.info("[Strategy] Processing Credit Card payment of ₹" + amount);
        return true;
    }
    @Override public String getMethodName() { return "CREDIT_CARD"; }
}
