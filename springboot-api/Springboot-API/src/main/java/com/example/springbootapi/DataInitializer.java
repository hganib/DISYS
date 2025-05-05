package com.example.springbootapi;

import com.example.springbootapi.entity.EnergyCurrent;
import com.example.springbootapi.entity.EnergyHistorical;
import com.example.springbootapi.repository.EnergyCurrentRepository;
import com.example.springbootapi.repository.EnergyHistoricalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer {

    private final EnergyCurrentRepository energyCurrentRepository;
    private final EnergyHistoricalRepository energyHistoricalRepository;

    public DataInitializer(EnergyCurrentRepository currentRepo, EnergyHistoricalRepository historicalRepo) {
        this.energyCurrentRepository = currentRepo;
        this.energyHistoricalRepository = historicalRepo;
    }

    @PostConstruct
    public void initData() {

        for (int i = 0; i < 10; i++) {
            double currentPool = ThreadLocalRandom.current().nextDouble(0, 100);
            double gridPortion = 100 - currentPool;

            energyCurrentRepository.save(new EnergyCurrent(
                    Math.round(currentPool * 100.0) / 100.0,
                    Math.round(gridPortion * 100.0) / 100.0,
                    LocalDateTime.now().minusDays(i + 1)
            ));
        }

        for (int i = 10; i > 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            energyHistoricalRepository.save(new EnergyHistorical(
                    Math.round(ThreadLocalRandom.current().nextDouble(1000, 3000) * 100.0) / 100.0,
                    Math.round(ThreadLocalRandom.current().nextDouble(1000, 3000) * 100.0) / 100.0,
                    Math.round(ThreadLocalRandom.current().nextDouble(1000, 3000) * 100.0) / 100.0,
                    start,
                    end
            ));
        }
    }
}