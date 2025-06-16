package at.technikum;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for sending hourly historical energy updates via RabbitMQ.
 * <p>
 * Contains the timestamp of the hour and aggregated kWh values for community production,
 * community consumption, and grid usage.
 */
public class HistoricalUpdateMessage {
    private LocalDateTime hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;


    public HistoricalUpdateMessage(LocalDateTime hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public HistoricalUpdateMessage() {
        // Default constructor for deserialization
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
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
}