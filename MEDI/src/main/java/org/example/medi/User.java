package org.example.medi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(".*@.*");

    private String email;
    private String role;
    private boolean isSeller;
    private final boolean active;
    private String password; // Changed from final to allow setting
    private String username;
    private String phone;
    private String address;
    private String shopName;
    private String shopLocation;
    private String profileImagePath;
    private final List<String> notifications = new ArrayList<>();

    // Seller constructor - FIXED VERSION
    public User(String username, String phone, String address, String shopName,
                String shopLocation, String email, String password, String role) {
        this.username = safe(username);
        this.phone = safe(phone);
        this.address = safe(address, "Not specified");
        this.shopName = safe(shopName);
        this.shopLocation = safe(shopLocation);
        this.email = validateEmail(email);
        this.password = safe(password);
        setRole(role);
        this.profileImagePath = "default_profile.png";
        this.active = true;
    }

    // Buyer constructor
    public User(String username, String email, String password, String role) {
        this.username = safe(username);
        this.email = validateEmail(email);
        this.password = safe(password);
        setRole(role);
        this.phone = "";
        this.address = "Not specified";
        this.shopName = isSeller ? "" : null;
        this.shopLocation = isSeller ? "" : null;
        this.profileImagePath = "default_profile.png";
        this.active = true;
    }

    // File-based constructor
    public User(String username, String phone, String address, String email,
                String role, boolean isSeller, String imagePath) {

        this.username = safe(username);
        this.phone = safe(phone);
        this.address = safe(address, "Not specified");
        this.email = validateEmail(email);
        this.password = "placeholder";
        setRole(role);
        this.isSeller = isSeller;
        this.shopName = isSeller ? "" : null;
        this.shopLocation = isSeller ? "" : null;
        this.profileImagePath = (imagePath != null && !imagePath.isEmpty()) ? imagePath : "default_profile.png";
        this.active = true;
    }

    // Default constructor
    public User() {
        this.username = "";
        this.phone = "";
        this.address = "Not specified";
        this.email = "";
        this.password = "";
        this.role = "buyer";
        this.isSeller = false;
        this.shopName = null;
        this.shopLocation = null;
        this.profileImagePath = "default_profile.png";
        this.active = true;
    }

    // Validation helpers
    private String validateEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).find())
            return "invalid@example.com";
        return email.trim().toLowerCase();
    }

    private String validateRole(String role) {
        if (role == null || role.trim().isEmpty()) return "buyer";
        if (!"buyer".equalsIgnoreCase(role) && !"seller".equalsIgnoreCase(role))
            throw new IllegalArgumentException("Role must be 'buyer' or 'seller'");
        return role.toLowerCase();
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String safe(String value, String fallback) {
        return value != null ? value : fallback;
    }

    // Getters
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isSeller() { return isSeller; }
    public String getProfileImagePath() { return profileImagePath; }
    public String getUsername() { return username; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getShopName() { return shopName; }
    public String getShopLocation() { return shopLocation; }
    public List<String> getNotifications() { return new ArrayList<>(notifications); }
    public String getPassword() { return password; } // Added getter

    // Setters
    public void setEmail(String email) { this.email = validateEmail(email); }
    public void setRole(String role) {
        this.role = validateRole(role);
        this.isSeller = "seller".equalsIgnoreCase(this.role);
    }
    public void setProfileImagePath(String path) {
        this.profileImagePath = (path != null && !path.isEmpty()) ? path : "default_profile.png";
    }
    public void setUsername(String username) { this.username = safe(username); }
    public void setPhone(String phone) { this.phone = safe(phone); }
    public void setAddress(String address) { this.address = safe(address, "Not specified"); }
    public void setShopName(String shopName) {
        if (isSeller) this.shopName = safe(shopName);
    }
    public void setShopLocation(String shopLocation) {
        if (isSeller) this.shopLocation = safe(shopLocation);
    }
    public void setPassword(String password) { this.password = safe(password); } // Added setter

    // Notifications
    public void addNotification(String message) {
        if (message != null && !message.trim().isEmpty()) notifications.add(message.trim());
    }
    public void clearNotifications() { notifications.clear(); }

    // Debugging
    @Override
    public String toString() {
        return String.format("User[username=%s, email=%s, role=%s, active=%s, seller=%s]",
                username, email, role, active, isSeller);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(email, user.email) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, role);
    }
}