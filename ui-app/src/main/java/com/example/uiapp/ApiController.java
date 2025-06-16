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

/**
 * Controller for the JavaFX UI that calls the Spring Boot API to fetch
 * current and historical energy data and displays the results in labels.
 */
public class ApiController implements Initializable {

    // FXML-injected labels for displaying current energy percentages
    @FXML private Label responseCommunityPool;
    @FXML private Label responseGridPortion;

    // FXML-injected labels for displaying historical energy metrics
    @FXML private Label responseCommunityProduced;
    @FXML private Label responseCommunityUsed;
    @FXML private Label responseGridUsed;

    // FXML-injected controls for selecting historical date/time range
    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;

    // HTTP client for sending REST requests
    private final HttpClient client;
    // Jackson ObjectMapper configured with JavaTimeModule for LocalDateTime
    private final ObjectMapper objectMapper;

    /**
     * Constructor initializes the HTTP client and JSON mapper.
     */
    public ApiController() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Called by JavaFX after FXML fields are injected.
     * Sets up the hour spinners to range from 0 to 23.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
    }

    /**
     * Handler for the "Refresh Current" button.
     * Sends an HTTP GET to /energy/current and updates the UI labels with the result.
     */
    @FXML
    protected void refreshCurrentEnergy() {
        try {
            // Build the HTTP GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:8080/energy/current"))
                    .build();

            // Perform the request and get the JSON body as a string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Deserialize JSON into EnergyCurrent DTO
            EnergyCurrent energyCurrent = objectMapper.readValue(response.body(), EnergyCurrent.class);

            // Update labels with formatted percentages
            responseCommunityPool.setText("Community Pool: %s%%".formatted(energyCurrent.getCommunityPool()));
            responseGridPortion.setText("Grid Portion: %s%%".formatted(energyCurrent.getGridPortion()));
        } catch (Exception e) {
            // Log the error and update labels to indicate failure
            e.printStackTrace();
            responseCommunityPool.setText("Error: " + e.getMessage());
            responseGridPortion.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Handler for the "Refresh Historical" button.
     * Reads start/end from the date pickers and spinners,
     * calls /energy/historical?start=...&end=..., and updates UI labels.
     */
    @FXML
    protected void refreshHistoricalEnergy() {
        try {
            // Construct LocalDateTime for start and end based on UI controls
            LocalDateTime start = startDatePicker.getValue().atTime(startHourSpinner.getValue(), 0);
            LocalDateTime end = endDatePicker.getValue().atTime(endHourSpinner.getValue(), 0);

            // Convert to ISO string parameters
            String startParam = start.toString();
            String endParam   = end.toString();

            // Build the HTTP GET request with query parameters
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:8080/energy/historical?start=" + startParam + "&end=" + endParam))
                    .build();

            // Perform the request and parse JSON into EnergyHistorical DTO
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            EnergyHistorical energyHistorical = objectMapper.readValue(response.body(), EnergyHistorical.class);

            // Update UI labels with formatted kWh values
            responseCommunityProduced.setText(
                    String.format("Community produced: %.3f kWh", energyHistorical.getCommunityProduced())
            );
            responseCommunityUsed.setText(
                    String.format("Community used: %.3f kWh", energyHistorical.getCommunityUsed())
            );
            responseGridUsed.setText(
                    String.format("Grid used: %.3f kWh", energyHistorical.getGridUsed())
            );
        } catch (Exception e) {
            // On error, log and display error on all relevant labels
            e.printStackTrace();
            responseCommunityPool.setText("Error: " + e.getMessage());
            responseGridPortion.setText("Error: " + e.getMessage());
            responseCommunityProduced.setText("Error: " + e.getMessage());
        }
    }
}