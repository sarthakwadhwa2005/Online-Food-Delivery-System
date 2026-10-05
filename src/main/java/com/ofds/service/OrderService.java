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

        if (itemIds == null || quantities == null || itemIds.isEmpty()
                || itemIds.size() != quantities.size()) {
            throw new IllegalArgumentException("A non-empty cart with matching quantities is required.");
        }
        Restaurant restaurant = restaurantService.getById(restaurantId);

        OrderBuilder builder = new OrderBuilder().forCustomer(customer).fromRestaurant(restaurant);
        for (int i = 0; i < itemIds.size(); i++) {
            MenuItem item = menuItemRepository.findById(itemIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            if (!item.isAvailability() || item.getRestaurant() == null
                    || !item.getRestaurant().getRestaurantId().equals(restaurantId)
                    || quantities.get(i) == null || quantities.get(i) <= 0) {
                throw new IllegalArgumentException("Invalid or unavailable menu item in cart.");
            }
            builder.addItem(item, quantities.get(i));
        }
        Order order = builder.build();
        orderRepository.save(order);

        Payment payment = new Payment(order, paymentMethod, order.getTotalAmount());
        paymentRepository.save(payment);
        order.setPayment(payment);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() != OrderStatus.PLACED)
            throw new IllegalStateException("Only placed orders can be cancelled.");
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
        requireStatus(order, OrderStatus.PLACED);
        order.updateStatus(OrderStatus.PREPARING);
        Order saved = orderRepository.save(order);
        orderNotifier.notifyObservers(saved);
        return saved;
    }

    @Transactional
    public Order updatePreparationStatus(Long orderId, OrderStatus status) {
        Order order = getById(orderId);
        if (status != OrderStatus.PREPARING && status != OrderStatus.OUT_FOR_DELIVERY
                && status != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Invalid restaurant order status.");
        }
        if (status == OrderStatus.PREPARING) requireStatus(order, OrderStatus.PLACED);
        if (status == OrderStatus.OUT_FOR_DELIVERY) requireStatus(order, OrderStatus.PREPARING);
        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be cancelled.");
        }
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
        requireStatus(order, OrderStatus.PREPARING);
        if (!partner.isAvailabilityStatus()) {
            throw new IllegalStateException("Delivery partner is not available.");
        }
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
        requireStatus(order, OrderStatus.OUT_FOR_DELIVERY);
        if (order.getDeliveryPartner() == null
                || !order.getDeliveryPartner().getUserId().equals(partner.getUserId())) {
            throw new IllegalArgumentException("Order is not assigned to this delivery partner.");
        }
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

    public Order getCustomerOrder(Long orderId, Customer customer) {
        Order order = getById(orderId);
        if (order.getCustomer() == null
                || !order.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException("Order does not belong to customer.");
        }
        return order;
    }

    public Order getOwnerOrder(Long orderId, RestaurantOwner owner) {
        Order order = getById(orderId);
        if (order.getRestaurant() == null || order.getRestaurant().getOwner() == null
                || !order.getRestaurant().getOwner().getUserId().equals(owner.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException("Order is not owned by restaurant owner.");
        }
        return order;
    }

    private void requireStatus(Order order, OrderStatus expected) {
        if (order.getStatus() != expected) {
            throw new IllegalStateException("Order must be " + expected + ".");
        }
    }
}
