package com.example.uiapp.entity;

import java.time.LocalDateTime;

public class EnergyHistorical {
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;
    private LocalDateTime hour;

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

    public LocalDateTime getHour() {return hour;}

    public void setHour(LocalDateTime hour) {this.hour = hour;}

}
