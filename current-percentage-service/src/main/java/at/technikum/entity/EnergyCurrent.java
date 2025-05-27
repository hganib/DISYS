package at.technikum.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_current")
public class EnergyCurrent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Stunde (auf volle Stunde gerundet), z. B. 2025-05-26T08:00
     */
    @Column(name = "hour", nullable = false, updatable = false)
    private LocalDateTime hour;

    /** Prozentsatz der Community (z.B. 51.69) */
    @Column(name = "community_pool", nullable = false)
    private double communityPool;

    /** Prozentsatz der Grid-Portion (z.B. 48.31) */
    @Column(name = "grid_portion", nullable = false)
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