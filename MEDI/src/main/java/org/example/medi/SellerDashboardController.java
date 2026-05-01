package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class SellerDashboardController {

    @FXML public Button notificationBtn;

    @FXML
    private void handleNotification() {
        User seller = UserManager.getCurrentUser();
        if (seller == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No user");
            alert.setHeaderText(null);
            alert.setContentText("No user is currently logged in.");
            alert.showAndWait();
            return;
        }

        java.util.List<String> notes = seller.getNotifications();
        String messages = (notes == null || notes.isEmpty()) ? "" : String.join("\n", notes);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Notifications");
        alert.setHeaderText("New Orders");
        alert.setContentText(messages.isEmpty() ? "No new orders." : messages);
        alert.showAndWait();

        seller.clearNotifications();
        UserManager.saveUser(seller); // use the actual save method you have
    }

    @FXML
    public void handleViewProfile(ActionEvent actionEvent) {
        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null && "seller".equalsIgnoreCase(currentUser.getRole())) {
            SceneManager.switchTo("/org/example/medi/profileSeller.fxml", (Node) actionEvent.getSource());
        } else {
            SceneManager.switchTo("/org/example/medi/profileBuyer.fxml", (Node) actionEvent.getSource());
        }
    }

    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        UserManager.logout();
        SceneManager.switchTo("/org/example/medi/login.fxml", (Node) actionEvent.getSource());
    }

    @FXML
    public void handleViewOrders(ActionEvent actionEvent) {
        SceneManager.switchTo("/org/example/medi/seller_orders.fxml", (Node) actionEvent.getSource());
    }

    @FXML
    public void handleUploadProduct(ActionEvent actionEvent) {
        SceneManager.switchTo("/org/example/medi/plantseller.fxml", (Node) actionEvent.getSource());
    }

}