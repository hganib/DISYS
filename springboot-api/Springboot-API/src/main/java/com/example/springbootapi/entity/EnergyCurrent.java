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
    private LocalDateTime hour;

    public EnergyCurrent() {}

    public EnergyCurrent(double communityPool, double gridPortion, LocalDateTime hour) {
        this.communityPool = communityPool;
        this.gridPortion = gridPortion;
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

    public LocalDateTime getHour() {return hour;}

    public void setHour(LocalDateTime timestamp) {this.hour = timestamp;}
}
