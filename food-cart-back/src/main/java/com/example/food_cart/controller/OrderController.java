package com.example.food_cart.controller;

import com.example.food_cart.model.OrderRequest;
import com.example.food_cart.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/order")
    public ResponseEntity<Map<String, String>> raiseOrder(@RequestBody OrderRequest orderRequest) {
        if (orderRequest.getCustomerName() == null || orderRequest.getCustomerName().isEmpty() ||
                orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer name and items are required");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            emailService.sendOrderEmail(orderRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order placed successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to process order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}