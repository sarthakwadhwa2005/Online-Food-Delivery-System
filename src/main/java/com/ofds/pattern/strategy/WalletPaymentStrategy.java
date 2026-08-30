package com.ofds.pattern.strategy;

import java.util.logging.Logger;

public class WalletPaymentStrategy implements PaymentStrategy {
    private static final Logger log = Logger.getLogger(WalletPaymentStrategy.class.getName());
    @Override
    public boolean processPayment(double amount) {
        log.info("[Strategy] Processing Wallet payment of ₹" + amount);
        return true;
    }
    @Override public String getMethodName() { return "WALLET"; }
}
