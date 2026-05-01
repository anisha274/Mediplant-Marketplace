package org.example.medi;

public class RoleContext {

    // Combined fields from both versions
    private static volatile String currentRole = "";

    public static synchronized void setSelectedRole(String role) {
        validateRole(role);
        currentRole = role.toLowerCase().trim();
    }

    public static synchronized boolean isSeller() {
        return "seller".equals(currentRole);
    }

    public static synchronized boolean isBuyer() {
        return "buyer".equals(currentRole);
    }

    // Validation methods
    private static void validateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role must be either 'buyer' or 'seller'");
        }
        String normalized = role.toLowerCase().trim();
        if (!"buyer".equals(normalized) && !"seller".equals(normalized)) {
            throw new IllegalArgumentException("Invalid role specified");
        }
    }

    public static String getSelectedRole() {
        return currentRole;
    }
}