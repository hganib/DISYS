package com.example.springbootapi.repository;

import com.example.springbootapi.entity.EnergyCurrent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for accessing EnergyCurrent entities.
 * Provides CRUD operations and custom finder methods for current energy usage data.
 */
public interface EnergyCurrentRepository
    extends JpaRepository<EnergyCurrent, Integer>
    {
        /**
         * Retrieves the most recent EnergyCurrent record based on the 'hour' field.
         * <p>
         * This method uses Spring Data JPA's query derivation mechanism to
         * generate a SQL query equivalent to:
         * SELECT * FROM energy_current ORDER BY hour DESC LIMIT 1;
         *
         * @return the latest EnergyCurrent entity or null if none exist
         */
        EnergyCurrent findTopByOrderByHourDesc();
    }