package com.ofds.service;

import com.ofds.model.*;
import com.ofds.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private RestaurantOwnerRepository ownerRepository;

    @Transactional
    public Restaurant createRestaurant(String name, String address, RestaurantOwner owner) {
        Restaurant r = new Restaurant(name, address, owner);
        return restaurantRepository.save(r);
    }

    @Transactional
    public Restaurant updateRestaurant(Long id, String name, String address) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        r.setName(name);
        r.setAddress(address);
        return restaurantRepository.save(r);
    }

    @Transactional
    public void deleteRestaurant(Long id) { restaurantRepository.deleteById(id); }

    public List<Restaurant> getAllRestaurants() { return restaurantRepository.findAll(); }

    public List<Restaurant> searchRestaurants(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name);
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<Restaurant> getRestaurantsByOwner(RestaurantOwner owner) {
        return restaurantRepository.findByOwner(owner);
    }

    @Transactional
    public MenuItem addMenuItem(Long restaurantId, String name, double price, boolean availability) {
        Restaurant r = getById(restaurantId);
        MenuItem item = new MenuItem(name, price, availability, r);
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem updateMenuItem(Long itemId, String name, double price, boolean availability) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        item.setName(name);
        item.setPrice(price);
        item.setAvailability(availability);
        return menuItemRepository.save(item);
    }

    @Transactional
    public void deleteMenuItem(Long itemId) { menuItemRepository.deleteById(itemId); }

    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        Restaurant r = getById(restaurantId);
        return menuItemRepository.findByRestaurant(r);
    }

    public List<MenuItem> getAvailableMenu(Long restaurantId) {
        Restaurant r = getById(restaurantId);
        return menuItemRepository.findByRestaurantAndAvailabilityTrue(r);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }

    @Transactional
    public void updateRating(Long restaurantId, float newRating) {
        Restaurant r = getById(restaurantId);
        float updated = (r.getRating() == 0) ? newRating : (r.getRating() + newRating) / 2f;
        r.setRating(Math.min(5.0f, Math.max(0f, updated)));
        restaurantRepository.save(r);
    }
}
