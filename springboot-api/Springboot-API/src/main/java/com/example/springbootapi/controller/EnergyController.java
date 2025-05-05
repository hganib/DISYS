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


@RestController
public class EnergyController {
    private final EnergyCurrentRepository energyCurrentRepository;
    private final EnergyHistoricalRepository energyHistoricalRepository;
    public EnergyController(EnergyCurrentRepository energyCurrentRepository, EnergyHistoricalRepository energyHistoricalRepository) {
        this.energyCurrentRepository = energyCurrentRepository;
        this.energyHistoricalRepository = energyHistoricalRepository;
    }


    @GetMapping("/energy/current") //http://localhost:8080/energy/current
    public EnergyCurrent currentEnergy() {
        return energyCurrentRepository.findTopByOrderByTimestampDesc();
    }

    @GetMapping("/energy/historical") //http://localhost:8080/energy/historical?start=2025-01-01&end=2025-03-01
    public EnergyHistorical historicalEnergy(@RequestParam("start") LocalDateTime start,
                                                   @RequestParam("end") LocalDateTime end) {
        List<Object[]> results = energyHistoricalRepository.sumHistoricalByDateRange(start, end);

        if (results.isEmpty() || results.get(0) == null) {
            return new EnergyHistorical(3340, 0, 0, start, end);
        }

        Object[] result = results.get(0);

        double communityProduced = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
        double communityUsed     = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
        double gridUsed          = result[2] != null ? ((Number) result[2]).doubleValue() : 0.0;

        return new EnergyHistorical(communityProduced, communityUsed, gridUsed, start, end);
    }
}
