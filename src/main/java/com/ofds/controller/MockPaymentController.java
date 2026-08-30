package com.ofds.controller;

import com.ofds.model.*;
import com.ofds.repository.OrderRepository;
import com.ofds.repository.PaymentRepository;
import com.ofds.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * MockPaymentController simulates a real payment gateway experience:
 * 1. /payment/checkout  – Shows the payment page (card form / UPI / wallet)
 * 2. /payment/process   – AJAX endpoint that simulates processing delay + result
 * 3. /payment/confirm   – Final confirmation, updates order & payment in DB
 */
@Controller
@RequestMapping("/payment")
public class MockPaymentController {

    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;

    /**
     * Step 1 – Build the order and redirect to checkout page.
     * Called when customer clicks "Proceed to Pay" on menu page.
     */
    @PostMapping("/checkout")
    @Transactional
    public String checkout(@RequestParam Long restaurantId,
                           @RequestParam List<Long> itemIds,
                           @RequestParam List<Integer> quantities,
                           @RequestParam PaymentMethod paymentMethod,
                           @AuthenticationPrincipal UserDetails ud,
                           Model model) {
        try {
            Customer customer = userService.findCustomerByEmail(ud.getUsername()).orElseThrow();
            Order order = orderService.placeOrder(customer, restaurantId,
                    itemIds, quantities, paymentMethod, restaurantService);

            model.addAttribute("order", order);
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("customer", customer);
            return "payment/checkout";
        } catch (Exception e) {
            return "redirect:/customer/restaurant/" + restaurantId + "/menu?error=true";
        }
    }

    /**
     * Step 2 – AJAX endpoint simulating payment processing.
     * 90% success rate. Returns JSON {status: "success"|"failed"}.
     */
    @PostMapping("/process")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> process(
            @RequestParam Long orderId,
            @RequestParam String cardOrUpiInput) {

        // Simulate processing delay done client-side (JS handles the wait)
        // Specific test inputs that always succeed or fail
        boolean success;
        String input = cardOrUpiInput.trim().replaceAll("\\s", "");

        if (input.equals("0000000000000000") || input.equals("fail@upi")) {
            success = false; // force failure for testing
        } else if (input.startsWith("4") || input.contains("@") || input.length() >= 6) {
            success = new Random().nextInt(10) > 0; // 90% success
        } else {
            success = false;
        }

        Order order = orderService.getById(orderId);
        Payment payment = order.getPayment();

        if (payment != null) {
            if (success) {
                payment.makePayment();  // COMPLETED
            } else {
                payment.refundPayment(); // FAILED
            }
            paymentRepository.save(payment);
        }

        return ResponseEntity.ok(Map.of(
            "status", success ? "success" : "failed",
            "orderId", orderId,
            "txnId", "TXN" + System.currentTimeMillis()
        ));
    }
}
