package com.ofds.pattern.builder;

import com.ofds.model.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: Builder (Creational)
 * Provides a fluent API for constructing complex Order objects step by step,
 * separating construction from representation.
 */
@Component
public class OrderBuilder {

    private Customer customer;
    private Restaurant restaurant;
    private final List<OrderItem> items = new ArrayList<>();

    public OrderBuilder forCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderBuilder fromRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }

    public OrderBuilder addItem(MenuItem menuItem, int quantity) {
        this.items.add(new OrderItem(menuItem, quantity));
        return this;
    }

    public Order build() {
        if (customer == null || restaurant == null) {
            throw new IllegalStateException("Order requires a customer and a restaurant.");
        }
        Order order = new Order(customer, restaurant);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PLACED);
        items.forEach(order::addItem);
        order.calculateTotal();
        return order;
    }

    public OrderBuilder reset() {
        this.customer = null;
        this.restaurant = null;
        this.items.clear();
        return this;
    }
}
