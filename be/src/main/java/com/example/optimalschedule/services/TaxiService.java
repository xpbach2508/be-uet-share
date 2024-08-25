package com.example.optimalschedule.services;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.Taxi;
import com.example.optimalschedule.repository.DriverRepository;
import com.example.optimalschedule.repository.TaxiRepository;
import com.example.optimalschedule.services.IServices.ITaxiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxiService implements ITaxiService {

    @Autowired
    private TaxiRepository taxiRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void createTaxi(Taxi taxi) {
        taxiRepository.save(taxi);
    }

    @Override
    public List<Taxi> getAllTaxiEmpty() {
        return taxiRepository.getAllTaxiEmpty();
    }

    @Override
    public List<Driver> getAllTaxiActive() {
        return driverRepository.findAllByOrderByIdDesc();
    }
}
