package com.ofds.repository;

import com.ofds.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {
    Optional<RestaurantOwner> findByEmail(String email);
}
