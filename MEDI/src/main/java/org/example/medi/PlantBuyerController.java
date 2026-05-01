package org.example.medi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantBuyerController {
    @FXML public Button searchButton;
    @FXML private VBox plantListVBox;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;

    private final List<HBox> fullPlantItems = new ArrayList<>();
    private static final String BUYER_FILE = "buyer.txt";
    private static final String DEFAULT_IMAGE_PATH = "/default_plant.png";
    private RadioButton bkashRadioButton;

    @FXML
    public void initialize() {
        categoryFilter.getItems().addAll("All", "Ferns", "Medicinal", "Herbs", "Flowers", "Herbal");
        categoryFilter.setValue("All");
        categoryFilter.setOnAction(this::handleCategoryFilterChange);
        searchField.textProperty().addListener(this::handleSearchTextChange);
        loadPlantsFromFile();
    }

    public void refreshPlants() {
        loadPlantsFromFile();
    }

    private void handleCategoryFilterChange(ActionEvent event) {
        applyFilters();
    }

    private void handleSearchTextChange(javafx.beans.Observable observable, String oldValue, String newValue) {
        applyFilters();
    }

    private void loadPlantsFromFile() {
        fullPlantItems.clear();
        plantListVBox.getChildren().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(BUYER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addPlantToList(line);
            }
            plantListVBox.getChildren().addAll(fullPlantItems);
        } catch (Exception e) {
            System.err.println("Failed to load plants: " + e.getMessage());
            plantListVBox.getChildren().add(new Label("Failed to load plants. Please try again later."));
        }

        if (fullPlantItems.isEmpty()) {
            plantListVBox.getChildren().add(new Label("No plants available."));
        }
    }

    private void addPlantToList(String entry) {
        if (entry == null || entry.trim().isEmpty()) return;

        String[] parts = entry.split("::");
        if (parts.length < 7) {
            System.err.println("Invalid plant entry: " + entry);
            return;
        }

        String name = parts[0];
        String desc = parts[1];
        String price = parts[2];
        String imagePath = parts[3];
        String category = parts[4];
        String rating = parts[5];
        String shop = parts[6];

        ImageView imageView = createPlantImageView(imagePath);
        Label nameLabel = createNameLabel(name);
        Label categoryLabel = createCategoryLabel(category);
        Label priceLabel = createPriceLabel(price);
        Label ratingLabel = createRatingLabel(rating);
        Label shopLabel = createShopLabel(shop);
        Label descLabel = createDescLabel(desc);

        Button cartBtn = createCartButton(name, desc, price, shop);
        Button buyBtn = createBuyButton(name, desc, price, shop);

        HBox buttonBox = new HBox(10, cartBtn, buyBtn);
        VBox textBox = createTextVBox(nameLabel, categoryLabel, priceLabel, ratingLabel, shopLabel, descLabel, buttonBox);
        HBox fullEntry = createPlantEntryHBox(imageView, textBox);

        fullPlantItems.add(fullEntry);
    }

    private ImageView createPlantImageView(String imagePath) {
        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:" + imagePath, 150, 150, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Image load failed for: " + imagePath);
            loadDefaultImage(imageView);
        }
        return imageView;
    }

    private void loadDefaultImage(ImageView imageView) {
        try (InputStream defaultImageStream = Objects.requireNonNull(
                getClass().getResourceAsStream(DEFAULT_IMAGE_PATH))) {
            Image defaultImage = new Image(defaultImageStream);
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Default image also failed to load");
        }
    }

    private Label createNameLabel(String name) {
        Label label = new Label("🌿 " + name);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    private Label createCategoryLabel(String category) {
        return new Label("Category: " + category);
    }

    private Label createPriceLabel(String price) {
        return new Label("Price: " + price + " TK");
    }

    private Label createRatingLabel(String rating) {
        return new Label("Rating: " + createRatingStars(rating));
    }

    private Label createShopLabel(String shop) {
        return new Label("Shop: " + shop);
    }

    private Label createDescLabel(String desc) {
        Label label = new Label(desc.isEmpty() ? "No description provided." : desc);
        label.setWrapText(true);
        label.setStyle("-fx-font-style: italic;");
        return label;
    }

    private String createRatingStars(String rating) {
        try {
            int ratingValue = Integer.parseInt(rating);
            return "⭐".repeat(Math.min(5, Math.max(0, ratingValue)));
        } catch (NumberFormatException e) {
            return "⭐";
        }
    }

    private Button createCartButton(String name, String desc, String price, String shop) {
        Button button = new Button("Add to Cart");
        button.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        button.setOnAction(e -> addToCart(name, desc, price, shop));
        return button;
    }

    private Button createBuyButton(String name, String desc, String price, String shop) {
        Button button = new Button("Buy Now");
        button.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        button.setOnAction(e -> showBuyDialog(name, desc, price, shop));
        return button;
    }

    private VBox createTextVBox(Node... children) {
        VBox vbox = new VBox(5, children);
        vbox.setPrefWidth(400);
        vbox.setPadding(new Insets(5));
        vbox.setStyle("-fx-font-size: 13px;");
        return vbox;
    }

    private HBox createPlantEntryHBox(ImageView imageView, VBox textBox) {
        HBox hbox = new HBox(10, imageView, textBox);
        hbox.setPadding(new Insets(10));
        hbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");
        return hbox;
    }

    private void addToCart(String name, String desc, String price, String shop) {
        String itemInfo = String.format("Name: %s, Desc: %s, Price: %s, Shop: %s", name, desc, price, shop);
        CartManager.addToCart(itemInfo);
        showAlert("Added to Cart", name + " has been added to your cart.");
    }

    private void showBuyDialog(String name, String desc, String price, String shop) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Buy Plant - " + name);

        Label infoLabel = new Label("Do you want to buy this plant directly?");
        CheckBox directBuyCheck = new CheckBox("Buy now with payment");
        directBuyCheck.setSelected(true);

        Label methodLabel = new Label("Choose Payment Method:");
        RadioButton cashOption = new RadioButton("Cash");
        this.bkashRadioButton = new RadioButton("Bkash");
        ToggleGroup paymentGroup = new ToggleGroup();
        cashOption.setToggleGroup(paymentGroup);
        bkashRadioButton.setToggleGroup(paymentGroup);
        cashOption.setSelected(true);

        TextField bkashField = new TextField();
        bkashField.setPromptText("Bkash Number");
        bkashField.setVisible(false);

        bkashRadioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            bkashField.setVisible(newVal);
            if (newVal) bkashField.requestFocus();
        });

        ButtonType confirmBtn = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtn, cancelBtn);

        VBox content = new VBox(10, infoLabel, directBuyCheck, methodLabel,
                new HBox(10, cashOption, bkashRadioButton), bkashField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmBtn) {
                handlePurchase(directBuyCheck, paymentGroup, bkashField, name, desc, price, shop);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handlePurchase(CheckBox directBuyCheck, ToggleGroup paymentGroup,
                                TextField bkashField, String name, String desc, String price, String shop) {
        if (directBuyCheck.isSelected()) {
            RadioButton selected = (RadioButton) paymentGroup.getSelectedToggle();
            String method = selected.getText();
            String bkashNumber = bkashRadioButton.isSelected() ? bkashField.getText().trim() : null;

            if (bkashRadioButton.isSelected() && (bkashNumber == null || bkashNumber.isEmpty())) {
                showAlert("Bkash Number Required", "Please enter your Bkash number.");
                return;
            }

            String buyerName = UserManager.getCurrentUser().getUsername();
            String itemInfo = String.format("Name: %s, Desc: %s, Price: %s, Shop: %s", name, desc, price, shop);

            CartManager.addToCart(itemInfo);
            CartManager.saveCart(buyerName, method, bkashNumber);
            CartManager.updatePaycheck(Double.parseDouble(price));
            CartManager.clearCart();
            showAlert("Payment Successful", "Your payment for " + name + " has been processed.");
        } else {
            addToCart(name, desc, price, shop);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyFilters() {
        String query = searchField.getText().toLowerCase();
        String category = categoryFilter.getValue();

        plantListVBox.getChildren().clear();

        boolean hasMatches = false;
        for (HBox item : fullPlantItems) {
            VBox textBox = (VBox) item.getChildren().get(1);
            Label nameLabel = (Label) textBox.getChildren().get(0);
            Label categoryLabel = (Label) textBox.getChildren().get(1);

            String nameText = nameLabel.getText().toLowerCase().replace("🌿 ", "");
            String categoryText = categoryLabel.getText().toLowerCase().replace("category: ", "");

            boolean matchesSearch = query.isEmpty() || nameText.contains(query);
            boolean matchesCategory = category.equals("All") || categoryText.equals(category.toLowerCase());

            if (matchesSearch && matchesCategory) {
                plantListVBox.getChildren().add(item);
                hasMatches = true;
            }
        }

        if (!hasMatches) {
            plantListVBox.getChildren().add(new Label("No plants found matching your criteria."));
        }
    }

    @FXML
    public void searchPlantByName(ActionEvent event) {
        applyFilters();
    }

    @FXML
    public void handleViewCart(ActionEvent event) {
        SceneManager.switchTo("cart.fxml", (Node) event.getSource());
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserManager.logout();
        SceneManager.switchTo("login.fxml", (Node) event.getSource());
    }

    @FXML
    public void handleProfile(ActionEvent event) {
        try {
            User currentUser = UserManager.getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "No user logged in");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/medi/profileBuyer.fxml"));
            Parent root = loader.load();

            ProfileBuyerController profileController = loader.getController();
            profileController.initializeWithUser(currentUser);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Failed to open profile: " + e.getMessage());
            showAlert("Error", "Could not open profile view.");
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        refreshPlants();
    }
}
