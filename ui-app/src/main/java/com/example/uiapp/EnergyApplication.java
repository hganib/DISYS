package com.example.uiapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main entry point for the JavaFX UI application.
 * <p>
 * It loads the FXML layout, sets up the primary stage, and shows the window.
 */
public class EnergyApplication extends Application {

    /**
     * Called by the JavaFX runtime when the application is launched.
     *
     * @param stage the primary stage (window) provided by JavaFX
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML layout from the resource file
        FXMLLoader fxmlLoader = new FXMLLoader(EnergyApplication.class.getResource("energy-view.fxml"));

        // Create a Scene using the loaded layout, with initial size 800x600
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Set the window title displayed on the stage
        stage.setTitle("Energy Application");

        // Attach the scene to the stage
        stage.setScene(scene);

        // Display the stage
        stage.show();
    }

    /**
     * Standard Java main method. Delegates to the JavaFX Application.launch
     * which initializes the JavaFX toolkit and calls start().
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        launch();
    }
}