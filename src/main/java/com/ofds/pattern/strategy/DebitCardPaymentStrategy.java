package com.ofds.pattern.strategy;

import java.util.logging.Logger;

public class DebitCardPaymentStrategy implements PaymentStrategy {
    private static final Logger log = Logger.getLogger(DebitCardPaymentStrategy.class.getName());
    @Override
    public boolean processPayment(double amount) {
        log.info("[Strategy] Processing Debit Card payment of ₹" + amount);
        return true;
    }
    @Override public String getMethodName() { return "DEBIT_CARD"; }
}
