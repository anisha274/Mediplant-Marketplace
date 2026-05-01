package org.example.medi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<String> cartItems = new ArrayList<>();
    private static final String ORDER_FILE = "orders.txt";
    private static final String PAYCHECK_FILE = "paycheck.txt";

    public static void addToCart(String plantInfo) {
        cartItems.add(plantInfo);
    }

    public static List<String> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public static void removeItem(String item) {
        cartItems.remove(item);
    }

    public static double getTotalAmount() {
        double total = 0.0;
        for (String item : cartItems) {
            try {
                String[] tokens = item.split(",");
                for (String token : tokens) {
                    token = token.trim();
                    if (token.contains("Price:")) {
                        String[] parts = token.split("Price:");
                        if (parts.length > 1) {
                            String priceStr = parts[1].trim().replaceAll("[^\\d.]", "");
                            total += Double.parseDouble(priceStr);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Could not parse price from item: " + item);
            }
        }
        return total;
    }

    public static void saveCart(String buyerUsername, String paymentMethod, String bkashNumber) {
        try (FileWriter writer = new FileWriter(ORDER_FILE, true)) {
            writer.write("=== Order ===\n");
            writer.write("Buyer: " + buyerUsername + "\n");
            writer.write("Payment: " + paymentMethod + (bkashNumber != null ? " (" + bkashNumber + ")" : "") + "\n");
            for (String item : cartItems) {
                writer.write(" - " + item + "\n");
            }
            writer.write("Total: " + String.format("%.2f", getTotalAmount()) + " TK\n\n");
        } catch (IOException e) {
            System.out.println("❌ Could not save cart: " + e.getMessage());
        }
    }

    public static void updatePaycheck(double amount) {
        try (FileWriter writer = new FileWriter(PAYCHECK_FILE, true)) {
            writer.write("Received: " + String.format("%.2f", amount) + " TK\n");
        } catch (IOException e) {
            System.out.println("❌ Could not update paycheck: " + e.getMessage());
        }
    }

    public static void clearCart() {
        cartItems.clear();
    }
}