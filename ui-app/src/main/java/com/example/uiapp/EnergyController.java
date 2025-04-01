package com.example.uiapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EnergyController {

    @FXML
    private Label counterText;
    private int counter = 0;
    public TextField firstNumberInput;
    public TextField secondNumberInput;
    @FXML
    private Label resultText;


    @FXML
    protected void onCounterTextButtonClick() {
        counter++;
        counterText.setText("Number of clicks: " + counter);
    }

    @FXML
    protected void onAddButtonClick() {
        int firstNumber = Integer.parseInt(firstNumberInput.getText());
        int secondNumber = Integer.parseInt(secondNumberInput.getText());
        int result = firstNumber + secondNumber;
        resultText.setText("Result: " + result);
    }
}