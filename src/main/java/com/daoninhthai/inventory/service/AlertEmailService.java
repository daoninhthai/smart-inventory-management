package com.daoninhthai.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEmailService {

    private final JavaMailSender mailSender;

    public void sendStockAlert(String[] recipients, String productName, String sku,
                               String warehouseName, int currentQty, int threshold) {
        String subject = "Low Stock Alert: " + productName + " (" + sku + ")";
        String body = String.format(
                "Low stock alert for product: %s (SKU: %s)\n\n" +
                "Warehouse: %s\n" +
                "Current Quantity: %d\n" +
                "Threshold: %d\n\n" +
                "Please take action to reorder this product.",
                productName, sku, warehouseName, currentQty, threshold
        );
        sendEmail(recipients, subject, body);
    }

    public void sendOrderStatusAlert(String[] recipients, String orderNumber, String status) {
        String subject = "Order Status Update: " + orderNumber;
        String body = String.format(
                "Purchase order %s has been updated.\n\n" +
                "New Status: %s\n\n" +
                "Please review the order in the inventory system.",
                orderNumber, status
        );
        sendEmail(recipients, subject, body);
    }

    public void sendExpiryAlert(String[] recipients, String productName, String sku,
                                String warehouseName, int daysUntilExpiry) {
        String subject = "Expiry Alert: " + productName + " (" + sku + ")";
        String body = String.format(
                "Product expiry warning for: %s (SKU: %s)\n\n" +
                "Warehouse: %s\n" +
                "Days until expiry: %d\n\n" +
                "Please review and take necessary action.",
                productName, sku, warehouseName, daysUntilExpiry
        );
        sendEmail(recipients, subject, body);
    }

    private void sendEmail(String[] recipients, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipients);
            message.setSubject("[Smart Inventory] " + subject);
            message.setText(body);
            message.setFrom("noreply@smart-inventory.com");
            mailSender.send(message);
            log.info("Alert email sent: subject='{}', recipients={}", subject, recipients.length);
        } catch (Exception e) {
            log.error("Failed to send alert email: subject='{}'", subject, e);
        }
    }
}
