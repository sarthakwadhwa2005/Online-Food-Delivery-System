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
@RequestMapping("/delivery")
public class DeliveryPartnerController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;

    private DeliveryPartner getPartner(UserDetails ud) {
        return userService.findDeliveryPartnerByEmail(ud.getUsername()).orElseThrow();
    }

    @Transactional(readOnly = true)
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        DeliveryPartner partner = getPartner(ud);
        model.addAttribute("partner", partner);
        model.addAttribute("myOrders", orderService.getOrdersByDeliveryPartner(partner));
        model.addAttribute("requests", orderService.getDeliveryRequests());
        return "delivery/dashboard";
    }

    @Transactional(readOnly = true)
    @GetMapping("/requests")
    public String viewRequests(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("partner", getPartner(ud));
        model.addAttribute("requests", orderService.getDeliveryRequests());
        return "delivery/requests";
    }

    @Transactional
    @PostMapping("/order/{id}/accept")
    public String acceptTask(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        orderService.acceptDeliveryTask(id, getPartner(ud));
        return "redirect:/delivery/dashboard?accepted=true";
    }

    @Transactional
    @PostMapping("/order/{id}/deliver")
    public String deliverOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        orderService.deliverOrder(id, getPartner(ud));
        return "redirect:/delivery/dashboard?delivered=true";
    }

    @Transactional(readOnly = true)
    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails ud, Model model) {
        DeliveryPartner partner = getPartner(ud);
        model.addAttribute("partner", partner);
        model.addAttribute("orders", orderService.getOrdersByDeliveryPartner(partner));
        return "delivery/my-orders";
    }

    @Transactional(readOnly = true)
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("partner", getPartner(ud));
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "delivery/profile";
    }

    @Transactional
    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails ud,
                                @RequestParam String name,
                                @RequestParam String phone) {
        DeliveryPartner partner = getPartner(ud);
        userService.updateProfile(partner.getUserId(), name, phone);
        return "redirect:/delivery/profile?updated=true";
    }
}
