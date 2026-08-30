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

@Controller
@RequestMapping("/owner")
public class RestaurantOwnerController {

    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;

    private RestaurantOwner getOwner(UserDetails ud) {
        return userService.findOwnerByEmail(ud.getUsername()).orElseThrow();
    }

    @Transactional(readOnly = true)
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        RestaurantOwner owner = getOwner(ud);
        var restaurants = restaurantService.getRestaurantsByOwner(owner);
        model.addAttribute("owner", owner);
        model.addAttribute("restaurants", restaurants);
        long pending = restaurants.stream()
                .flatMap(r -> orderService.getPendingOrdersByRestaurant(r).stream()).count();
        model.addAttribute("pendingCount", pending);
        return "restaurant/dashboard";
    }

    @Transactional(readOnly = true)
    @GetMapping("/restaurants")
    public String manageRestaurants(@AuthenticationPrincipal UserDetails ud, Model model) {
        RestaurantOwner owner = getOwner(ud);
        model.addAttribute("owner", owner);
        model.addAttribute("restaurants", restaurantService.getRestaurantsByOwner(owner));
        return "restaurant/restaurants";
    }

    @Transactional
    @PostMapping("/restaurant/create")
    public String createRestaurant(@AuthenticationPrincipal UserDetails ud,
                                   @RequestParam String name, @RequestParam String address) {
        restaurantService.createRestaurant(name, address, getOwner(ud));
        return "redirect:/owner/restaurants?created=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/restaurant/{id}/menu")
    public String manageMenu(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("owner", getOwner(ud));
        model.addAttribute("restaurant", restaurantService.getById(id));
        model.addAttribute("menuItems", restaurantService.getMenuByRestaurant(id));
        return "restaurant/menu";
    }

    @Transactional
    @PostMapping("/restaurant/{id}/menu/add")
    public String addMenuItem(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam double price,
                              @RequestParam(defaultValue = "true") boolean availability) {
        restaurantService.addMenuItem(id, name, price, availability);
        return "redirect:/owner/restaurant/" + id + "/menu?added=true";
    }

    @Transactional
    @PostMapping("/menu/{itemId}/update")
    public String updateMenuItem(@PathVariable Long itemId,
                                 @RequestParam String name,
                                 @RequestParam double price,
                                 @RequestParam(defaultValue = "false") boolean availability,
                                 @RequestParam Long restaurantId) {
        restaurantService.updateMenuItem(itemId, name, price, availability);
        return "redirect:/owner/restaurant/" + restaurantId + "/menu?updated=true";
    }

    @Transactional
    @PostMapping("/menu/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long itemId, @RequestParam Long restaurantId) {
        restaurantService.deleteMenuItem(itemId);
        return "redirect:/owner/restaurant/" + restaurantId + "/menu?deleted=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/restaurant/{id}/orders")
    public String viewOrders(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud, Model model) {
        Restaurant restaurant = restaurantService.getById(id);
        model.addAttribute("owner", getOwner(ud));
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("orders", orderService.getOrdersByRestaurant(restaurant));
        model.addAttribute("statuses", OrderStatus.values());
        return "restaurant/orders";
    }

    @Transactional
    @PostMapping("/order/{id}/accept")
    public String acceptOrder(@PathVariable Long id, @RequestParam Long restaurantId) {
        orderService.acceptOrder(id);
        return "redirect:/owner/restaurant/" + restaurantId + "/orders?accepted=true";
    }

    @Transactional
    @PostMapping("/order/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               @RequestParam Long restaurantId) {
        orderService.updatePreparationStatus(id, status);
        return "redirect:/owner/restaurant/" + restaurantId + "/orders?updated=true";
    }
}
