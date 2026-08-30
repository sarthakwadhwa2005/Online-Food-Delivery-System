package com.ofds.service;

import com.ofds.model.*;
import com.ofds.pattern.builder.OrderBuilder;
import com.ofds.pattern.factory.PaymentFactory;
import com.ofds.pattern.observer.OrderNotifier;
import com.ofds.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private DeliveryPartnerRepository deliveryPartnerRepository;
    @Autowired private PaymentFactory paymentFactory;
    @Autowired private OrderNotifier orderNotifier;

    @Transactional
    public Order placeOrder(Customer customer, Long restaurantId,
                            List<Long> itemIds, List<Integer> quantities,
                            PaymentMethod paymentMethod,
                            RestaurantService restaurantService) {

        Restaurant restaurant = restaurantService.getById(restaurantId);

        OrderBuilder builder = new OrderBuilder().forCustomer(customer).fromRestaurant(restaurant);
        for (int i = 0; i < itemIds.size(); i++) {
            MenuItem item = menuItemRepository.findById(itemIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            builder.addItem(item, quantities.get(i));
        }
        Order order = builder.build();
        orderRepository.save(order);

        var strategy = paymentFactory.createPaymentStrategy(paymentMethod);
        boolean success = strategy.processPayment(order.getTotalAmount());
        Payment payment = new Payment(order, paymentMethod, order.getTotalAmount());
        if (success) payment.makePayment();
        paymentRepository.save(payment);
        order.setPayment(payment);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED)
            throw new RuntimeException("Cannot cancel a delivered order.");
        order.updateStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    public Order trackOrder(Long orderId) { return getById(orderId); }

    public List<Order> getOrderHistory(Customer customer) {
        return orderRepository.findByCustomerOrderByOrderDateDesc(customer);
    }

    public List<Order> getOrdersByRestaurant(Restaurant restaurant) {
        return orderRepository.findByRestaurant(restaurant);
    }

    public List<Order> getPendingOrdersByRestaurant(Restaurant restaurant) {
        return orderRepository.findByRestaurantAndStatus(restaurant, OrderStatus.PLACED);
    }

    @Transactional
    public Order acceptOrder(Long orderId) {
        Order order = getById(orderId);
        order.updateStatus(OrderStatus.PREPARING);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    @Transactional
    public Order updatePreparationStatus(Long orderId, OrderStatus status) {
        Order order = getById(orderId);
        order.updateStatus(status);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    public List<Order> getDeliveryRequests() {
        return orderRepository.findByStatus(OrderStatus.PREPARING);
    }

    @Transactional
    public Order acceptDeliveryTask(Long orderId, DeliveryPartner partner) {
        Order order = getById(orderId);
        order.setDeliveryPartner(partner);
        partner.setAvailabilityStatus(false);
        deliveryPartnerRepository.save(partner);
        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    @Transactional
    public Order deliverOrder(Long orderId, DeliveryPartner partner) {
        Order order = getById(orderId);
        partner.setAvailabilityStatus(true);
        deliveryPartnerRepository.save(partner);
        order.updateStatus(OrderStatus.DELIVERED);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    public List<Order> getOrdersByDeliveryPartner(DeliveryPartner partner) {
        return orderRepository.findByDeliveryPartner(partner);
    }

    public List<Order> getAllOrders() { return orderRepository.findAll(); }

    @Transactional
    public Order assignDeliveryPartner(Long orderId, Long partnerId) {
        Order order = getById(orderId);
        DeliveryPartner partner = deliveryPartnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Delivery partner not found"));
        order.setDeliveryPartner(partner);
        return orderRepository.save(order);
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
