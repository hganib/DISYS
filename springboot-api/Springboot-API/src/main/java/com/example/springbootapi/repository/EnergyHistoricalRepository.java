package com.example.springbootapi.repository;

import com.example.springbootapi.entity.EnergyHistorical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for querying historical energy aggregates.
 * Extends Spring Data JPA's JpaRepository to inherit standard CRUD methods.
 */
public interface EnergyHistoricalRepository
    extends JpaRepository<EnergyHistorical, Integer>
    {
        /**
         * Custom JPQL query to sum production, consumption, and grid usage over a date range.
         * <p>
         * The query returns a list of Object arrays, where each array contains three elements:
         * 1. SUM(e.communityProduced)
         * 2. SUM(e.communityUsed)
         * 3. SUM(e.gridUsed)
         * <p>
         * @param start start of the time window (inclusive)
         * @param end   end of the time window (inclusive)
         * @return a list containing a single Object[] with the summed values, or an empty list if no records
         */
        @Query("""
            SELECT SUM(e.communityProduced), SUM(e.communityUsed), SUM(e.gridUsed)
            FROM EnergyHistorical e
            WHERE e.hour >= :start AND e.hour <= :end
        """)
        List<Object[]> sumHistoricalByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    }