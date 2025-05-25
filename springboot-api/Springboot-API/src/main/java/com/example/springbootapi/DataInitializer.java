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

        // 🔁 Aktueller Eintrag für aktuelle Stunde
        double communityPool = ThreadLocalRandom.current().nextDouble(0, 100);
        double gridPortion = 100 - communityPool;
        LocalDateTime hour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0); // gerundete Stunde

        energyCurrentRepository.save(new EnergyCurrent(
                round(communityPool),
                round(gridPortion),
                hour
        ));

        // ⏳ Historische stündliche Daten (wie zuvor)
        for (int dayOffset = 3; dayOffset > 0; dayOffset--) {
            LocalDateTime baseDate = LocalDateTime.now().minusDays(dayOffset).toLocalDate().atStartOfDay();

            for (int h = 0; h < 24; h++) {
                LocalDateTime timestamp = baseDate.plusHours(h);

                energyHistoricalRepository.save(new EnergyHistorical(
                        round(ThreadLocalRandom.current().nextDouble(100.0, 300.0)),
                        round(ThreadLocalRandom.current().nextDouble(100.0, 300.0)),
                        round(ThreadLocalRandom.current().nextDouble(100.0, 300.0)),
                        timestamp
                ));
            }
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}