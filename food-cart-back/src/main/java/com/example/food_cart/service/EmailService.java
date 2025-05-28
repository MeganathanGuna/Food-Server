package com.example.food_cart.service;

import com.example.food_cart.model.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderEmail(OrderRequest order) {
        try {
            logger.info("Preparing to send email for order from {}", order.getCustomerName());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("keshoreaws@gmail.com");
            message.setSubject("New Food Order from " + order.getCustomerName());

            StringBuilder content = new StringBuilder();
            content.append("Customer: ").append(order.getCustomerName()).append("\n");
            content.append("Items Ordered:\n");

            double totalCost = 0.0;
            for (String item : order.getItems()) {
                content.append(" - ").append(item).append("\n");
                // Parse item string: "ItemName - ₹Price x Quantity"
                try {
                    String[] parts = item.split(" - ₹| x ");
                    if (parts.length == 3) {
                        double price = Double.parseDouble(parts[1]);
                        int quantity = Integer.parseInt(parts[2]);
                        totalCost += price * quantity;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse item for cost calculation: {}", item);
                }
            }
            content.append("Total Items: ").append(order.getItems().size()).append("\n");
            content.append("Total Cost: ₹").append(String.format("%.2f", totalCost)).append("\n");

            message.setText(content.toString());
            mailSender.send(message);
            logger.info("Email sent successfully to keshoreaws@gmail.com");
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}