package org.example.medi;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuyerOrdersController {

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, String> productColumn;

    @FXML
    private TableColumn<Order, String> sellerColumn;

    @FXML
    private TableColumn<Order, Integer> quantityColumn;

    @FXML
    private TableColumn<Order, Double> priceColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.switchTo("buyer_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    public void initialize() {
        // Set up table columns
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        sellerColumn.setCellValueFactory(new PropertyValueFactory<>("sellerEmail"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load orders from orders.txt and populate the table
        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = new ArrayList<>();
        User currentUser = UserManager.getCurrentUser();

        if (currentUser == null) {
            System.out.println("No user logged in");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[0].equals(currentUser.getEmail())) {
                    Order order = new Order(
                            parts[0], // buyerEmail
                            parts[1], // sellerEmail
                            parts[2], // productName
                            Integer.parseInt(parts[3]), // quantity
                            Double.parseDouble(parts[4]), // price
                            parts[5], // status
                            parts[6]  // orderDate
                    );
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing order data: " + e.getMessage());
        }

        ordersTable.getItems().setAll(orders);
    }
}