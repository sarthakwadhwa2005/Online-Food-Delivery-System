package com.ofds.repository;

import com.ofds.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);
    List<Order> findByRestaurant(Restaurant restaurant);
    List<Order> findByDeliveryPartner(DeliveryPartner deliveryPartner);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status);
}
