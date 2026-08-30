package com.ofds.pattern.observer;

import com.ofds.model.Order;
import java.util.logging.Logger;

public class RestaurantNotificationObserver implements OrderObserver {
    private static final Logger log = Logger.getLogger(RestaurantNotificationObserver.class.getName());
    @Override
    public void onOrderStatusChanged(Order order) {
        log.info("[Observer] Alert to restaurant " + order.getRestaurant().getName()
                + ": Order #" + order.getOrderId() + " -> " + order.getStatus());
    }
}
