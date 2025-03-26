package com.example.springbootapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnergyController {
    @GetMapping("/energy/current") //http://localhost:8080/energy/current
    public String currentEnergy() {
        int energy = 1234;
        return String.format("Current Energy: %d KWh", energy);
    }

    @GetMapping("/energy/historical") //http://localhost:8080/energy/historical?start=2025-01-01&end=2025-03-01
    public String historicalEnergy(@RequestParam("start") String start,
                                   @RequestParam("end") String end) {
        int energy = 2346743;
        return String.format("Historical Energy: %d from %s to %s", energy, start, end);
    }

}
