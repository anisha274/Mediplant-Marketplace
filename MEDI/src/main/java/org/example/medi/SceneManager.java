package org.example.medi;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Stack;

public class SceneManager {

    private static final String BASE_PATH = "/org/example/medi/";
    private static final Stack<Scene> sceneHistory = new Stack<>();

    // 🔄 Core scene switching method
    private static void switchScene(Window window, String fxmlFile, String title) throws IOException {
        String fullPath = fxmlFile.startsWith("/") ? fxmlFile : BASE_PATH + fxmlFile;
        URL resource = SceneManager.class.getResource(fullPath);

        if (resource == null) {
            throw new IOException("FXML file not found at: " + fullPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Scene newScene = new Scene(root);

        if (window instanceof Stage stage) {
            stage.setScene(newScene);
            if (title != null) stage.setTitle(title);
            stage.show();
        } else {
            Scene currentScene = window.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            }
        }

        System.out.println("✅ Loaded scene: " + fullPath);
    }

    // 🔁 Switch to a new scene with optional title
    public static void switchTo(String fxmlFile, Node sourceNode, String title) {
        Objects.requireNonNull(sourceNode, "Source node cannot be null");
        Objects.requireNonNull(fxmlFile, "FXML file cannot be null");

        Scene currentScene = sourceNode.getScene();
        if (currentScene == null) {
            throw new IllegalStateException("Source node is not part of a scene");
        }

        Window window = currentScene.getWindow();
        if (window == null) {
            throw new IllegalStateException("Scene is not attached to a window");
        }

        sceneHistory.push(currentScene);
        try {
            switchScene(window, fxmlFile, title);
        } catch (Exception e) {
            showError("Could not load: " + fxmlFile + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔁 Overload without title
    public static void switchTo(String fxmlFile, Node sourceNode) {
        switchTo(fxmlFile, sourceNode, null);
    }

    // 🔙 Return to log in screen with role-based title
    public static void switchToLogin(Node sourceNode, String role) {
        String title = "seller".equalsIgnoreCase(role) ? "Seller Login" : "Buyer Login";
        switchTo("login.fxml", sourceNode, title);
    }

    // ⏪ Go back to previous scene
    public static void switchToPreviousScene() {
        if (sceneHistory.isEmpty()) {
            throw new IllegalStateException("No previous scene in history");
        }

        try {
            Scene previousScene = sceneHistory.pop();
            Window window = previousScene.getWindow();
            if (window != null) {
                if (window instanceof Stage stage) {
                    stage.setScene(previousScene);
                    stage.show();
                } else {
                    Scene currentScene = window.getScene();
                    if (currentScene != null) {
                        currentScene.setRoot(previousScene.getRoot());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore previous scene", e);
        }
    }

    // 🧭 Dashboard switcher based on role - FIXED VERSION
    public static void switchToDashboard(Node sourceNode, String role) {
        String fxmlFile;
        String title;
        System.out.println("🔄 Requested dashboard switch for role: " + role);

        if ("seller".equalsIgnoreCase(role)) {
            fxmlFile = "seller_dashboard.fxml";
            title = "Seller Dashboard";
        } else if ("buyer".equalsIgnoreCase(role)) {
            fxmlFile = "buyer_dashboard.fxml";
            title = "Buyer Dashboard";
        } else {
            showError("❌ Unknown role: " + role);
            return;
        }

        try {
            System.out.println("Attempting to load dashboard: " + fxmlFile);

            // Use the same approach as switchTo() method
            Scene currentScene = sourceNode.getScene();
            if (currentScene == null) {
                throw new IllegalStateException("Source node is not part of a scene");
            }

            Window window = currentScene.getWindow();
            if (window == null) {
                throw new IllegalStateException("Scene is not attached to a window");
            }

            // Push current scene to history
            sceneHistory.push(currentScene);

            // Load and switch to the dashboard
            switchScene(window, fxmlFile, title);

            System.out.println("✅ Successfully switched to: " + fxmlFile);

        } catch (Exception e) {
            System.out.println("❌ Failed to switch to dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load dashboard: " + e.getMessage());

            // Fallback: redirect to log in if dashboard fails
            switchToLogin(sourceNode, role);
        }
    }

    // ⚠️ Error alert
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Scene Switch Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}