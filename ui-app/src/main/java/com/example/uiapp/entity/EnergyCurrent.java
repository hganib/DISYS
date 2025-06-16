package com.example.uiapp.entity;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing current energy usage distribution
 * in the UI application. This class is not managed by JPA and is used solely for
 * transporting data from the Spring Boot API to the UI layer.
 */
public class EnergyCurrent {

    /**
     * Percentage of total energy usage covered by the community pool (e.g., 51.69).
     */
    private double communityPool;

    /**
     * Percentage of total energy usage drawn from the grid (e.g., 48.31).
     */
    private double gridPortion;

    /**
     * The hour (truncated to the full hour) that this record represents.
     * Example: 2025-05-27T15:00
     */
    private LocalDateTime hour;

    /**
     * Retrieves the community usage percentage.
     *
     * @return communityPool as a percentage of total usage
     */
    public double getCommunityPool() {
        return communityPool;
    }

    /**
     * Sets the community usage percentage.
     *
     * @param communityPool percentage value to set
     */
    public void setCommunityPool(double communityPool) {
        this.communityPool = communityPool;
    }

    /**
     * Retrieves the grid usage percentage.
     *
     * @return gridPortion as a percentage of total usage
     */
    public double getGridPortion() {
        return gridPortion;
    }

    /**
     * Sets the grid usage percentage.
     *
     * @param gridPortion percentage value to set
     */
    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }

    /**
     * Retrieves the timestamp (hour) this DTO refers to.
     *
     * @return LocalDateTime truncated to the hour
     */
    public LocalDateTime getHour() {
        return hour;
    }

    /**
     * Sets the timestamp (hour) for this DTO.
     *
     * @param hour LocalDateTime to set (should be truncated to the hour)
     */
    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }
}
