module com.example.uiapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.uiapp to javafx.fxml;
    exports com.example.uiapp;
}