package com.example.uiapp;

import com.example.uiapp.entity.EnergyCurrent;
import com.example.uiapp.entity.EnergyHistorical;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ApiController implements Initializable {

    @FXML private Label responseCommunityPool;
    @FXML private Label responseGridPortion;
    @FXML private Label responseCommunityProduced;
    @FXML private Label responseCommunityUsed;
    @FXML private Label responseGridUsed;

    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;

    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public ApiController() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
    }

    @FXML
    protected void refreshCurrentEnergy() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:8080/energy/current"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            EnergyCurrent energyCurrent = objectMapper.readValue(response.body(), EnergyCurrent.class);

            responseCommunityPool.setText("Community Pool: %s%%".formatted(energyCurrent.getCommunityPool()));
            responseGridPortion.setText("Grid Portion: %s%%".formatted(energyCurrent.getGridPortion()));
        } catch (Exception e) {
            e.printStackTrace();
            responseCommunityPool.setText("Error: " + e.getMessage());
            responseGridPortion.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void refreshHistoricalEnergy() {
        try {
            LocalDateTime start = startDatePicker.getValue().atTime(startHourSpinner.getValue(), 0);
            LocalDateTime end = endDatePicker.getValue().atTime(endHourSpinner.getValue(), 0);

            String startParam = start.toString();
            String endParam = end.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:8080/energy/historical?start=" + startParam + "&end=" + endParam))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            EnergyHistorical energyHistorical = objectMapper.readValue(response.body(), EnergyHistorical.class);

            responseCommunityProduced.setText("Community produced: %s kWh".formatted(energyHistorical.getCommunityProduced()));
            responseCommunityUsed.setText("Community used: %s kWh".formatted(energyHistorical.getCommunityUsed()));
            responseGridUsed.setText("Grid used: %s kWh".formatted(energyHistorical.getGridUsed()));
        } catch (Exception e) {
            e.printStackTrace();
            responseCommunityPool.setText("Error: " + e.getMessage());
            responseGridPortion.setText("Error: " + e.getMessage());
            responseCommunityProduced.setText("Error: " + e.getMessage());
        }
    }
}