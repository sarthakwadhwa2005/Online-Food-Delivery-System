package com.ofds.pattern.facade;

import com.ofds.model.*;
import com.ofds.pattern.builder.OrderBuilder;
import com.ofds.pattern.factory.PaymentFactory;
import com.ofds.pattern.observer.OrderNotifier;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

/**
 * DESIGN PATTERN: Facade (Structural)
 * Demonstrates the Facade pattern - a simplified entry point that internally
 * coordinates Builder, Factory, Strategy, and Observer patterns.
 * Used directly by OrderService to orchestrate the order placement flow.
 */
@Component
public class OrderFacade {

    private static final Logger log = Logger.getLogger(OrderFacade.class.getName());

    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PaymentFactory paymentFactory;
    @Autowired private OrderNotifier orderNotifier;

    public Order placeOrder(Customer customer, Restaurant restaurant,
                            List<MenuItem> menuItems, List<Integer> quantities,
                            PaymentMethod paymentMethod) {
        // Builder pattern
        OrderBuilder builder = new OrderBuilder().forCustomer(customer).fromRestaurant(restaurant);
        for (int i = 0; i < menuItems.size(); i++) {
            builder.addItem(menuItems.get(i), quantities.get(i));
        }
        Order order = builder.build();
        orderRepository.save(order);

        // Factory + Strategy pattern
        var strategy = paymentFactory.createPaymentStrategy(paymentMethod);
        boolean success = strategy.processPayment(order.getTotalAmount());
        Payment payment = new Payment(order, paymentMethod, order.getTotalAmount());
        if (success) payment.makePayment();
        paymentRepository.save(payment);
        order.setPayment(payment);
        Order saved = orderRepository.save(order);

        // Observer pattern
        orderNotifier.notifyObservers(saved);
        log.info("[Facade] Order #" + saved.getOrderId() + " placed successfully.");
        return saved;
    }

    public Order updateStatus(Order order, OrderStatus newStatus) {
        order.updateStatus(newStatus);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }
}
