package com.example.springbootapi.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * JPA Entity representing the current distribution of energy usage.
 * Persists the percentage split between community and grid usage for each hour.
 */
@Entity
@Table(name = "energy_current")
public class EnergyCurrent {

    @Id // Unique identifier for the energy current record
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Auto-incremented primary key
    private int id;
    @Column(name = "community_pool", nullable = false) // Percentage of energy used from the community pool
    private double communityPool;
    @Column(name = "grid_portion", nullable = false) // Percentage of energy used from the grid
    private double gridPortion;
    @Column(name = "hour", nullable = false, updatable = false) // Timestamp for the hour this record represents
    private LocalDateTime hour;

    public EnergyCurrent() {}

    public EnergyCurrent(double communityPool, double gridPortion, LocalDateTime hour) {
        this.communityPool = communityPool;
        this.gridPortion = gridPortion;
        this.hour = hour;
    }

    public double getCommunityPool() {
        return communityPool;
    }

    public void setCommunityPool(double communityPool) {
        this.communityPool = communityPool;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }

    public LocalDateTime getHour() {return hour;}

    public void setHour(LocalDateTime timestamp) {this.hour = timestamp;}
}
