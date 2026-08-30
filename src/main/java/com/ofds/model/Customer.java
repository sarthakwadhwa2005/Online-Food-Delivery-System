package com.ofds.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public Customer() {}

    public Customer(String name, String email, String phone, String password, String address) {
        super(name, email, phone, password);
        this.address = address;
    }

    public void browseRestaurants() {}
    public void viewMenu() {}
    public void addToCart() {}
    public void placeOrder() {}
    public void cancelOrder() {}
    public void trackOrder() {}
    public void viewOrderHistory() {}
    public void rateRestaurant() {}

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}
