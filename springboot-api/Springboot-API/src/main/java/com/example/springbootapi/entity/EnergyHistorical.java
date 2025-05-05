package com.example.springbootapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class EnergyHistorical {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public EnergyHistorical() {}

    public EnergyHistorical(double communityProduced, double communityUsed, double gridUsed, LocalDateTime startTime, LocalDateTime endTime) {
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public LocalDateTime getStartTime() {return startTime;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public LocalDateTime getEndTime() {return endTime;}

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}
}
