package com.ofds.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    @Test
    void addItemRecalculatesTotalFromMenuPriceSnapshot() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address", new RestaurantOwner());
        MenuItem item = new MenuItem("Test Item", 12.50, true, restaurant);
        Order order = new Order(new Customer(), restaurant);

        order.addItem(new OrderItem(item, 2));

        assertEquals(25.0, order.getTotalAmount(), 0.001);
    }
}
