package com.example.optimalschedule.controller;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.Taxi;
import com.example.optimalschedule.model.response.MessageResponse;
import com.example.optimalschedule.services.TaxiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Controller
@RequestMapping("/taxi")
@PreAuthorize("hasRole('ADMIN')")
public class TaxiController {

    @Autowired
    private TaxiService taxiService;

    @PostMapping("/create")
    public ResponseEntity<?> createTaxi(@RequestBody Taxi taxi) {
        taxiService.createTaxi(taxi);
        return ResponseEntity.ok(new MessageResponse("Success"));
    }

    @GetMapping("/taxi_empty")
    public ResponseEntity<?> getAllTaxiEmpty() {
        List<Taxi> taxis = taxiService.getAllTaxiEmpty();
        return ResponseEntity.ok(taxis);
    }

    @GetMapping("/taxi_active")
    public ResponseEntity<?> getAllTaxiActive() {
        List<Driver> taxis = taxiService.getAllTaxiActive();
        return ResponseEntity.ok(taxis);
    }
}
