package org.example.medi;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProfileSellerController {
    @FXML private ImageView profileImageView;
    @FXML private Button changeImageBtn, editBtn, saveBtn, backBtn;
    @FXML private TextField emailField, usernameField, phoneField, addressField, roleField;
    @FXML private TextField shopNameField, shopLocationField;
    private User currentUser;

    // -------------------- Initialization --------------------
    public void initialize() {
        currentUser = UserManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("No user found in session.");
            return;
        }
        loadUserData(currentUser);
        setFieldsEditable(false);
        changeImageBtn.setDisable(true);
        saveBtn.setVisible(false);
        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            loadProfileImage(currentUser.getProfileImagePath());
        }
    }

    public void initializeWithUser(User user) {
        this.currentUser = user;
        initialize();
    }

    // -------------------- Event Handlers --------------------
    @FXML
    private void handleEdit() {
        setFieldsEditable(true);
        changeImageBtn.setDisable(false);
        saveBtn.setVisible(true);
        editBtn.setDisable(true);
    }

    @FXML
    private void handleSave() {
        try {
            // Validate required fields
            String username = usernameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String shopName = shopNameField.getText().trim();
            String shopLocation = shopLocationField.getText().trim();

            if (username.isEmpty()) {
                showErrorAlert("Validation Error", "Username cannot be empty");
                return;
            }
            if (address.isEmpty()) {
                showErrorAlert("Validation Error", "Address cannot be empty");
                return;
            }
            if (shopName.isEmpty()) {
                showErrorAlert("Validation Error", "Shop name cannot be empty");
                return;
            }
            if (shopLocation.isEmpty()) {
                showErrorAlert("Validation Error", "Shop location cannot be empty");
                return;
            }

            // Update current user with form data
            currentUser.setUsername(username);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            currentUser.setShopName(shopName);
            currentUser.setShopLocation(shopLocation);

            // Save data in your app storage (UserManager)
            boolean success = UserManager.saveUserDetails(currentUser);

            // Also persist to a text file
            saveUserToFile(currentUser);

            if (success) {
                // Update UI using refreshSellerDashboard method
                refreshSellerDashboard(currentUser);

                // Update UI state
                setFieldsEditable(false);
                changeImageBtn.setDisable(true);
                saveBtn.setVisible(false);
                editBtn.setDisable(false);
                UserManager.setCurrentUser(currentUser);
                showSuccessAlert("Success", "Profile updated successfully!");
            } else {
                showErrorAlert("Update Failed", "Failed to update profile. Please try again.");
            }
        } catch (Exception e) {
            showErrorAlert("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            try {
                profileImageView.setImage(new Image(file.toURI().toString()));
                currentUser.setProfileImagePath(file.getAbsolutePath());
                boolean success = UserManager.saveUserDetails(currentUser);
                if (success) {
                    // Update UI with new image using refreshSellerDashboard
                    refreshSellerDashboard(currentUser);
                    showSuccessAlert("Image Updated", "Profile image updated successfully!");
                    UserManager.setCurrentUser(currentUser);
                } else {
                    showErrorAlert("Save Failed", "Failed to save image changes.");
                }
            } catch (Exception e) {
                showErrorAlert("Image Error", "Failed to load image: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleBack() {
        try {
            SceneManager.switchTo("seller_dashboard.fxml", backBtn);
        } catch (Exception e) {
            getStage().close();
        }
    }

    // -------------------- Profile Updates --------------------
    private void refreshSellerDashboard(User updatedUser) {
        phoneField.setText(updatedUser.getPhone());
        addressField.setText(updatedUser.getAddress());
        shopNameField.setText(updatedUser.getShopName());
        shopLocationField.setText(updatedUser.getShopLocation());
        if (updatedUser.getProfileImagePath() != null && !updatedUser.getProfileImagePath().isEmpty()) {
            loadProfileImage(updatedUser.getProfileImagePath());
        }
    }

    // -------------------- Utility Methods --------------------
    private void loadUserData(User user) {
        emailField.setText(user.getEmail());
        usernameField.setText(user.getUsername());
        phoneField.setText(user.getPhone());
        addressField.setText(user.getAddress());
        roleField.setText(user.getRole());
        shopNameField.setText(user.getShopName());
        shopLocationField.setText(user.getShopLocation());
    }

    private void setFieldsEditable(boolean editable) {
        emailField.setEditable(false); // always non-editable
        usernameField.setEditable(editable);
        roleField.setEditable(false);
        phoneField.setEditable(editable);
        addressField.setEditable(editable);
        shopNameField.setEditable(editable);
        shopLocationField.setEditable(editable);
    }

    private void loadProfileImage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                profileImageView.setImage(new Image(file.toURI().toString()));
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }
    }

    private Stage getStage() {
        return (Stage) backBtn.getScene().getWindow();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // -------------------- File Persistence --------------------
    private void saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("seller_info.txt", true))) {
            writer.write("Email: " + user.getEmail());
            writer.newLine();
            writer.write("Username: " + user.getUsername());
            writer.newLine();
            writer.write("Phone: " + user.getPhone());
            writer.newLine();
            writer.write("Address: " + user.getAddress());
            writer.newLine();
            writer.write("Role: " + user.getRole());
            writer.newLine();
            writer.write("Shop Name: " + user.getShopName());
            writer.newLine();
            writer.write("Shop Location: " + user.getShopLocation());
            writer.newLine();
            writer.write("Profile Image Path: " + user.getProfileImagePath());
            writer.newLine();
            writer.write("---------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            showErrorAlert("File Save Error", "Failed to save user data to seller_info.txt: " + e.getMessage());
        }
    }
}