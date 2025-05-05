package com.example.springbootapi.repository;

import com.example.springbootapi.entity.EnergyCurrent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EnergyCurrentRepository
    extends JpaRepository<EnergyCurrent, Integer>
    {
        EnergyCurrent findTopByOrderByTimestampDesc();
    }