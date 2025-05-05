package com.example.springbootapi.repository;

import com.example.springbootapi.entity.EnergyHistorical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyHistoricalRepository
    extends JpaRepository<EnergyHistorical, Integer>
    {
        @Query("SELECT SUM(e.communityProduced), SUM(e.communityUsed), SUM(e.gridUsed) FROM EnergyHistorical e WHERE e.startTime >= :start AND e.endTime <= :end")
        List<Object[]> sumHistoricalByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    }