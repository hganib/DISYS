package com.example.uiapp.entity;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing historical aggregated energy data
 * in the UI application. This class is not managed by JPA and is used solely for
 * transporting summed data from the backend API to the UI layer.
 */
public class EnergyHistorical {

    /**
     * Total energy produced by the community during the hour (in kWh).
     */
    private double communityProduced;

    /**
     * Total energy consumed by the community during the hour (in kWh).
     */
    private double communityUsed;

    /**
     * Total energy drawn from the grid during the hour (in kWh), to cover any shortfall.
     */
    private double gridUsed;

    /**
     * The timestamp representing the specific hour covered by this record,
     * truncated to the full hour (e.g., 2025-05-27T14:00).
     */
    private LocalDateTime hour;

    /**
     * Returns the total community-produced energy for the hour.
     *
     * @return communityProduced in kWh
     */
    public double getCommunityProduced() {
        return communityProduced;
    }

    /**
     * Sets the total community-produced energy for the hour.
     *
     * @param communityProduced the produced kWh value to set
     */
    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    /**
     * Returns the total community-consumed energy for the hour.
     *
     * @return communityUsed in kWh
     */
    public double getCommunityUsed() {
        return communityUsed;
    }

    /**
     * Sets the total community-consumed energy for the hour.
     *
     * @param communityUsed the consumed kWh value to set
     */
    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    /**
     * Returns the total grid-drawn energy for the hour.
     *
     * @return gridUsed in kWh
     */
    public double getGridUsed() {
        return gridUsed;
    }

    /**
     * Sets the total grid-drawn energy for the hour.
     *
     * @param gridUsed the grid kWh value to set
     */
    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }

    /**
     * Returns the timestamp (hour) this record applies to.
     *
     * @return LocalDateTime truncated to the hour
     */
    public LocalDateTime getHour() {
        return hour;
    }

    /**
     * Sets the timestamp (hour) for this record.
     *
     * @param hour LocalDateTime value truncated to the full hour
     */
    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }
}