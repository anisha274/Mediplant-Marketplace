package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Node;

public class BuyerDashboardController {


    @FXML public Button notificationBtn;

    @FXML
    private void handleNotification() {
        User buyer = UserManager.getCurrentUser();
        String messages = String.join("\n", buyer.getNotifications());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Updates");
        alert.setHeaderText("Seller Confirmations");
        alert.setContentText(messages.isEmpty() ? "No updates yet." : messages);
        alert.showAndWait();

        buyer.clearNotifications();
        UserManager.saveUserDetails(buyer);
    }
    @FXML
    public void handleViewProfile(ActionEvent actionEvent) {
        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null && "Seller".equalsIgnoreCase(currentUser.getRole())) {
            SceneManager.switchTo("profileSeller.fxml", (Node) actionEvent.getSource());
        } else {
            SceneManager.switchTo("profileBuyer.fxml", (Node) actionEvent.getSource());
        }
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        UserManager.logout();
        SceneManager.switchTo("login.fxml", (Node) actionEvent.getSource());
    }

    @FXML
    public void handleViewProducts(ActionEvent actionEvent) {
        SceneManager.switchTo("plantbuyer.fxml", (Node) actionEvent.getSource());
    }

    @FXML
    public void handleViewOrders(ActionEvent actionEvent) {
        SceneManager.switchTo("buyer_orders.fxml", (Node) actionEvent.getSource());
    }
}
