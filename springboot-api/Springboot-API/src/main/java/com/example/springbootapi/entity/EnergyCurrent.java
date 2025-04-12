package com.example.springbootapi.entity;

public class EnergyCurrent {
    private double communityPool;
    private double gridPortion;

    public EnergyCurrent(double communityPool, double gridPortion) {
        this.communityPool = communityPool;
        this.gridPortion = gridPortion;
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
}
