module com.example.uiapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens com.example.uiapp to javafx.fxml;
    exports com.example.uiapp;
    exports com.example.uiapp.entity;
}