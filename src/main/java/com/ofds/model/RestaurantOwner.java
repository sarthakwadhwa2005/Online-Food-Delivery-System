package com.ofds.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_owners")
@DiscriminatorValue("RESTAURANT_OWNER")
public class RestaurantOwner extends User {

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Restaurant> restaurants = new ArrayList<>();

    public RestaurantOwner() {}

    public RestaurantOwner(String name, String email, String phone, String password) {
        super(name, email, phone, password);
    }

    public void manageRestaurant() {}
    public void manageMenu() {}
    public void acceptOrder() {}
    public void updatePreparationStatus() {}

    public List<Restaurant> getRestaurants() { return restaurants; }
    public void setRestaurants(List<Restaurant> restaurants) { this.restaurants = restaurants; }
}
