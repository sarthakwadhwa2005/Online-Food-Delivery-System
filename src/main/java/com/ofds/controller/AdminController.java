package com.ofds.controller;

import com.ofds.model.*;
import com.ofds.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;
    @Autowired private PaymentService paymentService;

    @Transactional(readOnly = true)
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("userCount",       userService.getAllUsers().size());
        model.addAttribute("restaurantCount", restaurantService.getAllRestaurants().size());
        model.addAttribute("orderCount",      orderService.getAllOrders().size());
        model.addAttribute("totalRevenue",    paymentService.getTotalRevenue());
        model.addAttribute("recentOrders",    orderService.getAllOrders().stream().limit(5).toList());
        return "admin/dashboard";
    }

    @Transactional(readOnly = true)
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @Transactional
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users?deleted=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/restaurants")
    public String manageRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "admin/restaurants";
    }

    @Transactional
    @PostMapping("/restaurants/{id}/delete")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return "redirect:/admin/restaurants?deleted=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/orders")
    public String monitorOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("availablePartners", userService.getAvailableDeliveryPartners());
        return "admin/orders";
    }

    @Transactional
    @PostMapping("/orders/{orderId}/assign")
    public String assignDeliveryPartner(@PathVariable Long orderId, @RequestParam Long partnerId) {
        orderService.assignDeliveryPartner(orderId, partnerId);
        return "redirect:/admin/orders?assigned=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalRevenue",    paymentService.getTotalRevenue());
        model.addAttribute("totalOrders",     orderService.getAllOrders().size());
        model.addAttribute("deliveredOrders", orderService.getAllOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED).count());
        model.addAttribute("cancelledOrders", orderService.getAllOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED).count());
        model.addAttribute("payments",        paymentService.getAllPayments());
        model.addAttribute("restaurants",     restaurantService.getAllRestaurants());
        model.addAttribute("partners",        userService.getAllDeliveryPartners());
        return "admin/reports";
    }
}
