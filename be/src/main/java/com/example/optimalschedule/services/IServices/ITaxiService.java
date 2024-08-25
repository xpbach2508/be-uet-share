package com.example.optimalschedule.services.IServices;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.Taxi;

import java.util.List;

public interface ITaxiService {

    void createTaxi(Taxi taxi);

    List<Taxi> getAllTaxiEmpty();

    List<Driver> getAllTaxiActive();
}
