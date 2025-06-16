package at.technikum.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing hourly aggregates of energy production and consumption.
 * Mapped to the 'energy_historical' table in the database.
 */
@Entity
@Table(name = "energy_historical")
public class EnergyHistorical {

    @Id // Unique identifier for the energy historical record
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private int id;

    @Column(name = "community_produced") // Amount of energy produced by the community in kWh
    private double communityProduced;

    @Column(name = "community_used") // Amount of energy consumed by the community in kWh
    private double communityUsed;

    @Column(name = "grid_used")  // Amount of energy used from the grid in kWh
    private double gridUsed;

    @Column(name = "hour") // Timestamp for the hour this record represents
    private LocalDateTime hour;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }
}