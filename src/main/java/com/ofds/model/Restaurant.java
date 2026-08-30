package com.ofds.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    private String name;
    private String address;
    private float rating;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private RestaurantOwner owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MenuItem> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    public Restaurant() {}

    public Restaurant(String name, String address, RestaurantOwner owner) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.rating = 0;
    }

    public void addMenuItem(MenuItem item) { item.setRestaurant(this); menuItems.add(item); }
    public void removeMenuItem(MenuItem item) { menuItems.remove(item); }
    public void updateMenuItem(MenuItem item, String name, double price, boolean availability) {
        item.setName(name); item.setPrice(price); item.setAvailability(availability);
    }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public RestaurantOwner getOwner() { return owner; }
    public void setOwner(RestaurantOwner owner) { this.owner = owner; }
    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }
    public List<Order> getOrders() { return orders; }
}
