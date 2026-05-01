package org.example.medi;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class ProfileBuyerController {
    @FXML private ImageView profileImageView;
    @FXML private Button changeImageBtn, editBtn, saveBtn, backBtn;
    @FXML private TextField emailField, usernameField, phoneField, addressField, roleField;
    private User currentUser;

    public void initialize() {
        currentUser = UserManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("No user found in session.");
            return;
        }
        emailField.setText(currentUser.getEmail());
        usernameField.setText(currentUser.getUsername());
        phoneField.setText(currentUser.getPhone());
        addressField.setText(currentUser.getAddress());
        roleField.setText(currentUser.getRole());

        emailField.setEditable(false);
        roleField.setEditable(false);
        usernameField.setEditable(false);
        phoneField.setEditable(false);
        addressField.setEditable(false);

        changeImageBtn.setDisable(true);
        saveBtn.setVisible(false);

        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            try {
                File file = new File(currentUser.getProfileImagePath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                }
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        usernameField.setEditable(true);
        phoneField.setEditable(true);
        addressField.setEditable(true);
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

            if (username.isEmpty()) {
                showErrorAlert("Validation Error", "Username cannot be empty");
                return;
            }
            if (address.isEmpty()) {
                showErrorAlert("Validation Error", "Address cannot be empty");
                return;
            }

            // Update current user with form data
            currentUser.setUsername(username);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);

            // Save updated user
            boolean success = UserManager.saveUserDetails(currentUser);

            if (success) {
                // Save to file for backup
                saveUserToFile(currentUser);

                // Update UI using refreshUserData method
                refreshUserData(currentUser);

                // Update UI state
                usernameField.setEditable(false);
                phoneField.setEditable(false);
                addressField.setEditable(false);
                changeImageBtn.setDisable(true);
                saveBtn.setVisible(false);
                editBtn.setDisable(false);

                // Update session
                UserManager.setCurrentUser(currentUser);
                showSuccessAlert("Success", "Profile updated successfully!");
            } else {
                showErrorAlert("Update Failed", "Unable to update profile. Please try again.");
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
                UserManager.saveUserDetails(currentUser);

                // Update UI with new image using refreshUserData
                refreshUserData(currentUser);

                showSuccessAlert("Image Updated", "Profile image updated successfully!");
            } catch (Exception e) {
                showErrorAlert("Image Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            SceneManager.switchTo("buyer_dashboard.fxml", backBtn);
        } catch (Exception e) {
            getStage().close();
        }
    }

    private Stage getStage() {
        return (Stage) backBtn.getScene().getWindow();
    }

    public void initializeWithUser(User user) {
        this.currentUser = user;
        initialize();
    }

    private void refreshUserData(User updatedUser) {
        usernameField.setText(updatedUser.getUsername());
        phoneField.setText(updatedUser.getPhone());
        addressField.setText(updatedUser.getAddress());
        if (updatedUser.getProfileImagePath() != null && !updatedUser.getProfileImagePath().isEmpty()) {
            try {
                File file = new File(updatedUser.getProfileImagePath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                }
            } catch (Exception e) {
                System.err.println("Error refreshing profile image: " + e.getMessage());
            }
        }
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

    private void saveUserToFile(User user) {
        File file = new File("buyer_info.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String line = String.join(",",
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getAddress(),
                    user.getRole(),
                    user.getProfileImagePath() != null ? user.getProfileImagePath() : "N/A"
            );
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            showErrorAlert("File Error", "Could not save user data: " + e.getMessage());
        }
    }
}