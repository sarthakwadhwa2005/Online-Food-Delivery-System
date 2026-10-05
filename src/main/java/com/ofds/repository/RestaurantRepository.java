package com.ofds.repository;

import com.ofds.model.Restaurant;
import com.ofds.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByOwner(RestaurantOwner owner);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
