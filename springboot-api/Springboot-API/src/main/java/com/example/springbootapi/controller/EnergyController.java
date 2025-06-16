package com.example.springbootapi.controller;

import com.example.springbootapi.entity.EnergyCurrent;
import com.example.springbootapi.entity.EnergyHistorical;
import com.example.springbootapi.repository.EnergyCurrentRepository;
import com.example.springbootapi.repository.EnergyHistoricalRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller exposing endpoints for retrieving current and historical energy data.
 */
@RestController
public class EnergyController {
    /** Repository for accessing the latest energy percentage data. */
    private final EnergyCurrentRepository energyCurrentRepository;
    /** Repository for querying aggregated historical energy records. */
    private final EnergyHistoricalRepository energyHistoricalRepository;

    /**
     * Constructor-based dependency injection of JPA repositories.
     *
     * @param energyCurrentRepository     repository for current energy data
     * @param energyHistoricalRepository  repository for historical energy data
     */
    public EnergyController(EnergyCurrentRepository energyCurrentRepository, EnergyHistoricalRepository energyHistoricalRepository) {
        this.energyCurrentRepository = energyCurrentRepository;
        this.energyHistoricalRepository = energyHistoricalRepository;
    }

    /**
     * Endpoint to retrieve the most recent EnergyCurrent record.
     *
     * HTTP GET /energy/current
     *
     * @return the latest hourly percentage split between community and grid usage
     */
    @GetMapping("/energy/current") //http://localhost:8080/energy/current
    public EnergyCurrent currentEnergy() {
        // Finds the single record with the highest 'hour' value (i.e., newest)
        return energyCurrentRepository.findTopByOrderByHourDesc();
    }


    /**
     * Endpoint to retrieve aggregated historical energy data for a date range.
     *
     * HTTP GET /energy/historical?start={start}&end={end}
     *
     * @param start start timestamp (inclusive) for the aggregation window
     * @param end   end timestamp (inclusive) for the aggregation window
     * @return an EnergyHistorical object containing summed production, consumption, and grid usage
     */
    @GetMapping("/energy/historical") //http://localhost:8080/energy/historical?start=2025-01-01&end=2025-03-01
    public EnergyHistorical historicalEnergy(@RequestParam("start") LocalDateTime start,
                                                   @RequestParam("end") LocalDateTime end) {

        // Execute custom repository query: SUM(community_produced), SUM(community_used), SUM(grid_used)
        List<Object[]> results = energyHistoricalRepository.sumHistoricalByDateRange(start, end);

        // If the query returns no data, return a default placeholder record
        if (results.isEmpty() || results.get(0) == null) {
            // Example fallback:
            return new EnergyHistorical(9999, 9999, 9999, start);
        }

        // Extract aggregated values from the first result row
        Object[] result = results.get(0);
        double communityProduced = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
        double communityUsed     = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
        double gridUsed          = result[2] != null ? ((Number) result[2]).doubleValue() : 0.0;

        // Construct and return a new EnergyHistorical DTO with the summed values
        return new EnergyHistorical(communityProduced, communityUsed, gridUsed, start);
    }
}
