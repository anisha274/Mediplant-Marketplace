package org.example.medi;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class PlantSellerController {
    @FXML public Button browseBtn;
    @FXML public Button addBtn;
    @FXML public Button updateBtn;
    @FXML public Button deleteBtn;
    @FXML
    private ListView<HBox> plantList;
    @FXML
    private TextField nameField, descField, priceField, imageField, categoryField, ratingField, sellerShopField;

    private final String SELLER_FILE = "seller.txt", BUYER_FILE = "buyer.txt";
    private final ObservableList<HBox> fullPlantItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        plantList.setItems(fullPlantItems);
        loadPlants();
    }

    @FXML
    private void loadPlants() {
        fullPlantItems.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(SELLER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                addToList(line);
            }
        } catch (IOException e) {
            System.err.println("No plants loaded: " + e.getMessage());
        }
    }

    @FXML
    public void handleBrowse(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Plant Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(null);
        if (file != null) imageField.setText(file.getAbsolutePath());
    }

    @FXML
    public void handleProfile(ActionEvent event) {
        try {
            User currentUser = UserManager.getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "No user logged in");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/medi/profileSeller.fxml"));
            Parent root = loader.load();

            ProfileSellerController profileController = loader.getController();
            profileController.initializeWithUser(currentUser);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Failed to open profile: " + e.getMessage());
            showAlert("Error", "Could not open profile view.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent e) throws IOException {
        UserManager.logout();
        SceneManager.switchTo("login.fxml", (Node) e.getSource());
    }

    @FXML
    public void addPlant() {
        String name = nameField.getText().trim(),
                desc = descField.getText().trim(),
                price = priceField.getText().trim(),
                imagePath = imageField.getText().trim(),
                category = categoryField.getText().trim(),
                rating = ratingField.getText().trim(),
                shop = sellerShopField.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || price.isEmpty() || category.isEmpty() || rating.isEmpty() || shop.isEmpty()) {
            showAlert("Error", "All fields required");
            return;
        }

        try {
            int r = Integer.parseInt(rating);
            if (r < 0 || r > 5) {
                showAlert("Error", "Rating must be between 0 and 5");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Rating must be a number");
            return;
        }

        String entry = String.join("::", name, desc, price, imagePath, category, rating, shop);
        addToList(entry);
        savePlants();
        copyToBuyerFile();
        clearFields();
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        descField.clear();
        priceField.clear();
        imageField.clear();
        categoryField.clear();
        ratingField.clear();
        sellerShopField.clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void copyToBuyerFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SELLER_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(BUYER_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Failed to sync buyer file: " + e.getMessage());
        }
    }

    @FXML
    private void savePlants() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SELLER_FILE))) {
            for (HBox hb : fullPlantItems) {
                VBox vb = (VBox) hb.getChildren().get(1);
                Label l = (Label) vb.getChildren().getFirst();
                String[] lines = l.getText().split("\n");

                String name = lines[0].replace("Name: ", "").trim();
                String category = lines[1].replace("Category: ", "").trim();
                String price = lines[2].replace("Price: ", "").trim();
                String ratingStars = lines[3].replace("Rating: ", "").trim();
                String imagePath = lines[4].replace("Image: ", "").trim();
                String shop = lines[5].replace("Shop: ", "").trim();
                String desc = lines[6].replace("Description: ", "").trim();
                String rating = String.valueOf(ratingStars.length());

                String entry = String.join("::", name, desc, price, imagePath, category, rating, shop);
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save plants: " + e.getMessage());
        }
    }
@FXML
    private void addToList(String entry) {
        String[] parts = entry.split("::");
        if (parts.length < 7) return;

        String name = parts[0], desc = parts[1], price = parts[2], imagePath = parts[3],
                category = parts[4], rating = parts[5], shop = parts[6];

        ImageView img = new ImageView();
        try {
            img.setImage(new Image(new FileInputStream(imagePath)));
        } catch (Exception e) {
            img.setImage(new Image("file:default.png"));
        }
        img.setFitHeight(80);
        img.setFitWidth(80);

        Label info = new Label(
                "Name: " + name + "\n" +
                        "Category: " + category + "\n" +
                        "Price: " + price + "\n" +
                        "Rating: " + "*".repeat(Integer.parseInt(rating)) + "\n" +
                        "Image: " + imagePath + "\n" +
                        "Shop: " + shop + "\n" +
                        "Description: " + desc
        );
        VBox vb = new VBox(info);
        HBox hb = new HBox(10, img, vb);
        fullPlantItems.add(hb);
    }
@FXML
    private void addToList(String entry, int i) {
        String[] parts = entry.split("::");
        if (parts.length < 7) return;

        String name = parts[0], desc = parts[1], price = parts[2], imagePath = parts[3],
                category = parts[4], rating = parts[5], shop = parts[6];

        ImageView img = new ImageView();
        try {
            img.setImage(new Image(new FileInputStream(imagePath)));
        } catch (Exception e) {
            img.setImage(new Image("file:default.png"));
        }
        img.setFitHeight(80);
        img.setFitWidth(80);

        Label info = new Label(
                "Name: " + name + "\n" +
                        "Category: " + category + "\n" +
                        "Price: " + price + "\n" +
                        "Rating: " + "*".repeat(Integer.parseInt(rating)) + "\n" +
                        "Image: " + imagePath + "\n" +
                        "Shop: " + shop + "\n" +
                        "Description: " + desc
        );
        VBox vb = new VBox(info);
        HBox hb = new HBox(10, img, vb);
        fullPlantItems.set(i, hb);
    }

    @FXML
    private void showUpdateDialog(int index, String name, String desc, String price, String imagePath, String category, String rating, String shop) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Plant Details");

        // Create fields pre-filled with existing data
        TextField nameField = new TextField(name);
        TextField descField = new TextField(desc);
        TextField priceField = new TextField(price);
        TextField imageField = new TextField(imagePath);
        TextField categoryField = new TextField(category);
        TextField ratingField = new TextField(rating);
        TextField shopField = new TextField(shop);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1); grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2); grid.add(priceField, 1, 2);
        grid.add(new Label("Image Path:"), 0, 3); grid.add(imageField, 1, 3);
        grid.add(new Label("Category:"), 0, 4); grid.add(categoryField, 1, 4);
        grid.add(new Label("Rating (0–5):"), 0, 5); grid.add(ratingField, 1, 5);
        grid.add(new Label("Shop:"), 0, 6); grid.add(shopField, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String updatedEntry = String.join("::",
                        nameField.getText().trim(),
                        descField.getText().trim(),
                        priceField.getText().trim(),
                        imageField.getText().trim(),
                        categoryField.getText().trim(),
                        ratingField.getText().trim(),
                        shopField.getText().trim()
                );
                addToList(updatedEntry, index);
                savePlants();
                copyToBuyerFile();
            }
        });
    }
    @FXML
    public void updatePlant() {
        int i = plantList.getSelectionModel().getSelectedIndex();
        if (i < 0) {
            showAlert("Error", "Select a plant to update");
            return;
        }

        HBox hb = fullPlantItems.get(i);
        VBox vb = (VBox) hb.getChildren().get(1);
        Label l = (Label) vb.getChildren().getFirst();
        String[] lines = l.getText().split("\n");

        String name = lines[0].replace("Name: ", "").trim();
        String category = lines[1].replace("Category: ", "").trim();
        String price = lines[2].replace("Price: ", "").trim();
        String ratingStars = lines[3].replace("Rating: ", "").trim();
        String imagePath = lines[4].replace("Image: ", "").trim();
        String shop = lines[5].replace("Shop: ", "").trim();
        String desc = lines[6].replace("Description: ", "").trim();
        String rating = String.valueOf(ratingStars.length());

        showUpdateDialog(i, name, desc, price, imagePath, category, rating, shop);
    }


    @FXML
    public void deletePlant() {
        int i = plantList.getSelectionModel().getSelectedIndex();
        if (i < 0) {
            showAlert("Error", "Select a plant to delete");
            return;
        }

        fullPlantItems.remove(i);
        savePlants(); // Persist the updated list
        copyToBuyerFile(); // Sync with buyer view if needed

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Delete Successful");
        success.setHeaderText(null);
        success.setContentText("Plant deleted successfully!");
        success.showAndWait();
    }

}