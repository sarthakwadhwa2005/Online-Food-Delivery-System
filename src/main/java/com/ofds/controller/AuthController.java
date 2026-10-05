package com.ofds.controller;

import com.ofds.model.VehicleType;
import com.ofds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired private UserService userService;

    @GetMapping("/")
    public String home() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout, Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("msg", "Logged out successfully.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String role,
                           @RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) VehicleType vehicleType,
                           Model model) {
        try {
            switch (role) {
                case "CUSTOMER"         -> userService.registerCustomer(name, email, phone, password, address != null ? address : "");
                case "RESTAURANT_OWNER" -> userService.registerOwner(name, email, phone, password);
                case "DELIVERY_PARTNER" -> userService.registerDeliveryPartner(name, email, phone, password,
                                                vehicleType != null ? vehicleType : VehicleType.BIKE);
            }
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicleTypes", VehicleType.values());
            return "auth/register";
        }
    }
}
