package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML public Button loginButton;
    @FXML public Button createAccountButton;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.setItems(FXCollections.observableArrayList("Buyer", "Seller"));

        String preselectedRole = RoleContext.getSelectedRole(); // from LandingController
        if (preselectedRole != null) {
            roleComboBox.setValue(capitalize(preselectedRole));
        }
    }

    private String capitalize(String role) {
        return role.substring(0,1).toUpperCase() + role.substring(1).toLowerCase();
    }


    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (role == null || email.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields and select a role.");
            return;
        }

        try {
            User user = UserManager.getAuthenticatedUser(email, password, role.toLowerCase());
            if (user != null) {
                UserManager.setCurrentUser(user);
                String targetView = "seller".equalsIgnoreCase(role) ? "seller_dashboard.fxml" : "buyer_dashboard.fxml";
                SceneManager.switchTo(targetView, (Node) event.getSource());
            } else {
                showAlert("Incorrect email or password.");
            }
        } catch (Exception e) {
            showAlert("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void goToCreateAccount(ActionEvent event) {
        String role = roleComboBox.getValue();

        if (role == null) {
            showAlert("Please select a role before creating an account.");
            return;
        }

        String targetView = "Buyer".equalsIgnoreCase(role) ? "create_account_buyer.fxml" : "create_account_seller.fxml";
        SceneManager.switchTo(targetView, (Node) event.getSource());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}