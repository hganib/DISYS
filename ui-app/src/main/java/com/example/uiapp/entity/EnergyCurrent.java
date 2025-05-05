package com.example.uiapp.entity;

import java.time.LocalDateTime;

public class EnergyCurrent {
    private double communityPool;
    private double gridPortion;
    private LocalDateTime timestamp;

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}
}
