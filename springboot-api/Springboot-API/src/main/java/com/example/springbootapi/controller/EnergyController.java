package com.example.springbootapi.controller;

import com.example.springbootapi.entity.EnergyCurrent;
import com.example.springbootapi.entity.EnergyHistorical;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class EnergyController {
    @GetMapping("/energy/current") //http://localhost:8080/energy/current
    public EnergyCurrent currentEnergy() {
        double communityPool = ThreadLocalRandom.current().nextDouble(0, 100);
        double gridPortion = ThreadLocalRandom.current().nextDouble(0, 100);

        communityPool = Math.round(communityPool * 100.0) / 100.0;
        gridPortion = Math.round(gridPortion * 100.0) / 100.0;

        return new EnergyCurrent(communityPool, gridPortion);
    }

    @GetMapping("/energy/historical") //http://localhost:8080/energy/historical?start=2025-01-01&end=2025-03-01
    public EnergyHistorical historicalEnergy(@RequestParam("start") String start,
                                   @RequestParam("end") String end) {
        double communityProduced = ThreadLocalRandom.current().nextDouble(0, 10000);
        double communityUsed = ThreadLocalRandom.current().nextDouble(0, 10000);
        double gridUsed = ThreadLocalRandom.current().nextDouble(0, 10000);

        communityProduced = Math.round(communityProduced * 100.0) / 100.0;
        communityUsed = Math.round(communityUsed * 100.0) / 100.0;
        gridUsed = Math.round(gridUsed * 100.0) / 100.0;

        return new EnergyHistorical(communityProduced, communityUsed, gridUsed);
    }
}
