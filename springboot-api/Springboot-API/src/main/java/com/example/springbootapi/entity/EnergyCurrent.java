package com.example.springbootapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class EnergyCurrent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private double communityPool;
    private double gridPortion;
    private LocalDateTime timestamp;

    public EnergyCurrent() {}

    public EnergyCurrent(double communityPool, double gridPortion, LocalDateTime timestamp) {
        this.communityPool = communityPool;
        this.gridPortion = gridPortion;
        this.timestamp = timestamp;
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

    public LocalDateTime getTimestamp() {return timestamp;}

    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}
}
