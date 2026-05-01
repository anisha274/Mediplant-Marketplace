package org.example.medi;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox; // Added missing import
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellerOrdersController {

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, String> productColumn;

    @FXML
    private TableColumn<Order, String> buyerColumn;

    @FXML
    private TableColumn<Order, Integer> quantityColumn;

    @FXML
    private TableColumn<Order, Double> priceColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, Void> actionsColumn;

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.switchTo("seller_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    public void initialize() {
        // Set up table columns - LINK FXML FIELDS TO JAVA CODE
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        buyerColumn.setCellValueFactory(new PropertyValueFactory<>("buyerEmail"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Make status column editable
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statusColumn.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            order.setStatus(event.getNewValue());
            updateOrderStatus(order);
        });

        // Add action buttons column
        actionsColumn.setCellFactory(param -> new TableCell<>() { // Fixed diamond operator
            private final Button confirmBtn = new Button("Confirm");
            private final Button cancelBtn = new Button("Cancel");

            {
                confirmBtn.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    order.setStatus("CONFIRMED");
                    updateOrderStatus(order);
                    ordersTable.refresh();

                    // Send notification to buyer
                    User buyer = UserManager.findUserByEmail(order.getBuyerEmail(), "buyer");
                    if (buyer != null) {
                        buyer.addNotification("✅ Your order for '" + order.getProductName() + "' has been confirmed");
                        UserManager.saveUser(buyer);
                    }
                });


                cancelBtn.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    order.setStatus("CANCELLED");
                    updateOrderStatus(order);
                    ordersTable.refresh();

                    // Send notification to buyer
                    User buyer = UserManager.findUserByEmail(order.getBuyerEmail(), "buyer");
                    if (buyer != null) {
                        buyer.addNotification("❌ Your order for '" + order.getProductName() + "' has been cancelled");
                        UserManager.saveUser(buyer);
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    if ("PENDING".equals(order.getStatus())) {
                        setGraphic(new HBox(5, confirmBtn, cancelBtn));
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

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
                if (parts.length >= 7 && parts[1].equals(currentUser.getEmail())) {
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

    private void updateOrderStatus(Order updatedOrder) {
        List<String> allOrders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 &&
                        parts[0].equals(updatedOrder.getBuyerEmail()) &&
                        parts[1].equals(updatedOrder.getSellerEmail()) &&
                        parts[2].equals(updatedOrder.getProductName())) {
                    // Update this order
                    line = String.join(",",
                            updatedOrder.getBuyerEmail(),
                            updatedOrder.getSellerEmail(),
                            updatedOrder.getProductName(),
                            String.valueOf(updatedOrder.getQuantity()),
                            String.valueOf(updatedOrder.getPrice()),
                            updatedOrder.getStatus(),
                            updatedOrder.getOrderDate()
                    );
                }
                allOrders.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading orders: " + e.getMessage());
            return;
        }

        // Write all orders back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt"))) {
            for (String order : allOrders) {
                writer.write(order);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing orders: " + e.getMessage());
        }
    }
}