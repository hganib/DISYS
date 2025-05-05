package com.example.uiapp;

import com.example.uiapp.entity.EnergyCurrent;
import com.example.uiapp.entity.EnergyHistorical;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ApiController {

    @FXML
    private Label responseCommunityPool;
    @FXML
    private Label responseGridPortion;
    @FXML
    private Label responseCommunityProduced;
    @FXML
    private Label responseCommunityUsed;
    @FXML
    private Label responseGridUsed;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private HttpClient client;
    private ObjectMapper objectMapper;

    public ApiController() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    @FXML
    protected void refreshCurrentEnergy() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:8080/energy/current"))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

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
            String start = startDatePicker.getValue().atStartOfDay().toString();  // -> LocalDate to LocalDateTime
            String end = endDatePicker.getValue().plusDays(1).atStartOfDay().toString();


            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:8080/energy/historical?start=" + start + "&end=" + end))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

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