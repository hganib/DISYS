package com.example.springbootapi;

import com.example.springbootapi.entity.EnergyCurrent;
import com.example.springbootapi.entity.EnergyHistorical;
import com.example.springbootapi.repository.EnergyCurrentRepository;
import com.example.springbootapi.repository.EnergyHistoricalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Component that populates the database with sample data at application startup.
 * <p>
 * Inserts one current energy record for the present hour and generates
 * historical hourly records for the past three days.
 */
@Component
public class DataInitializer {

    /** Repository for saving current energy data. */
    private final EnergyCurrentRepository energyCurrentRepository;
    /** Repository for saving historical energy aggregates. */
    private final EnergyHistoricalRepository energyHistoricalRepository;

    /**
     * Injects the required repositories via constructor injection.
     *
     * @param currentRepo    repository for EnergyCurrent entities
     * @param historicalRepo repository for EnergyHistorical entities
     */
    public DataInitializer(EnergyCurrentRepository currentRepo, EnergyHistoricalRepository historicalRepo) {
        this.energyCurrentRepository = currentRepo;
        this.energyHistoricalRepository = historicalRepo;
    }

    /**
     * Method annotated with @PostConstruct runs after bean creation.
     * Generates one current record and a series of historical records.
     */
    @PostConstruct
    public void initData() {
        // Determine the current hour, truncating minutes/seconds/nanos
        LocalDateTime currentHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // Generate a random community vs. grid percentage split
        double communityPool = ThreadLocalRandom.current().nextDouble(0, 100);
        double gridPortion = 100 - communityPool;

        // Save the current energy usage percentages
        energyCurrentRepository.save(new EnergyCurrent(
                round(communityPool),
                round(gridPortion),
                currentHour
        ));

        // Generate historical data for each hour over the past 3 days
        LocalDateTime end = currentHour;
        LocalDateTime start = end.minusDays(3).withHour(0);

        // Loop through each hour from start to end
        for (LocalDateTime timestamp = start; !timestamp.isAfter(end); timestamp = timestamp.plusHours(1)) {

            // Generate random values for community production and usage
            double communityProduced = round(ThreadLocalRandom.current().nextDouble(100.0, 300.0));
            double hourKwh = generateKwh(timestamp.getHour());
            double totalUsed = round(hourKwh * 300);

            // Determine consumption vs. grid usage
            double communityUsed = Math.min(totalUsed, communityProduced);
            double gridUsed = totalUsed - communityUsed;

            // Persist the hourly historical record
            energyHistoricalRepository.save(new EnergyHistorical(
                    round(communityProduced),
                    round(communityUsed),
                    round(gridUsed),
                    timestamp
            ));
        }
    }

    /**
     * Rounds a double to two decimal places.
     *
     * @param value the raw value to round
     * @return the value rounded to 2 decimal places
     */
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Generates a randomized kWh factor based on the hour of day.
     *<p>
     * Peak hours (6–9, 17–21): 0.3 – 1.0 kWh;
     * daytime (10–16): 0.1 – 0.4 kWh;
     * night: 0.01 – 0.1 kWh.
     *
     * @param hour the hour of the day (0–23)
     * @return a kWh value rounded to 2 decimal places
     */
    private static double generateKwh(int hour) {
        // peak hours (6–9, 17–21): 0.3 – 1.0
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            return round(ThreadLocalRandom.current().nextDouble(0.3, 1.0));
        }
        // daytime (10–16): 0.1 – 0.4
        else if (hour >= 10 && hour <= 16) {
            return round(ThreadLocalRandom.current().nextDouble(0.1, 0.4));
        }
        // night (all other hours): 0.01 – 0.1
        else {
            return round(ThreadLocalRandom.current().nextDouble(0.01, 0.1));
        }
    }
}