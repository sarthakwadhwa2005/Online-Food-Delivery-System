package com.ofds.pattern.observer;

import com.ofds.model.Order;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderNotifier {
    private final List<OrderObserver> observers = new ArrayList<>();

    public OrderNotifier() {
        observers.add(new CustomerNotificationObserver());
        observers.add(new RestaurantNotificationObserver());
        observers.add(new DeliveryPartnerNotificationObserver());
    }

    public void notifyObservers(Order order) {
        observers.forEach(o -> o.onOrderStatusChanged(order));
    }
}
