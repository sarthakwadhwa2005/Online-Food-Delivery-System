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
        RestaurantOwner owner = getOwner(ud);
        model.addAttribute("owner", owner);
        model.addAttribute("restaurant", restaurantService.getOwnedRestaurant(id, owner));
        model.addAttribute("menuItems", restaurantService.getMenuByRestaurant(id));
        return "restaurant/menu";
    }

    @Transactional
    @PostMapping("/restaurant/{id}/menu/add")
    public String addMenuItem(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails ud,
                              @RequestParam String name,
                              @RequestParam double price,
                              @RequestParam(defaultValue = "true") boolean availability) {
        restaurantService.getOwnedRestaurant(id, getOwner(ud));
        restaurantService.addMenuItem(id, name, price, availability);
        return "redirect:/owner/restaurant/" + id + "/menu?added=true";
    }

    @Transactional
    @PostMapping("/menu/{itemId}/update")
    public String updateMenuItem(@PathVariable Long itemId,
                                 @AuthenticationPrincipal UserDetails ud,
                                 @RequestParam String name,
                                 @RequestParam double price,
                                 @RequestParam(defaultValue = "false") boolean availability,
                                 @RequestParam Long restaurantId) {
        restaurantService.getOwnedRestaurant(restaurantId, getOwner(ud));
        restaurantService.updateMenuItem(itemId, name, price, availability);
        return "redirect:/owner/restaurant/" + restaurantId + "/menu?updated=true";
    }

    @Transactional
    @PostMapping("/menu/{itemId}/delete")
    public String deleteMenuItem(@PathVariable Long itemId, @RequestParam Long restaurantId,
                                 @AuthenticationPrincipal UserDetails ud) {
        restaurantService.getOwnedRestaurant(restaurantId, getOwner(ud));
        restaurantService.deleteMenuItem(itemId);
        return "redirect:/owner/restaurant/" + restaurantId + "/menu?deleted=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/restaurant/{id}/orders")
    public String viewOrders(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud, Model model) {
        RestaurantOwner owner = getOwner(ud);
        Restaurant restaurant = restaurantService.getOwnedRestaurant(id, owner);
        model.addAttribute("owner", owner);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("orders", orderService.getOrdersByRestaurant(restaurant));
        model.addAttribute("statuses", OrderStatus.values());
        return "restaurant/orders";
    }

    @Transactional
    @PostMapping("/order/{id}/accept")
    public String acceptOrder(@PathVariable Long id, @RequestParam Long restaurantId,
                              @AuthenticationPrincipal UserDetails ud) {
        RestaurantOwner owner = getOwner(ud);
        if (!orderService.getOwnerOrder(id, owner).getRestaurant().getRestaurantId().equals(restaurantId)) {
            throw new org.springframework.security.access.AccessDeniedException("Order does not belong to restaurant.");
        }
        orderService.acceptOrder(id);
        return "redirect:/owner/restaurant/" + restaurantId + "/orders?accepted=true";
    }

    @Transactional
    @PostMapping("/order/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               @RequestParam Long restaurantId,
                               @AuthenticationPrincipal UserDetails ud) {
        RestaurantOwner owner = getOwner(ud);
        if (!orderService.getOwnerOrder(id, owner).getRestaurant().getRestaurantId().equals(restaurantId)) {
            throw new org.springframework.security.access.AccessDeniedException("Order does not belong to restaurant.");
        }
        orderService.updatePreparationStatus(id, status);
        return "redirect:/owner/restaurant/" + restaurantId + "/orders?updated=true";
    }
}
