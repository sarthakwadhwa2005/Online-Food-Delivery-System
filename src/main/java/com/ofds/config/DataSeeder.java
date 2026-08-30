package com.ofds.config;

import com.ofds.model.*;
import com.ofds.repository.AdminRepository;
import com.ofds.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private AdminRepository adminRepository;

    @Override
    public void run(String... args) {
        // Only seed if database is empty
        if (adminRepository.findByEmail("admin@ofds.com").isPresent()) {
            System.out.println("=== Data already seeded, skipping. ===");
            return;
        }

        // Admin
        Admin admin = new Admin("Admin User", "admin@ofds.com", "9000000000",
                userService.encodePassword("admin123"), 1, "Operations");
        userService.saveAdmin(admin);

        // Customers
        Customer c1 = userService.registerCustomer("Sarthak Garg",  "sarthak@ofds.com", "9111111111", "pass123", "Koramangala, Bengaluru");
        Customer c2 = userService.registerCustomer("Sneh Patel",    "sneh@ofds.com",    "9222222222", "pass123", "Indiranagar, Bengaluru");

        // Restaurant Owners
        RestaurantOwner o1 = userService.registerOwner("Shravan M",      "shravan@ofds.com", "9333333333", "pass123");
        RestaurantOwner o2 = userService.registerOwner("Sarthak Wadhwa", "wadhwa@ofds.com",  "9444444444", "pass123");

        // Delivery Partners
        userService.registerDeliveryPartner("Ravi Kumar",  "ravi@ofds.com",  "9555555555", "pass123", VehicleType.BIKE);
        userService.registerDeliveryPartner("Priya Singh", "priya@ofds.com", "9666666666", "pass123", VehicleType.SCOOTER);

        // Restaurants
        Restaurant r1 = restaurantService.createRestaurant("Spice Garden",    "MG Road, Bengaluru",      o1);
        Restaurant r2 = restaurantService.createRestaurant("The Pizza House",  "HSR Layout, Bengaluru",   o2);
        Restaurant r3 = restaurantService.createRestaurant("Biryani Palace",   "Whitefield, Bengaluru",   o1);

        // Menu – Spice Garden
        restaurantService.addMenuItem(r1.getRestaurantId(), "Butter Chicken",  320.0, true);
        restaurantService.addMenuItem(r1.getRestaurantId(), "Paneer Tikka",    250.0, true);
        restaurantService.addMenuItem(r1.getRestaurantId(), "Garlic Naan",      50.0, true);
        restaurantService.addMenuItem(r1.getRestaurantId(), "Mango Lassi",      80.0, true);

        // Menu – Pizza House
        restaurantService.addMenuItem(r2.getRestaurantId(), "Margherita Pizza", 299.0, true);
        restaurantService.addMenuItem(r2.getRestaurantId(), "BBQ Chicken Pizza",399.0, true);
        restaurantService.addMenuItem(r2.getRestaurantId(), "Garlic Bread",      99.0, true);
        restaurantService.addMenuItem(r2.getRestaurantId(), "Cold Drink",        60.0, true);

        // Menu – Biryani Palace
        restaurantService.addMenuItem(r3.getRestaurantId(), "Chicken Biryani",  280.0, true);
        restaurantService.addMenuItem(r3.getRestaurantId(), "Veg Biryani",      220.0, true);
        restaurantService.addMenuItem(r3.getRestaurantId(), "Raita",             50.0, true);
        restaurantService.addMenuItem(r3.getRestaurantId(), "Gulab Jamun",       80.0, true);

        System.out.println("============================================");
        System.out.println("  OFDS Demo Data Loaded!");
        System.out.println("  Visit: http://localhost:8080");
        System.out.println("  Logins (password: pass123):");
        System.out.println("    Customer : sarthak@ofds.com");
        System.out.println("    Owner    : shravan@ofds.com");
        System.out.println("    Delivery : ravi@ofds.com");
        System.out.println("    Admin    : admin@ofds.com  (admin123)");
        System.out.println("============================================");
    }
}
