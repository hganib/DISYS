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
        // 📌 Aktueller Eintrag (z. B. 20:00 Uhr)
        LocalDateTime currentHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        double communityPool = ThreadLocalRandom.current().nextDouble(0, 100);
        double gridPortion = 100 - communityPool;

        energyCurrentRepository.save(new EnergyCurrent(
                round(communityPool),
                round(gridPortion),
                currentHour
        ));

        // 🕒 Historische Daten stündlich (inkl. currentHour)
        LocalDateTime end = currentHour;
        LocalDateTime start = end.minusDays(3).withHour(0);

        for (LocalDateTime timestamp = start; !timestamp.isAfter(end); timestamp = timestamp.plusHours(1)) {
            double communityProduced = round(ThreadLocalRandom.current().nextDouble(100.0, 300.0));
            double hourKwh = generateKwh(timestamp.getHour());
            double totalUsed = round(hourKwh * 300);

            double communityUsed = Math.min(totalUsed, communityProduced);
            double gridUsed = totalUsed - communityUsed;

            energyHistoricalRepository.save(new EnergyHistorical(
                    round(communityProduced),
                    round(communityUsed),
                    round(gridUsed),
                    timestamp
            ));
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static double generateKwh(int hour) {
        // Peak morgens (6–9) & abends (17–21): 0.3 – 1.0
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            return round(ThreadLocalRandom.current().nextDouble(0.3, 1.0));
        }
        // Tagsüber (10–16): 0.1 – 0.4
        else if (hour >= 10 && hour <= 16) {
            return round(ThreadLocalRandom.current().nextDouble(0.1, 0.4));
        }
        // Nachts: 0.01 – 0.1
        else {
            return round(ThreadLocalRandom.current().nextDouble(0.01, 0.1));
        }
    }
}