package org.example.medi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OrderManager {
    public static void placeOrder(User buyer, User seller, String productName, double price, int quantity) throws IOException {
        // Save order to orders.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt", true))) {
            String orderData = String.join(",",
                    buyer.getEmail(),
                    seller.getEmail(),
                    productName,
                    String.valueOf(quantity),
                    String.valueOf(price),
                    "PENDING", // status
                    java.time.LocalDate.now().toString()
            );
            writer.write(orderData);
            writer.newLine();
        }

        seller.addNotification("📦 New order for '" + productName + "' from " + buyer.getUsername());
        UserManager.saveUser(seller);
    }

    public static void confirmOrder(User seller, User buyer, String productName) {
        // Update order status (optional)
        buyer.addNotification("✅ Your order for '" + productName + "' has been confirmed by " + seller.getUsername());
        UserManager.saveUserDetails(buyer);
    }
}
