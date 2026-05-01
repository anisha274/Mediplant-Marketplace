package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

public class CreateAccountBuyerController {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(".*@.*");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @FXML public Button doneButton;
    @FXML public Button backButton;
    @FXML public TextField roleField;
    @FXML private TextField usernameField;
    @FXML private TextField addressField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    @FXML
    private void handleDone(ActionEvent event) {
        try {
            // Get form data
            String username = usernameField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            // Debug output
            System.out.println("Creating buyer account with:");
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Address: " + address);

            // Basic validation
            if (username.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Missing Fields", "All fields are required!");
                return;
            }

            if (!isValidUsername(username)) {
                showAlert("Invalid Username", "Username must be 3–20 characters (letters, numbers, underscores)");
                return;
            }

            // Simplified email validation - only check for @ symbol
            if (!isValidEmail(email)) {
                showAlert("Invalid Email", "Email must contain @ symbol");
                return;
            }

            if (password.length() < 6) {
                showAlert("Weak Password", "Password must be at least 6 characters");
                return;
            }

            if (address.length() < 10) {
                showAlert("Invalid Address", "Please enter a more detailed address");
                return;
            }

            // Create user with buyer role - FIXED: Use proper constructor
            User newUser = new User(username, email, password, "buyer");
            newUser.setAddress(address);
            newUser.setPhone(""); // Set empty phone for buyer
            newUser.setShopName(null); // No shop for buyer
            newUser.setShopLocation(null); // No shop location for buyer

            System.out.println("User object created with role: " + newUser.getRole());

            // Save user to file
            UserManager.saveUser(newUser);
            System.out.println("User saved to file successfully");

            // Set as current user
            UserManager.setCurrentUser(newUser);
            System.out.println("Current user set in session");

            // Debug print to confirm role
            System.out.println("✅ Created user with role: " + newUser.getRole());

            // Switch to buyer dashboard
            System.out.println("Attempting to switch to buyer dashboard...");
            SceneManager.switchToDashboard((Node) event.getSource(), newUser.getRole());

        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            showAlert("Validation Error", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.switchTo("login.fxml", (Node) event.getSource());
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}