package com.ofds.pattern.strategy;

import java.util.logging.Logger;

public class UPIPaymentStrategy implements PaymentStrategy {
    private static final Logger log = Logger.getLogger(UPIPaymentStrategy.class.getName());

    @Override
    public boolean processPayment(double amount) {
        log.info("[Strategy] Processing UPI payment of ₹" + amount);
        return true;
    }

    @Override
    public String getMethodName() { return "UPI"; }
}
