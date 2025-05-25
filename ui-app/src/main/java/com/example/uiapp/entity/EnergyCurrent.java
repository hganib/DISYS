package com.example.uiapp.entity;

import java.time.LocalDateTime;

public class EnergyCurrent {
    private double communityPool;
    private double gridPortion;
    private LocalDateTime hour;

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

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {this.hour = hour;}
}
