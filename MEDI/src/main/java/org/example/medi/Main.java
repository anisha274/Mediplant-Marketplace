package org.example.medi;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

public class Main extends Application {
    private static final String APP_TITLE = "Plant Marketplace";
    private static final String ICON_PATH = "/org/example/medi/image/plant.png";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;

    private static final String[] ICON_SIZES = {
            "/org/example/medi/image/plant_1024x1024.svg"
    };

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize user files and logout
            UserManager.initializeUserFiles();
            UserManager.logout();

            // Load FXML
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/org/example/medi/landing.fxml")
            ));

            // Set scene
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);

            InputStream iconStream = getClass().getResourceAsStream("/org/example/medi/image/plant.png");
            if (iconStream != null) {
                BufferedImage dockIcon = ImageIO.read(iconStream);
                Taskbar.getTaskbar().setIconImage(dockIcon);
            } else {
                System.err.println("Dock icon not found in resources.");
            }

            // Set window icon (cross-platform)
            Platform.runLater(() -> {
                try {
                    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                        loadMacOSIcons(primaryStage);
                    } else {
                        loadApplicationIcons(primaryStage);
                    }
                } catch (Exception e) {
                    System.err.println("Error setting window icon: " + e.getMessage());
                }
            });

            // Show window
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Application startup failed:");
            e.printStackTrace();
            Platform.exit();
        }
    }
    private void loadApplicationIcons(Stage stage) {
        for (String sizePath : ICON_SIZES) {
            try (InputStream is = getClass().getResourceAsStream(sizePath)) {
                if (is != null) {
                    stage.getIcons().add(new Image(is));
                }
            } catch (Exception e) {
                System.err.printf("Error loading icon %s: %s%n", sizePath, e.getMessage());
            }
        }

        if (stage.getIcons().isEmpty()) {
            loadDefaultIcon(stage);
        }
    }

    private void loadMacOSIcons(Stage stage) {
        String[] macPriorityOrder = {
                "/org/example/medi/image/plant_1024x1024.png",
        };

        for (String sizePath : macPriorityOrder) {
            try (InputStream is = getClass().getResourceAsStream(sizePath)) {
                if (is != null) {
                    stage.getIcons().add(new Image(is));
                    break;
                }
            } catch (Exception e) {
                System.err.printf("Error loading macOS icon %s: %s%n", sizePath, e.getMessage());
            }
        }

        if (stage.getIcons().isEmpty()) {
            loadApplicationIcons(stage);
        }
    }

    private void loadDefaultIcon(Stage stage) {
        try (InputStream is = getClass().getResourceAsStream(ICON_PATH)) {
            if (is != null) {
                stage.getIcons().add(new Image(is));
                System.out.println("Loaded default icon from: " + ICON_PATH);
            } else {
                System.err.println("Default icon not found at: " + ICON_PATH);
            }
        } catch (Exception e) {
            System.err.println("Error loading default icon: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_TITLE);
        }

        launch(args);
    }
}
