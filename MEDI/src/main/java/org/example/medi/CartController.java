package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class CartController {
    @FXML private ListView<HBox> cartList;
    @FXML private TextField usernameField;

    @FXML
    public void initialize() {
        refreshCartDisplay();
    }

    private void refreshCartDisplay() {
        cartList.getItems().clear();
        List<String> items = CartManager.getCartItems();

        for (String item : items) {
            Label label = new Label(item);
            Button deleteBtn = new Button("Delete");
            deleteBtn.setOnAction(e -> {
                CartManager.removeItem(item);
                refreshCartDisplay();
            });

            HBox row = new HBox(10, label, deleteBtn);
            cartList.getItems().add(row);
        }

        if (items.isEmpty()) {
            cartList.getItems().add(new HBox(new Label(" Cart is empty.")));
        }
    }

    @FXML
    private void handleCheckout(ActionEvent event) {
        String buyerName = usernameField.getText().trim();
        if (buyerName.isEmpty()) {
            showAlert("Missing Username", "Please enter your username before checkout.");
            return;
        }

        double totalAmount = CartManager.getTotalAmount();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Checkout");

        // UI Elements
        Label totalLabel = new Label("Total Amount: " + String.format("%.2f", totalAmount));
        Label methodLabel = new Label("Choose Payment Method:");

        RadioButton cashOption = new RadioButton("Cash");
        RadioButton bkashOption = new RadioButton("Bkash");
        ToggleGroup paymentGroup = new ToggleGroup();
        cashOption.setToggleGroup(paymentGroup);
        bkashOption.setToggleGroup(paymentGroup);
        cashOption.setSelected(true);

        TextField bkashField = new TextField();
        bkashField.setPromptText("Bkash Number");
        bkashField.setVisible(false);

        bkashOption.selectedProperty().addListener((obs, oldVal, newVal) -> {
            bkashField.setVisible(newVal);
        });

        ButtonType payButtonType = new ButtonType("Pay", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(payButtonType, cancelButtonType);

        VBox content = new VBox(10, totalLabel, methodLabel, new HBox(10, cashOption, bkashOption), bkashField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == payButtonType) {
                RadioButton selected = (RadioButton) paymentGroup.getSelectedToggle();
                String method = selected.getText();
                String bkashNumber = bkashOption.isSelected() ? bkashField.getText().trim() : null;

                if (bkashOption.isSelected() && (bkashNumber == null || bkashNumber.isEmpty())) {
                    showAlert("Bkash Number Required", "Please enter your Bkash number.");
                    return null;
                }

                CartManager.saveCart(buyerName, method, bkashNumber);
                CartManager.updatePaycheck(totalAmount);
                CartManager.clearCart();
                refreshCartDisplay();
                usernameField.clear();
                showAlert("Payment Successful", "? Your payment has been processed.");
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        SceneManager.switchTo("plantbuyer.fxml", (Node) event.getSource());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
