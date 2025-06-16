/**
 * Module declaration for the UI application.
 *
 * Defines required modules and controls visibility of packages for reflection.
 */
module com.example.uiapp {

    // JavaFX UI controls (Button, Label, Spinner, etc.)
    requires javafx.controls;

    // JavaFX FXML support for loading .fxml layout files
    requires javafx.fxml;

    // HTTP client API for making REST requests to backend
    requires java.net.http;

    // Jackson core library for JSON (de)serialization
    requires com.fasterxml.jackson.databind;

    // Jackson module to handle Java 8+ Date/Time types (LocalDateTime)
    requires com.fasterxml.jackson.datatype.jsr310;

    // Allow the 'com.example.uiapp' package to be reflectively accessed by JavaFX FXMLLoader
    opens com.example.uiapp to javafx.fxml;

    // Export application packages so they can be accessed by other modules (if needed)
    exports com.example.uiapp;
    exports com.example.uiapp.entity;
}