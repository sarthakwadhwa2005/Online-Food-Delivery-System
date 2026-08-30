package com.ofds.pattern.observer;

import com.ofds.model.Order;
import java.util.logging.Logger;

public class DeliveryPartnerNotificationObserver implements OrderObserver {
    private static final Logger log = Logger.getLogger(DeliveryPartnerNotificationObserver.class.getName());
    @Override
    public void onOrderStatusChanged(Order order) {
        if (order.getDeliveryPartner() != null) {
            log.info("[Observer] Notify delivery partner " + order.getDeliveryPartner().getName()
                    + ": Order #" + order.getOrderId() + " -> " + order.getStatus());
        }
    }
}
