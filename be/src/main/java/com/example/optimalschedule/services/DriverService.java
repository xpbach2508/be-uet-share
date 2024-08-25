package com.example.optimalschedule.services;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.repository.DriverRepository;
import com.example.optimalschedule.services.IServices.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public Driver getDriverById(int driverId) {
        return driverRepository.findById(driverId).orElse(null);
    }
}
