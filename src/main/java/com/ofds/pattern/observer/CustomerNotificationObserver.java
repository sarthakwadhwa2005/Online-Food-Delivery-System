package com.ofds.pattern.observer;

import com.ofds.model.Order;
import java.util.logging.Logger;

public class CustomerNotificationObserver implements OrderObserver {
    private static final Logger log = Logger.getLogger(CustomerNotificationObserver.class.getName());
    @Override
    public void onOrderStatusChanged(Order order) {
        log.info("[Observer] SMS to customer " + order.getCustomer().getName()
                + ": Your order #" + order.getOrderId() + " is now " + order.getStatus());
    }
}
