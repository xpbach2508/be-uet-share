package com.example.optimalschedule.controller;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@Controller
@RequestMapping("/driver")
@PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/find_by_id")
    public ResponseEntity<?> findById(@RequestParam("driverId") int driverId) {
        Driver driver = driverService.getDriverById(driverId);
        return ResponseEntity.ok(driver);
    }
}
