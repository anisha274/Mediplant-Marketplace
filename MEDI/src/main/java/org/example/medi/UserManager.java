package org.example.medi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String BUYER_FILE = "buyer_info.txt";
    private static final String SELLER_FILE = "seller_info.txt";
    private static User currentUser;

    // Initialize files on startup
    public static void initializeUserFiles() {
        try {
            File buyerFile = new File(BUYER_FILE);
            File sellerFile = new File(SELLER_FILE);

            if (!buyerFile.exists()) buyerFile.createNewFile();
            if (!sellerFile.exists()) sellerFile.createNewFile();

            System.out.println("User files initialized.");
        } catch (IOException e) {
            System.err.println("File initialization error: " + e.getMessage());
        }
    }

    // Save a new user to the appropriate file - FIXED
    public static void saveUser(User user) {
        String filePath = getFilePathByRole(user.getRole());

        System.out.println("Saving user: " + user.getEmail() + ", Role: " + user.getRole());

        // Check if user already exists
        if (userExists(user.getEmail(), user.getRole())) {
            System.out.println("User already exists: " + user.getEmail());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // SIMPLIFIED FORMAT: username,email,password,role,phone,address,shopName,shopLocation,profileImage
            String line = String.join(",",
                    user.getUsername(),           // 0
                    user.getEmail(),              // 1
                    user.getPassword(),           // 2 - Store plain text password temporarily
                    user.getRole(),               // 3
                    user.getPhone(),              // 4
                    user.getAddress(),            // 5
                    user.getShopName() != null ? user.getShopName() : "N/A",          // 6
                    user.getShopLocation() != null ? user.getShopLocation() : "N/A",  // 7
                    user.getProfileImagePath() != null ? user.getProfileImagePath() : "default_profile.png"  // 8
            );
            writer.write(line);
            writer.newLine();
            System.out.println("User saved successfully: " + user.getEmail());
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load all user details - FIXED
    public static List<User> loadAllUserDetails(String role) {
        List<User> users = new ArrayList<>();
        String filePath = getFilePathByRole(role);
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("User file does not exist yet: " + filePath);
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                System.out.println("DEBUG - Loading line: " + line);
                System.out.println("DEBUG - Parts length: " + parts.length);

                if (parts.length >= 9) {
                    // Create user with proper constructor
                    User user = new User(
                            parts[0], // username
                            parts[4], // phone
                            parts[5], // address
                            parts[6], // shopName
                            parts[7], // shopLocation
                            parts[1], // email
                            parts[2], // password
                            parts[3]  // role
                    );

                    user.setProfileImagePath(parts[8]);

                    users.add(user);
                    System.out.println("Loaded user: " + user.getEmail() + ", Role: " + user.getRole());
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    // Authenticate user - FIXED
    public static User getAuthenticatedUser(String email, String password, String role) {
        List<User> allUsers = loadAllUserDetails(role);

        for (User user : allUsers) {
            if (user.getEmail().equalsIgnoreCase(email) &&
                    user.getPassword().equals(password)) { // Compare plain text passwords
                return user;
            }
        }
        return null;
    }

    // Save user details - FIXED
    public static boolean saveUserDetails(User user) {
        String filePath = getFilePathByRole(user.getRole());
        List<User> users = loadAllUserDetails(user.getRole());
        boolean updated = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equalsIgnoreCase(user.getEmail())) {
                users.set(i, user);
                updated = true;
                break;
            }
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (User u : users) {
                    String line = String.join(",",
                            u.getUsername(),           // 0
                            u.getEmail(),              // 1
                            u.getPassword(),           // 2
                            u.getRole(),               // 3
                            u.getPhone(),              // 4
                            u.getAddress(),            // 5
                            u.getShopName() != null ? u.getShopName() : "N/A",          // 6
                            u.getShopLocation() != null ? u.getShopLocation() : "N/A",  // 7
                            u.getProfileImagePath() != null ? u.getProfileImagePath() : "default_profile.png"  // 8
                    );
                    writer.write(line);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error saving updated user details: " + e.getMessage());
            }
        }
        return false;
    }

    // Check if user exists by role
    public static boolean userExists(String email, String role) {
        List<User> users = loadAllUserDetails(role);
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    // Find user by email and role
    public static User findUserByEmail(String email, String role) {
        List<User> users = loadAllUserDetails(role);
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                return user;
            }
        }
        return null;
    }

    // Get file path based on role
    private static String getFilePathByRole(String role) {
        if ("buyer".equalsIgnoreCase(role)) return BUYER_FILE;
        else if ("seller".equalsIgnoreCase(role)) return SELLER_FILE;
        else throw new IllegalArgumentException("Invalid role: " + role);
    }

    // Current user management
    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("Current user set: " + (user != null ? user.getEmail() : "null"));
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}