package com.ofds.pattern.factory;

import com.ofds.model.PaymentMethod;
import com.ofds.pattern.strategy.*;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: Factory (Creational)
 * Creates the correct PaymentStrategy object based on the PaymentMethod enum,
 * decoupling object creation from the service that uses it.
 */
@Component
public class PaymentFactory {

    public PaymentStrategy createPaymentStrategy(PaymentMethod method) {
        return switch (method) {
            case UPI         -> new UPIPaymentStrategy();
            case CREDIT_CARD -> new CreditCardPaymentStrategy();
            case DEBIT_CARD  -> new DebitCardPaymentStrategy();
            case WALLET      -> new WalletPaymentStrategy();
        };
    }
}
