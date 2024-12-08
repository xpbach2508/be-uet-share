package com.example.optimalschedule.services;

import com.example.optimalschedule.entity.RequestRide;
import com.example.optimalschedule.repository.RequestRideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestRideService {
    @Autowired
    private RequestRideRepository requestRideRepository;

    public List<RequestRide> getAllServedRequest() {
        return requestRideRepository.findByStatusId(1);
    }
}
