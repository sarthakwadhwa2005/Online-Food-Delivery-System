package com.ofds.repository;

import com.ofds.model.MenuItem;
import com.ofds.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurant(Restaurant restaurant);
    List<MenuItem> findByRestaurantAndAvailabilityTrue(Restaurant restaurant);
}
