package com.example.springbootapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnergyController {
    @GetMapping("/hello")
    public String helloWorld() {
        return "DISYS Hello World";
    }
}
