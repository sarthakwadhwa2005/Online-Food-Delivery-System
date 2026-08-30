package com.ofds.controller;

import com.ofds.model.*;
import com.ofds.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;

    private Customer getCustomer(UserDetails ud) {
        return userService.findCustomerByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + ud.getUsername()));
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        Customer customer = getCustomer(ud);
        List<Order> orders = orderService.getOrderHistory(customer);
        model.addAttribute("customer", customer);
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("orderCount", orders.size());
        return "customer/dashboard";
    }

    @GetMapping("/restaurants")
    @Transactional(readOnly = true)
    public String browseRestaurants(@RequestParam(required = false) String search,
                                    @AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("customer", getCustomer(ud));
        model.addAttribute("restaurants", (search != null && !search.isBlank())
                ? restaurantService.searchRestaurants(search)
                : restaurantService.getAllRestaurants());
        model.addAttribute("search", search);
        return "customer/restaurants";
    }

    @GetMapping("/restaurant/{id}/menu")
    @Transactional(readOnly = true)
    public String viewMenu(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("customer", getCustomer(ud));
        model.addAttribute("restaurant", restaurantService.getById(id));
        model.addAttribute("menuItems", restaurantService.getAvailableMenu(id));
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "customer/menu";
    }

    @PostMapping("/order/place")
    @Transactional
    public String placeOrder(@RequestParam Long restaurantId,
                             @RequestParam List<Long> itemIds,
                             @RequestParam List<Integer> quantities,
                             @RequestParam PaymentMethod paymentMethod,
                             @AuthenticationPrincipal UserDetails ud) {
        try {
            Customer customer = getCustomer(ud);
            Order order = orderService.placeOrder(customer, restaurantId,
                    itemIds, quantities, paymentMethod, restaurantService);
            return "redirect:/customer/order/" + order.getOrderId() + "?success=true";
        } catch (Exception e) {
            return "redirect:/customer/restaurant/" + restaurantId + "/menu?error=true";
        }
    }

    @GetMapping("/order/{id}")
    @Transactional(readOnly = true)
    public String viewOrder(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("customer", getCustomer(ud));
        model.addAttribute("order", orderService.getById(id));
        return "customer/order-detail";
    }

    @PostMapping("/order/{id}/cancel")
    @Transactional
    public String cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return "redirect:/customer/orders?cancelled=true";
    }

    @GetMapping("/orders")
    @Transactional(readOnly = true)
    public String orderHistory(@AuthenticationPrincipal UserDetails ud, Model model) {
        Customer customer = getCustomer(ud);
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orderService.getOrderHistory(customer));
        return "customer/order-history";
    }

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    public String profile(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("customer", getCustomer(ud));
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    @Transactional
    public String updateProfile(@AuthenticationPrincipal UserDetails ud,
                                @RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam(required = false) String address) {
        Customer customer = getCustomer(ud);
        userService.updateProfile(customer.getUserId(), name, phone);
        if (address != null && !address.isBlank()) customer.setAddress(address);
        return "redirect:/customer/profile?updated=true";
    }

    @PostMapping("/restaurant/{id}/rate")
    @Transactional
    public String rateRestaurant(@PathVariable Long id, @RequestParam float rating) {
        restaurantService.updateRating(id, rating);
        return "redirect:/customer/orders?rated=true";
    }
}
