package at.technikum.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Long id;

    @Column(name = "hour", nullable = false, updatable = false) // Timestamp for the hour this record represents
    private LocalDateTime hour;

    @Column(name = "community_pool", nullable = false) // Percentage of energy used from the community pool
    private double communityPool;

    @Column(name = "grid_portion", nullable = false) // Percentage of energy used from the grid
    private double gridPortion;

    public EnergyCurrent() {
    }

    public EnergyCurrent(Long id, LocalDateTime hour, double communityPool, double gridPortion) {
        this.id = id;
        this.hour = hour;
        this.communityPool = communityPool;
        this.gridPortion = gridPortion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
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

    @Override
    public String toString() {
        return "EnergyCurrent{" +
                "id=" + id +
                ", hour=" + hour +
                ", communityPool=" + communityPool +
                ", gridPortion=" + gridPortion +
                '}';
    }
}