package com.example.uiapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label counterText;
    private int counter = 0;

    @FXML
    protected void onCounterTextButtonClick() {
        counter++;
        counterText.setText("Number of clicks: " + counter);
    }
}