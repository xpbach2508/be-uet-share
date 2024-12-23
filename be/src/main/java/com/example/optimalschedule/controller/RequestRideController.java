package com.example.optimalschedule.controller;

import com.example.optimalschedule.services.RequestRideService;
import com.example.optimalschedule.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
@RequestMapping("/request")
@PreAuthorize("hasRole('ADMIN')")
public class RequestRideController {

    @Autowired
    private RequestRideService service;

    @GetMapping("/served")
    public ResponseEntity<?> getAllRequestServed() {
        return ResponseEntity.ok(service.getAllServedRequest());
    }
}
