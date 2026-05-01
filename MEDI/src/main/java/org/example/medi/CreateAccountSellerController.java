package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

public class CreateAccountSellerController {
    // Simplified email pattern - only checks for @ symbol
    private static final Pattern EMAIL_PATTERN = Pattern.compile(".*@.*");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    @FXML public Button doneButton;
    @FXML public Button backButton;
    @FXML public TextField roleField;
    @FXML private TextField usernameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField shopNameField;
    @FXML private TextField shopLocationField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    @FXML
    private void handleDone(ActionEvent event) {
        try {
            // Get form data
            String username = usernameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String shopName = shopNameField.getText().trim();
            String shopLocation = shopLocationField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            // Debug output
            System.out.println("Creating seller account with:");
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phone);
            System.out.println("Shop: " + shopName);

            // Validation
            if (username.isEmpty() || phone.isEmpty() || address.isEmpty()
                    || shopName.isEmpty() || shopLocation.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "All fields are required!");
                return;
            }

            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showAlert("Invalid Email", "Please enter a valid email address.");
                return;
            }

            if (!PHONE_PATTERN.matcher(phone).matches()) {
                showAlert("Invalid Phone", "Please enter a valid phone number (10–15 digits)");
                return;
            }

            if (password.length() < 8) {
                showAlert("Weak Password", "Password must be at least 8 characters");
                return;
            }

            // Create seller user
            User newUser = new User(username, phone, address, shopName, shopLocation, email, password, "seller");

            System.out.println("User object created with role: " + newUser.getRole());

            // Save user to file
            UserManager.saveUser(newUser);
            System.out.println("User saved to file successfully");

            // Set as current user
            UserManager.setCurrentUser(newUser);
            System.out.println("Current user set in session");

            // Debug print to confirm role
            System.out.println("✅ Created seller user with role: " + newUser.getRole());

            // Switch to seller dashboard using SceneManager
            System.out.println("Attempting to switch to seller dashboard...");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}