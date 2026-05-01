package org.example.medi;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;

public class LandingController {
    private static final String APP_TITLE = "MEDI-Plant Marketplace";
    private static final String SELLER_ROLE = "seller";
    private static final String BUYER_ROLE = "buyer";
    private static final String APP_ICON_PATH = "/org/example/medi/image/plant.png";
    @FXML public ImageView appLogoImage;
    @FXML public Button buyerButton;
    @FXML public Button sellerButton;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) sellerButton.getScene().getWindow();
                stage.setTitle(APP_TITLE);

                InputStream iconStream = getClass().getResourceAsStream(APP_ICON_PATH);
                if (iconStream != null) {
                    stage.getIcons().add(new Image(iconStream));
                } else {
                    System.err.println("Icon not found at path: " + APP_ICON_PATH);
                }
            } catch (Exception e) {
                System.err.println("Error setting window icon: " + e.getMessage());
            }
        });
    }

    @FXML
    private void goToSellerPage(ActionEvent event) {
        handleRoleSelection(event, SELLER_ROLE);
    }

    @FXML
    private void goToBuyerPage(ActionEvent event) {
        handleRoleSelection(event, BUYER_ROLE);
    }

    @FXML
    private void handleRoleSelection(ActionEvent event, String role) {
        try {
            if (!SELLER_ROLE.equals(role) && !BUYER_ROLE.equals(role)) {
                throw new IllegalArgumentException("Invalid role specified");
            }

            // store selected role globally (optional, in case you want default selection in login)
            RoleContext.setSelectedRole(role);

            // Always go to the same login page
            SceneManager.switchTo("login.fxml", (Node) event.getSource());

        } catch (IllegalArgumentException e) {
            showAlert("Selection Error", "Invalid role selection. Please try again.", event);
        } catch (Exception e) {
            showAlert("Navigation Error", "Failed to load the login screen. Please try again.", event);
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message, ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(String.format("%s - %s", APP_TITLE, title));
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            InputStream iconStream = getClass().getResourceAsStream(APP_ICON_PATH);
            if (iconStream != null) {
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alert.initOwner(((Node) event.getSource()).getScene().getWindow());
                alertStage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Alert icon not found at path: " + APP_ICON_PATH);
            }
        } catch (Exception e) {
            System.err.println("Error loading alert icon: " + e.getMessage());
        }

        alert.showAndWait();
    }
}