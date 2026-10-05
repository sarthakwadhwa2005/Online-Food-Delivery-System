package com.ofds.pattern.observer;

import com.ofds.model.Order;

/**
 * DESIGN PATTERN: Observer (Behavioral)
 * When an Order's status changes, all registered observers are notified automatically.
 * Implements loose coupling between order state and notification logic.
 */
public interface OrderObserver {
    void onOrderStatusChanged(Order order);
}
